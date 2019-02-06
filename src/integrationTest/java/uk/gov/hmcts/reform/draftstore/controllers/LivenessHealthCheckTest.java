package uk.gov.hmcts.reform.draftstore.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/database.properties")
public class LivenessHealthCheckTest {

    @LocalServerPort
    private int port;

    @Test
    public void should_return_UP_for_liveness_check() {
        given()
            .port(port)
            .accept(APPLICATION_JSON_VALUE)
            .when()
            .get("/health/liveness")
            .then()
            .statusCode(200)
            .body("status", is("UP"));
    }

    @Test
    public void health_should_contain_liveness_check() {
        given()
            .port(port)
            .accept(APPLICATION_JSON_VALUE)
            .when()
            .get("/health")
            .then()
            .statusCode(200)
            .body("details.liveness.status", is("UP"));
    }
}
