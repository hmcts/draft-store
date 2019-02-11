FROM hmcts/cnp-java-base:openjdk-8u191-jre-alpine3.9-0.1

# Mandatory!
ENV APP draft-store.jar

COPY build/libs/$APP /opt/app/

HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD http_proxy="" wget -q --spider http://localhost:8800/health || exit 1

EXPOSE 8800

ENV JAVA_OPTS "-XX:+PrintFlagsFinal"
ENV JAVA_TOOL_OPTIONS "-XX:+UseContainerSupport -XX:InitialRAMPercentage=25.0 -XX:MaxRAMPercentage=70.0 -XX:MinRAMPercentage=10.0 -XX:UseParallelGC -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=40 -XX:GCTimeRatio=4 -XX:AdaptiveSizePolicyWeight=90 -Xms96M"

#CMD ["\$APP"]
CMD ["draft-store.jar"]
