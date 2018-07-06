provider "vault" {
  address = "https://vault.reform.hmcts.net:6200"
}

data "vault_generic_secret" "db_password" {
  path = "secret/${var.vault_section}/cmc/draft-store/database/password"
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

  preview_vault_name     = "${var.product}"
  default_vault_name     = "${var.product}-${var.env}"
  vault_name             = "${(var.env == "preview" || var.env == "spreview")
                                 ? local.preview_vault_name
                                 : local.default_vault_name
                             }"
}

module "api" {
  source        = "git@github.com:hmcts/moj-module-webapp"
  product       = "${var.product}-${var.component}"
  location      = "${var.location_api}"
  env           = "${var.env}"
  ilbIp         = "${var.ilbIp}"
  subscription  = "${var.subscription}"
  capacity      = "${var.capacity}"
  common_tags   = "${var.common_tags}"

  app_settings = {
    DRAFT_STORE_DB_HOST         = "${var.db_host}"
    DRAFT_STORE_DB_PORT         = "5432"
    DRAFT_STORE_DB_PASSWORD     = "${data.vault_generic_secret.db_password.data["value"]}"
    DRAFT_STORE_DB_USER_NAME    = "draftstore"
    DRAFT_STORE_DB_NAME         = "draftstore"
    DRAFT_STORE_DB_CONN_OPTIONS = "${local.db_connection_options}"

    IDAM_URL                    = "${var.idam_api_url}"
    S2S_URL                     = "${local.s2s_url}"

    MAX_STALE_DAYS_DEFAULT      = "${var.max_stale_days_default}"
    MAX_STALE_DAYS_CRON         = "${var.max_stale_days_cron}"

    FLYWAY_URL                  = "jdbc:postgresql://${var.db_host}:5432/draftstore${local.db_connection_options}"
    FLYWAY_USER                 = "draftstore"
    FLYWAY_PASSWORD             = "${data.vault_generic_secret.db_password.data["value"]}"

    RUN_DB_MIGRATION_ON_STARTUP = "${var.run_db_migration_on_startup}"

    LOGBACK_REQUIRE_ALERT_LEVEL = "false"
    LOGBACK_REQUIRE_ERROR_CODE  = "false"
  }
}

module "db" {
  source              = "git@github.com:hmcts/moj-module-postgres?ref=master"
  product             = "${var.product}-db"
  location            = "${var.location_api}"
  env                 = "${var.env}"
  database_name       = "draftstore"
  postgresql_user     = "draftstore"
  sku_name            = "GP_Gen5_2"
  sku_tier            = "GeneralPurpose"
  common_tags         = "${var.common_tags}"
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
}

resource "azurerm_key_vault_secret" "POSTGRES-USER" {
  name      = "${var.component}-POSTGRES-USER"
  value     = "draftstore"
  vault_uri = "${module.key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS" {
  name      = "${var.component}-POSTGRES-PASS"
  value     = "${data.vault_generic_secret.db_password.data["value"]}"
  vault_uri = "${module.key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "POSTGRES_HOST" {
  name      = "${var.component}-POSTGRES-HOST"
  value     = "${var.db_host}"
  vault_uri = "${module.key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "POSTGRES_PORT" {
  name      = "${var.component}-POSTGRES-PORT"
  value     = "5432"
  vault_uri = "${module.key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "POSTGRES_DATABASE" {
  name      = "${var.component}-POSTGRES-DATABASE"
  value     = "draftstore"
  vault_uri = "${module.key-vault.key_vault_uri}"
}
# endregion
