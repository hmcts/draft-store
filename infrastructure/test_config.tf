# configuration for end-to-end tests

resource "random_string" "idam_password" {
  length = 16
  special = false
}

# This is set only for environments where IdAM testing support is on.
# In other environments (e.g. prod) real IdAM password has to be manually set in Azure Vault
resource "azurerm_key_vault_secret" "idam_password_for_tests" {
  name      = "idam-password-for-tests"
  value     = "${random_string.idam_password.result}"
  vault_uri = "${module.key-vault.key_vault_uri}"
  count     = "${var.use_idam_testing_support == "true" ? 1 : 0}"
}
