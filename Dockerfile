ARG APP_INSIGHTS_AGENT_VERSION=3.2.6
FROM hmctspublic.azurecr.io/base/java:17-distroless

COPY lib/applicationinsights.json /opt/app/
COPY build/libs/draft-store.jar /opt/app/

EXPOSE 8800

CMD ["draft-store.jar"]
