FROM hmcts/cnp-java-base:openjdk-8u191-jre-alpine3.9-1.1-alpha

COPY build/libs/draft-store.jar /opt/app/

HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD http_proxy="" wget -q --spider http://localhost:8800/health || exit 1

EXPOSE 8800

CMD ["draft-store.jar"]
