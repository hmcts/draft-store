package uk.gov.hmcts.reform.draftstore.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.draftstore.controllers.helpers.SampleData;

import static io.restassured.RestAssured.given;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.reform.draftstore.service.AuthService.SECRET_HEADER;
import static uk.gov.hmcts.reform.draftstore.service.AuthService.SERVICE_HEADER;

/*
Test for DIV-881 - the exception handler was returning too much information from
uncaught exceptions, allowing clients to see, for example, failed sql statements. It was considered a security flaw...
So, this test demonstrates that for all unhandled exceptions the exception message is not returned to the client.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/database.properties")
@ActiveProfiles("test-unhandled-exception")
public class HealthTest {

    @LocalServerPort
    private int port;

    @Test
    public void should_return_200_for_liveness_check() throws Exception {

        given()
            .port(port)
            .accept(APPLICATION_JSON_VALUE)
            .header(SERVICE_HEADER, "some-service")
            .header(SECRET_HEADER, SampleData.secret())
            .when()
            .get("/health/liveness")
            .then()
            .statusCode(200)
            .log().all();
    }
}
