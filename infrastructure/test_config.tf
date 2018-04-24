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
