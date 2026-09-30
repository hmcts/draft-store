# Preserve the existing database module address outside sandbox.
moved {
  from = module.postgresql
  to   = module.postgresql[0]
}
