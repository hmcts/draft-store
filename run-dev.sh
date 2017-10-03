#!/usr/bin/env sh

export DRAFT_STORE_DB_PASSWORD=test
export DRAFT_STORE_DB_PORT=5432
export S2S_USER_STUB=true
export IDAM_USE_STUB=true

./gradlew clean installDist && docker-compose build && docker-compose up
