FROM hmcts/cnp-java-base:openjdk-8u181-jre-alpine3.8-1.0

# Mandatory!
ENV APP draft-store.jar
ENV APPLICATION_TOTAL_MEMORY 1024M
ENV APPLICATION_SIZE_ON_DISK_IN_MB 39

COPY build/libs/$APP /opt/app/

HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD http_proxy="" wget -q --spider http://localhost:8800/health || exit 1

EXPOSE 8800
