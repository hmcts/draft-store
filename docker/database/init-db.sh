#!/usr/bin/env bash

set -e

# Claim Store database
if [ -z "$DRAFT_STORE_DB_PASSWORD" ]; then
  echo "ERROR: Missing environment variables. Set value for 'DRAFT_STORE_DB_PASSWORD'."
  exit 1
fi

psql -v ON_ERROR_STOP=1 --username postgres --set USERNAME=draftstore --set PASSWORD=${DRAFT_STORE_DB_PASSWORD} <<-EOSQL
  CREATE USER :USERNAME WITH PASSWORD :'PASSWORD';

  CREATE DATABASE draftstore
    WITH OWNER = :USERNAME
    ENCODING = 'UTF-8'
    CONNECTION LIMIT = -1;
EOSQL
