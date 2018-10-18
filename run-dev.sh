#!/usr/bin/env sh

export S2S_USER_STUB=true
export IDAM_USE_STUB=true

./gradlew assemble && docker-compose build draft-store-api && docker-compose up
