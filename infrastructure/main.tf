provider "azurerm" {
  features {}
}

provider "azurerm" {
  subscription_id            = local.cft_vnet[local.env].subscription
  skip_provider_registration = "true"
  features {}
  alias = "cft_vnet"
}

locals {
  s2s_url = "http://rpe-service-auth-provider-${var.env}.service.core-compute-${var.env}.internal"

  env = var.env == "sandbox" ? "sbox" : var.env

  cft_vnet = {
    sbox = {
      subscription = "b72ab7b7-723f-4b18-b6f6-03b0f2c6a1bb"
    }
    perftest = {
      subscription = "8a07fdcd-6abd-48b3-ad88-ff737a4b9e3c"
    }
    aat = {
      subscription = "96c274ce-846d-4e48-89a7-d528432298a7"
    }
    ithc = {
      subscription = "62864d44-5da9-4ae9-89e7-0cf33942fa09"
    }
    preview = {
      subscription = "8b6ea922-0862-443e-af15-6056e1c9b9a4"
    }
    prod = {
      subscription = "8cbc6f36-7c56-4963-9d36-739db5d00b27"
    }
    demo = {
      subscription = "d025fece-ce99-4df2-b7a9-b649d3ff2060"
    }
  }
}


resource "azurerm_resource_group" "rg" {
  name     = "${var.product}-${var.component}-${var.env}"
  location = var.location

  tags = var.common_tags
}

# FlexibleServer v14
module "postgresql" {
  providers = {
    azurerm.postgres_network = azurerm.cft_vnet
  }

  source = "git@github.com:hmcts/terraform-module-postgresql-flexible?ref=master"
  env    = var.env

  product       = var.product
  component     = var.component
  business_area = "cft"


  common_tags = var.common_tags
  name        = "rpe-${var.product}-v14"
  pgsql_databases = [
    {
      name : "draftstore"
    }
  ]

  pgsql_version = var.pgsql_version
  create_mode   = var.pgsql_create_mode

  admin_user_object_id = var.jenkins_AAD_objectId
}

data "azurerm_user_assigned_identity" "rpe-shared-identity" {
  name                = "rpe-shared-${var.env}-mi"
  resource_group_name = "managed-identities-${var.env}-rg"
}

# region save DB details to Azure Key Vault

# this key vault is created in every environment, but preview, being short-lived,
# will use the aat one instead
module "key-vault" {
  source              = "git@github.com:hmcts/cnp-module-key-vault?ref=master"
  product             = var.product
  env                 = var.env
  tenant_id           = var.tenant_id
  object_id           = var.jenkins_AAD_objectId
  resource_group_name = azurerm_resource_group.rg.name

  # dcd_cc-dev group object ID
  product_group_object_id     = "38f9dea6-e861-4a50-9e73-21e64f563537"
  common_tags                 = var.common_tags
  managed_identity_object_ids = [data.azurerm_user_assigned_identity.rpe-shared-identity.principal_id]
}

# FlexibleServer v14 creds
resource "azurerm_key_vault_secret" "POSTGRES-USER" {
  name         = "${var.component}-POSTGRES-USER"
  value        = module.postgresql.username
  key_vault_id = module.key-vault.key_vault_id
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS" {
  name         = "${var.component}-POSTGRES-PASS"
  value        = module.postgresql.password
  key_vault_id = module.key-vault.key_vault_id
}

resource "azurerm_key_vault_secret" "POSTGRES_HOST" {
  name         = "${var.component}-POSTGRES-HOST"
  value        = module.postgresql.fqdn
  key_vault_id = module.key-vault.key_vault_id
}

resource "azurerm_key_vault_secret" "POSTGRES_PORT" {
  name         = "${var.component}-POSTGRES-PORT"
  value        = "5432"
  key_vault_id = module.key-vault.key_vault_id
}

resource "azurerm_key_vault_secret" "POSTGRES_DATABASE" {
  name         = "${var.component}-POSTGRES-DATABASE"
  value        = "draftstore"
  key_vault_id = module.key-vault.key_vault_id
}




resource "azurerm_key_vault_secret" "POSTGRES-USER-V14" {
  name         = "${var.component}-POSTGRES-USER-V14"
  value        = module.postgresql.username
  key_vault_id = module.key-vault.key_vault_id
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS-V14" {
  name         = "${var.component}-POSTGRES-PASS-V14"
  value        = module.postgresql.password
  key_vault_id = module.key-vault.key_vault_id
}

resource "azurerm_key_vault_secret" "POSTGRES_HOST-V14" {
  name         = "${var.component}-POSTGRES-HOST-V14"
  value        = module.postgresql.fqdn
  key_vault_id = module.key-vault.key_vault_id
}

resource "azurerm_key_vault_secret" "POSTGRES_PORT-V14" {
  name         = "${var.component}-POSTGRES-PORT-V14"
  value        = "5432"
  key_vault_id = module.key-vault.key_vault_id
}

resource "azurerm_key_vault_secret" "POSTGRES_DATABASE-V14" {
  name         = "${var.component}-POSTGRES-DATABASE-V14"
  value        = "draftstore"
  key_vault_id = module.key-vault.key_vault_id
}

# endregion
