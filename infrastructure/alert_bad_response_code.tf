module "bad-response-codes-alert" {
  source            = "git@github.com:hmcts/cnp-module-metric-alert"
  location          = var.location
  app_insights_name = "${var.product}-${var.component}-appinsights-${var.env}"

  alert_name = "Draft-store-bad-response-codes"
  alert_desc = "404 for /health and 5xx response codes"

  app_insights_query = <<EOF
requests
| where ((name == "GET /health" and resultCode == "404") or resultCode startswith "5")
| where operation_SyntheticSource !endswith "smoke test"
EOF

  frequency_in_minutes       = "5"
  time_window_in_minutes     = "5"
  severity_level             = "3"
  action_group_name          = "RPE alerts - ${var.env}"
  action_group_rg            = "rpe-${var.env}"
  custom_email_subject       = "Draft store - bad response codes detected"
  trigger_threshold_operator = "GreaterThan"
  trigger_threshold          = "0"
  resourcegroup_name         = azurerm_resource_group.rg.name
  common_tags                = var.common_tags

}
