FROM postgres:17-bullseye

COPY init-db.sh /docker-entrypoint-initdb.d

HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD psql -c 'select 1' -d draftstore -U draftstore

EXPOSE 5432
