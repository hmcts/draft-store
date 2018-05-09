# configuration for end-to-end tests

// TODO: use the draft store test microservice once https://github.com/hmcts/service-auth-provider-app/pull/27 is merged
data "vault_generic_secret" "tests_s2s_secret" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/send-letter-tests"
}

data "vault_generic_secret" "test_idam_client_secret" {
  path = "secret/${var.vault_section}/ccidam/idam-api/oauth2/client-secrets/cmc-citizen"
}

resource "azurerm_key_vault_secret" "s2s-secret-for-tests" {
  name      = "s2s-secret-for-tests"
  value     = "${data.vault_generic_secret.tests_s2s_secret.data["value"]}"
  vault_uri = "${module.key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "idam-client-secret-for-tests" {
  name      = "idam-client-secret-for-tests"
  value     = "${data.vault_generic_secret.test_idam_client_secret.data["value"]}"
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
