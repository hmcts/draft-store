variable "product" {
}

variable "component" {
}

variable "location" {
  default = "UK South"
}

variable "env" {
}

variable "tenant_id" {}

variable "jenkins_AAD_objectId" {
  description = "(Required) The Azure AD object ID of a user, service principal or security group in the Azure Active Directory tenant for the vault. The object ID must be unique for the list of access policies."
}

variable "subscription" {}

variable "common_tags" {
  type = map(string)
}

# region app config

variable "idam_api_url" {}

variable "use_idam_testing_support" {
  default = true
}

# endregion

# region test config

# redirect-uri to be used by end-to-end tests when signing user in to Idam (must be whitelisted)
variable "idam_redirect_uri_for_tests" {
  default = "https://cmc-citizen-frontend-saat-staging.service.core-compute-saat.internal/receiver"
}

# endregion

variable "appinsights_location" {
  default     = "West Europe"
  description = "Location for Application Insights"
}
