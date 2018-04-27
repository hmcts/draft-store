provider "vault" {
  address = "https://vault.reform.hmcts.net:6200"
}

locals {
  db_connection_options  = "?ssl=true"
  ase_name               = "${data.terraform_remote_state.core_apps_compute.ase_name[0]}"

  # Environment we take our dependencies from. Typically the same as for the app,
  # but has to be different for preview
  dependencies_env       = "${var.env == "preview"
                                ? "aat"
                                : var.env == "spreview" ? "saat" : var.env
                             }"

  dependencies_ase       = "${var.env == "preview"
                                ? "core-compute-aat"
                                : var.env == "spreview" ? "core-compute-saat" : local.ase_name
                             }"

  s2s_url                = "http://rpe-service-auth-provider-${local.dependencies_env}.service.${local.dependencies_ase}.internal"

  preview_vault_name     = "draft-store-aat"
  default_vault_name     = "${var.product}-${var.env}"
  vault_name             = "${(var.env == "preview" || var.env == "spreview")
                                 ? local.preview_vault_name
                                 : local.default_vault_name
                             }"

  non_preview_vault_uri  = "${module.key-vault.key_vault_uri}"

  # use aat vault so that there's no need to populate it with data every time a PR is created
  preview_vault_uri      = "https://draft-store-aat.vault.azure.net/"
  vault_uri              = "${(var.env == "preview" || var.env == "spreview")
                                 ? local.preview_vault_uri
                                 : local.non_preview_vault_uri
                             }"
}

module "db" {
  source              = "git@github.com:hmcts/moj-module-postgres"
  product             = "${var.product}-db"
  location            = "${var.location_db}"
  env                 = "${var.env}"
  postgresql_user     = "draftstore"
}

module "api" {
  source        = "git@github.com:hmcts/moj-module-webapp"
  product       = "${var.product}-${var.component}"
  location      = "${var.location_api}"
  env           = "${var.env}"
  ilbIp         = "${var.ilbIp}"
  subscription  = "${var.subscription}"

  app_settings = {
    DRAFT_STORE_DB_HOST         = "${module.db.host_name}"
    DRAFT_STORE_DB_PORT         = "${module.db.postgresql_listen_port}"
    DRAFT_STORE_DB_PASSWORD     = "${module.db.postgresql_password}"
    DRAFT_STORE_DB_USER_NAME    = "${module.db.user_name}"
    DRAFT_STORE_DB_NAME         = "${module.db.postgresql_database}"
    DRAFT_STORE_DB_CONN_OPTIONS = "${local.db_connection_options}"

    IDAM_URL                    = "${var.idam_api_url}"
    S2S_URL                     = "${local.s2s_url}"

    MAX_STALE_DAYS_DEFAULT      = "${var.max_stale_days_default}"
    MAX_STALE_DAYS_CRON         = "${var.max_stale_days_cron}"

    FLYWAY_URL                  = "jdbc:postgresql://${module.db.host_name}:${module.db.postgresql_listen_port}/${module.db.postgresql_database}${local.db_connection_options}"
    FLYWAY_USER                 = "${module.db.user_name}"
    FLYWAY_PASSWORD             = "${module.db.postgresql_password}"

    RUN_DB_MIGRATION_ON_STARTUP = "${var.run_db_migration_on_startup}"

    LOGBACK_REQUIRE_ALERT_LEVEL = "false"
    LOGBACK_REQUIRE_ERROR_CODE  = "false"
  }
}

# region save DB details to Azure Key Vault

# this key vault is created in every environment, but preview, being short-lived,
# will use the aat one instead
module "key-vault" {
  source              = "git@github.com:hmcts/moj-module-key-vault?ref=master"
  name                = "${local.vault_name}"
  product             = "${var.product}"
  env                 = "${var.env}"
  tenant_id           = "${var.tenant_id}"
  object_id           = "${var.jenkins_AAD_objectId}"
  resource_group_name = "${module.api.resource_group_name}"
  # dcd_cc-dev group object ID
  product_group_object_id = "38f9dea6-e861-4a50-9e73-21e64f563537"
  count               = "${(var.env == "preview" || var.env == "spreview") ? 0 : 1}"
}

resource "azurerm_key_vault_secret" "POSTGRES-USER" {
  name      = "${var.component}-POSTGRES-USER"
  value     = "${module.db.user_name}"
  vault_uri = "${local.vault_uri}"
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS" {
  name      = "${var.component}-POSTGRES-PASS"
  value     = "${module.db.postgresql_password}"
  vault_uri = "${local.vault_uri}"
}

resource "azurerm_key_vault_secret" "POSTGRES_HOST" {
  name      = "${var.component}-POSTGRES-HOST"
  value     = "${module.db.host_name}"
  vault_uri = "${local.vault_uri}"
}

resource "azurerm_key_vault_secret" "POSTGRES_PORT" {
  name      = "${var.component}-POSTGRES-PORT"
  value     = "${module.db.postgresql_listen_port}"
  vault_uri = "${local.vault_uri}"
}

resource "azurerm_key_vault_secret" "POSTGRES_DATABASE" {
  name      = "${var.component}-POSTGRES-DATABASE"
  value     = "${module.db.postgresql_database}"
  vault_uri = "${local.vault_uri}"
}
# endregion
