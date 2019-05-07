provider "azurerm" {
  version = "1.22.1"
}

locals {
  db_connection_options = "?sslmode=require"
  ase_name              = "${data.terraform_remote_state.core_apps_compute.ase_name[0]}"

  s2s_url  = "http://rpe-service-auth-provider-${var.env}.service.${local.ase_name}.internal"
  sku_size = "${var.env == "prod" || var.env == "sprod" || var.env == "aat" ? "I2" : "I1"}"
}

module "api" {
  source        = "git@github.com:hmcts/cnp-module-webapp"
  product       = "${var.product}-${var.component}"
  location      = "${var.location_api}"
  env           = "${var.env}"
  ilbIp         = "${var.ilbIp}"
  subscription  = "${var.subscription}"
  capacity      = "${var.capacity}"
  common_tags   = "${var.common_tags}"
  asp_name      = "${var.product}-${var.component}-${var.env}"
  asp_rg        = "${var.product}-${var.component}-${var.env}"
  instance_size = "${local.sku_size}"

  app_settings = {
    DRAFT_STORE_DB_HOST         = "${module.db.host_name}"
    DRAFT_STORE_DB_PORT         = "5432"
    DRAFT_STORE_DB_PASSWORD     = "${module.db.postgresql_password}"
    DRAFT_STORE_DB_USER_NAME    = "${module.db.user_name}"
    DRAFT_STORE_DB_NAME         = "${module.db.postgresql_database}"
    DRAFT_STORE_DB_CONN_OPTIONS = "${local.db_connection_options}"

    IDAM_URL = "${var.idam_api_url}"
    S2S_URL  = "${local.s2s_url}"

    MAX_STALE_DAYS_DEFAULT = "${var.max_stale_days_default}"
    MAX_STALE_DAYS_CRON    = "${var.max_stale_days_cron}"

    RUN_DB_MIGRATION_ON_STARTUP = "${var.run_db_migration_on_startup}"
  }
}

module "db" {
  source             = "git@github.com:hmcts/cnp-module-postgres?ref=master"
  product            = "rpe-${var.product}"
  location           = "${var.location_api}"
  env                = "${var.env}"
  database_name      = "draftstore"
  postgresql_user    = "draftstore"
  postgresql_version = "10"
  sku_name           = "GP_Gen5_2"
  sku_tier           = "GeneralPurpose"
  common_tags        = "${var.common_tags}"
}

# region save DB details to Azure Key Vault

# this key vault is created in every environment, but preview, being short-lived,
# will use the aat one instead
module "key-vault" {
  source              = "git@github.com:hmcts/cnp-module-key-vault?ref=master"
  product             = "${var.product}"
  env                 = "${var.env}"
  tenant_id           = "${var.tenant_id}"
  object_id           = "${var.jenkins_AAD_objectId}"
  resource_group_name = "${module.api.resource_group_name}"

  # dcd_cc-dev group object ID
  product_group_object_id = "38f9dea6-e861-4a50-9e73-21e64f563537"
  common_tags             = "${var.common_tags}"
}

resource "azurerm_key_vault_secret" "POSTGRES-USER" {
  name         = "${var.component}-POSTGRES-USER"
  value        = "${module.db.user_name}"
  key_vault_id = "${module.key-vault.key_vault_id}"
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS" {
  name         = "${var.component}-POSTGRES-PASS"
  value        = "${module.db.postgresql_password}"
  key_vault_id = "${module.key-vault.key_vault_id}"
}

resource "azurerm_key_vault_secret" "POSTGRES_HOST" {
  name         = "${var.component}-POSTGRES-HOST"
  value        = "${module.db.host_name}"
  key_vault_id = "${module.key-vault.key_vault_id}"
}

resource "azurerm_key_vault_secret" "POSTGRES_PORT" {
  name         = "${var.component}-POSTGRES-PORT"
  value        = "5432"
  key_vault_id = "${module.key-vault.key_vault_id}"
}

resource "azurerm_key_vault_secret" "POSTGRES_DATABASE" {
  name         = "${var.component}-POSTGRES-DATABASE"
  value        = "${module.db.postgresql_database}"
  key_vault_id = "${module.key-vault.key_vault_id}"
}

# endregion

