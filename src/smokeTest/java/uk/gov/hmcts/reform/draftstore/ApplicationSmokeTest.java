package uk.gov.hmcts.reform.draftstore;

import io.restassured.RestAssured;
import org.junit.Test;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.equalTo;

public class ApplicationSmokeTest extends SmokeTestSuite {

    @Test
    public void service_is_healthy() {
        RestAssured.baseURI = draftStoreUrl;

        RestAssured.given()
            .relaxedHTTPSValidation()
            .get("/health")
            .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .and()
            .body("status", equalTo(Status.UP.toString()));
    }
}
