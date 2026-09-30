# Preserve existing database and secret addresses outside sandbox.
moved {
  from = module.postgresql
  to   = module.postgresql[0]
}

moved {
  from = azurerm_key_vault_secret.POSTGRES-USER
  to   = azurerm_key_vault_secret.POSTGRES-USER[0]
}

moved {
  from = azurerm_key_vault_secret.POSTGRES-PASS
  to   = azurerm_key_vault_secret.POSTGRES-PASS[0]
}

moved {
  from = azurerm_key_vault_secret.POSTGRES_HOST
  to   = azurerm_key_vault_secret.POSTGRES_HOST[0]
}

moved {
  from = azurerm_key_vault_secret.POSTGRES_PORT
  to   = azurerm_key_vault_secret.POSTGRES_PORT[0]
}

moved {
  from = azurerm_key_vault_secret.POSTGRES_DATABASE
  to   = azurerm_key_vault_secret.POSTGRES_DATABASE[0]
}

moved {
  from = azurerm_key_vault_secret.POSTGRES-USER-V14
  to   = azurerm_key_vault_secret.POSTGRES-USER-V14[0]
}

moved {
  from = azurerm_key_vault_secret.POSTGRES-PASS-V14
  to   = azurerm_key_vault_secret.POSTGRES-PASS-V14[0]
}

moved {
  from = azurerm_key_vault_secret.POSTGRES_HOST-V14
  to   = azurerm_key_vault_secret.POSTGRES_HOST-V14[0]
}

moved {
  from = azurerm_key_vault_secret.POSTGRES_PORT-V14
  to   = azurerm_key_vault_secret.POSTGRES_PORT-V14[0]
}

moved {
  from = azurerm_key_vault_secret.POSTGRES_DATABASE-V14
  to   = azurerm_key_vault_secret.POSTGRES_DATABASE-V14[0]
}
