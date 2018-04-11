variable "product" {
  type    = "string"
  default = "draft-store"
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

variable "ilbIp" {}

variable "subscription" {}

# region app config

variable "max_stale_days_default" {
  default = 90
}

variable "max_stale_days_cron" {
  default = "0 0 3 * * *"
}

# endregion
