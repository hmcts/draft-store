# configuration for end-to-end tests

# Secrets for tests are stored in permanent (long-lived) Azure Key Vault instances.
# With the exception of (s)preview all Vault instances are long-lived. For preview, however,
# test secrets (not created during deployment) need to be copied over from a permanent vault -
# that's what the code below does.
data "azurerm_key_vault_secret" "source_s2s-secret-for-tests" {
  name      = "s2s-secret-for-tests"
  vault_uri = "${local.permanent_vault_uri}"
}

resource "azurerm_key_vault_secret" "s2s-secret-for-tests" {
  name      = "s2s-secret-for-tests"
  value     = "${data.azurerm_key_vault_secret.source_s2s-secret-for-tests.value}"
  vault_uri = "${module.key-vault.key_vault_uri}"
}

data "azurerm_key_vault_secret" "source_idam-client-secret-for-tests" {
  name      = "idam-client-secret-for-tests"
  vault_uri = "${local.permanent_vault_uri}"
}

resource "azurerm_key_vault_secret" "idam-client-secret-for-tests" {
  name      = "idam-client-secret-for-tests"
  value     = "${data.azurerm_key_vault_secret.source_idam-client-secret-for-tests.value}"
  vault_uri = "${module.key-vault.key_vault_uri}"
}

#region IdAM test user's password

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

#endregion
