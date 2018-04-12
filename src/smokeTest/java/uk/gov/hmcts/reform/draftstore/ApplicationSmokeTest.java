package uk.gov.hmcts.reform.draftstore;

import io.restassured.RestAssured;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringRunner.class)
public class ApplicationSmokeTest {

    @Value("${test-url}")
    private String testUrl;

    @Test
    public void service_is_healthy() {
        RestAssured.baseURI = testUrl;

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
