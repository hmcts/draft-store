output "vaultUri" {
  value = "${module.key-vault.key_vault_uri}"
}

output "vaultName" {
  value = "${module.key-vault.key_vault_name}"
}

output "microserviceName" {
  value = "${var.component}"
}

# region configuration for end-to-end tests

output "s2s_url_for_tests" {
  value = "${local.s2s_url}"
}

output "s2s_name_for_tests" {
  value = "send_letter_tests"
}

output "idam_url_for_tests" {
  value = "${var.idam_api_url}"
}

output "idam_user_email_for_tests" {
  value = "reformplatformengineering+tests@gmail.com"
}

// TODO: change the client to a dedicated test client (https://tools.hmcts.net/jira/browse/RPE-412)
output "idam_client_id_for_tests" {
  value = "cmc_citizen"
}

// One of the whitelisted URLs - tests need to use it in order to log user in
output "idam_redirect_uri_for_tests" {
  value = "https://cmc-citizen-frontend-aat-staging.service.core-compute-aat.internal/receiver"
}

# endregion
