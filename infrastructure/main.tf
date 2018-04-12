locals {
  db_connection_options = "?ssl=true"
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

    IDAM_URL                    = "http://betaDevBccidamAppLB.reform.hmcts.net:4551"
    S2S_URL                     = "http://betaDevBccidamAppLB.reform.hmcts.net:4552"

    MAX_STALE_DAYS_DEFAULT      = "${var.max_stale_days_default}"
    MAX_STALE_DAYS_CRON         = "${var.max_stale_days_cron}"

    FLYWAY_URL                  = "jdbc:postgresql://${module.db.host_name}:${module.db.postgresql_listen_port}/${module.db.postgresql_database}${local.db_connection_options}"
    FLYWAY_USER                 = "${module.db.user_name}"
    FLYWAY_PASSWORD             = "${module.db.postgresql_password}"
  }
}

# region save DB details to Azure Key Vault
module "key-vault" {
  source              = "git@github.com:hmcts/moj-module-key-vault?ref=master"
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
  value     = "${module.db.user_name}"
  vault_uri = "${module.key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS" {
  name      = "${var.component}-POSTGRES-PASS"
  value     = "${module.db.postgresql_password}"
  vault_uri = "${module.key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "POSTGRES_HOST" {
  name      = "${var.component}-POSTGRES-HOST"
  value     = "${module.db.host_name}"
  vault_uri = "${module.key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "POSTGRES_PORT" {
  name      = "${var.component}-POSTGRES-PORT"
  value     = "${module.db.postgresql_listen_port}"
  vault_uri = "${module.key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "POSTGRES_DATABASE" {
  name      = "${var.component}-POSTGRES-DATABASE"
  value     = "${module.db.postgresql_database}"
  vault_uri = "${module.key-vault.key_vault_uri}"
}
# endregion
