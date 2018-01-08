resource "random_string" "password" {
  length = 32
  special = true
}

module "api" {
  source   = "git@github.com/hmcts/terraform-module-webapp.git"
  product  = "${var.product}-api"
  location = "${var.location_api}"
  env      = "${var.env}"
  ilbIp    = "${var.ilbIp}"

  app_settings = {
    DRAFT_STORE_DB_HOST     = "${module.db.host_name}"
    DRAFT_STORE_DB_PORT     = "${module.db.postgresql_listen_port}"
    DRAFT_STORE_DB_PASSWORD = "${random_string.password.result}"

    IDAM_URL                = "http://idam-${var.env}.service.${data.terraform_remote_state.core_apps_compute.ase_name[0]}.internal"
    S2S_URL                 = "http://idam-s2s-${var.env}.service.${data.terraform_remote_state.core_apps_compute.ase_name[0]}.internal"

    MAX_STALE_DAYS_DEFAULT  = "${var.max_stale_days_default}"
    MAX_STALE_DAYS_CRON     = "${var.max_stale_days_cron}"
  }
}

module "db" {
  source              = "git@github.com/hmcts/terraform-module-postgres.git"
  product             = "${var.product}-db"
  location            = "${var.location_api}"
  env                 = "${var.env}"
  postgresql_user     = "draftstore"
  postgresql_password = "${random_string.password.result}"
}
