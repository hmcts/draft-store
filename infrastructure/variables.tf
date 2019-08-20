variable "product" {
  type = "string"
}
variable "enable_ase" {
  default = true
}

variable "raw_product" {
  default = "draft-store" // jenkins-library overrides product for PRs and adds e.g. pr-118-bulk-scan
}

variable "component" {
  type = "string"
}

variable "location_api" {
  type    = "string"
  default = "UK South"
}

variable "env" {
  type = "string"
}

# pr-###- in preview environment. Empty elsewhere.
variable "deployment_namespace" {
  type = "string"
}

variable "ilbIp" {}

variable "tenant_id" {}

variable "client_id" {
  description = "(Required) The object ID of a user, service principal or security group in the Azure Active Directory tenant for the vault. The object ID must be unique for the list of access policies. This is usually sourced from environment variables and not normally required to be specified."
}

variable "jenkins_AAD_objectId" {
  type        = "string"
  description = "(Required) The Azure AD object ID of a user, service principal or security group in the Azure Active Directory tenant for the vault. The object ID must be unique for the list of access policies."
}

variable "subscription" {}

variable "capacity" {
  default = "1"
}

variable "common_tags" {
  type = "map"
}

# region app config

variable "idam_api_url" {}

variable "max_stale_days_default" {
  default = 90
}

variable "max_stale_days_cron" {
  default = "0 0 3 * * *"
}

variable "run_db_migration_on_startup" {
  default = "false"
}

variable "use_idam_testing_support" {
  default = "true"
}

# endregion

# region test config

# redirect-uri to be used by end-to-end tests when signing user in to Idam (must be whitelisted)
variable "idam_redirect_uri_for_tests" {
  default = "https://cmc-citizen-frontend-saat-staging.service.core-compute-saat.internal/receiver"
}

# endregion

variable "managed_identity_object_id" {
  default = ""
}
