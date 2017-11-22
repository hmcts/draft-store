module "api" {
  source   = "git@github.com/hmcts/terraform-module-webapp.git"
  product  = "${var.product}"
  location = "${var.location}"
  env      = "${var.env}"
  asename  = "${data.terraform_remote_state.core_apps_compute.ase_name[0]}"

  app_settings = {
    DRAFT_STORE_DB_HOST     = "${module.db.host_name}"
    DRAFT_STORE_DB_PORT     = "${module.db.postgresql_listen_port}"
    DRAFT_STORE_DB_PASSWORD = "${var.db_password}"

    IDAM_URL                = "${var.idam_url}"
    IDAM_USE_STUB           = "${var.idam_use_stub}"
    S2S_URL                 = "${var.s2s_url}"
    S2S_USER_STUB           = "${var.s2s_use_stub}"

    MAX_STALE_DAYS_DEFAULT  = "${var.max_stale_days_default}"
    MAX_STALE_DAYS_CRON     = "${var.max_stale_days_cron}"
  }
}

module "db" {
  source              = "git@github.com/hmcts/terraform-module-postgres.git"
  product             = "${var.product}"
  location            = "${var.location}"
  env                 = "${var.env}"
  postgresql_user     = "draftstore"
  postgresql_password = "${var.db_password}"
}
