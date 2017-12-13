FROM openjdk:8-jre

COPY build/install/swagger /opt/app/

WORKDIR /opt/app

HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD http_proxy="" curl --silent --fail http://localhost:8800/health

EXPOSE 8800

ENTRYPOINT ["/opt/app/bin/swagger"]
