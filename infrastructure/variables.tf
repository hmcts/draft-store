variable "product" {
  type    = "string"
}

variable "component" {
  type = "string"
}

variable "location_api" {
  type    = "string"
  default = "UK South"
}

variable "location_db" {
  type    = "string"
  default = "West Europe"
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

variable "vault_section" {
  default = "test"
}

# region app config

variable "idam_api_url" {
  default = "http://betaDevBccidamAppLB.reform.hmcts.net"
}

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
