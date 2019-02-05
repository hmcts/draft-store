package uk.gov.hmcts.reform.draftstore.service;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class Liveness implements HealthIndicator {

    @Override
    public Health health() {
        return Health.up().build();
    }

}