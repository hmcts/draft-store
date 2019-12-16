provider "azurerm" {
  version = "1.39.0"
}

locals {
  s2s_url  = "http://rpe-service-auth-provider-${var.env}.service.core-compute-${var.env}.internal"
}

resource "azurerm_resource_group" "rg" {
  name     = "${var.product}-${var.component}-${var.env}"
  location = "${var.location}"

  tags = "${var.common_tags}"
}

module "db" {
  source             = "git@github.com:hmcts/cnp-module-postgres?ref=master"
  product            = "rpe-${var.product}"
  location           = "${var.location}"
  env                = "${var.env}"
  database_name      = "draftstore"
  postgresql_user    = "draftstore"
  postgresql_version = "10"
  sku_name           = "GP_Gen5_2"
  sku_tier           = "GeneralPurpose"
  common_tags        = "${var.common_tags}"
  subscription       = "${var.subscription}"
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
  resource_group_name = "${azurerm_resource_group.rg.name}"

  # dcd_cc-dev group object ID
  product_group_object_id    = "38f9dea6-e861-4a50-9e73-21e64f563537"
  common_tags                = "${var.common_tags}"
  managed_identity_object_id = "${var.managed_identity_object_id}"
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

resource "azurerm_key_vault_secret" "AZURE_APPINSGHTS_KEY" {
  name         = "AppInsightsInstrumentationKey"
  value        = "${azurerm_application_insights.appinsights.instrumentation_key}"
  key_vault_id = "${module.key-vault.key_vault_id}"
}

# endregion

resource "azurerm_application_insights" "appinsights" {
  name                = "${var.product}-${var.component}-appinsights-${var.env}"
  location            = "${var.appinsights_location}"
  resource_group_name = "${azurerm_resource_group.rg.name}"
  application_type    = "Web"

  tags = "${var.common_tags}"
}
