module "db" {
  source              = "git@github.com:hmcts/moj-module-postgres"
  product             = "${var.product}-db"
  location            = "${var.location_db}"
  env                 = "${var.env}"
  postgresql_user     = "draftstore"
}

module "api" {
  source        = "git@github.com:hmcts/moj-module-webapp"
  product       = "${var.product}-${var.microservice}"
  location      = "${var.location_api}"
  env           = "${var.env}"
  ilbIp         = "${var.ilbIp}"
  subscription  = "${var.subscription}"

  app_settings = {
    DRAFT_STORE_DB_HOST     = "${module.db.host_name}"
    DRAFT_STORE_DB_PORT     = "${module.db.postgresql_listen_port}"
    DRAFT_STORE_DB_PASSWORD = "${module.db.postgresql_password}"

    IDAM_URL                = "http://betaDevBccidamAppLB.reform.hmcts.net:4551"
    S2S_URL                 = "http://betaDevBccidamAppLB.reform.hmcts.net:4552"

    MAX_STALE_DAYS_DEFAULT  = "${var.max_stale_days_default}"
    MAX_STALE_DAYS_CRON     = "${var.max_stale_days_cron}"
  }
}
