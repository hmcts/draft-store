variable "product" {
  type    = "string"
  default = "draft-store"
}

variable "location" {
  type    = "string"
  default = "UK South"
}

variable "env" {
  type = "string"
}

variable "infrastructure_env" {
  default     = "dev"
  description = "Infrastructure environment to point to"
}

// region app config

variable "db_password" {}

variable "idam_url" {}
variable "idam_use_stub" {}

variable "s2s_url" {}
variable "s2s_use_stub" {}

variable "max_stale_days_default" {}
variable "max_stale_days_cron" {}

// endregion
