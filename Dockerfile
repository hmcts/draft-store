FROM hmcts/cnp-java-base:openjdk-8u191-jre-alpine3.9-0.1

# Mandatory!
ENV APP draft-store.jar

COPY build/libs/$APP /opt/app/

HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD http_proxy="" wget -q --spider http://localhost:8800/health || exit 1

EXPOSE 8800

#CMD ["\$APP"]
CMD ["draft-store.jar"]
