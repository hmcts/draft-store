#!/usr/bin/env bash
# Runs the api and backend db, grabs the generated swagger json spec and compares to what is in the repo
# fails if they do not match
docker-compose up -d
sleep 10
CURRENT_DOCS=api-docs/swagger-spec.json
touch $CURRENT_DOCS
NEW_DOCS=$(curl http://localhost:8800/v2/api-docs)
docker-compose stop
if [[ $(< ${CURRENT_DOCS}) != "$NEW_DOCS" ]]; then
    echo "API Documentation is out of date. Failing the build"
    exit 1
fi
echo "API Documentation is up to date."
exit 0
