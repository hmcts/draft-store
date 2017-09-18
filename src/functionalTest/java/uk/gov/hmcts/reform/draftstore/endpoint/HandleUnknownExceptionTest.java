package uk.gov.hmcts.reform.draftstore.endpoint;

import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.draftstore.data.DraftStoreDAO;
import uk.gov.hmcts.reform.draftstore.service.UserIdentificationService;

import java.util.UUID;

import static com.jayway.restassured.RestAssured.given;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.reform.draftstore.service.UserIdentificationService.SERVICE_HEADER;

/*
Test for DIV-881 - the exception handler was returning too much information from
uncaught exceptions, allowing clients to see, for example, failed sql statements. It was considered a security flaw...
So, this test demonstrates that for all unhandled exceptions the exception message is not returned to the client.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test-unhandled-exception")
public class HandleUnknownExceptionTest {
    private static final String DRAFT_URI = "/drafts";
    @Autowired
    private DraftStoreDAO dao;

    private static final String USER_ID = UUID.randomUUID().toString();
    private static final String AUTH_TOKEN = "hmcts-id " + USER_ID;

    @LocalServerPort
    private int port;

    @Test
    public void unhandledExceptionsShouldNotReturnExceptionDetailsToClient() throws Exception {
        when(dao.readAll(anyString(), anyString()))
            .thenThrow(new RuntimeException("do not display this message") { });

        given()
            .port(port)
            .accept(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, AUTH_TOKEN)
            .header(SERVICE_HEADER, "some-service")
            .when()
            .get(DRAFT_URI)
            .then()
            .statusCode(SC_INTERNAL_SERVER_ERROR)
            .contentType("application/json;charset=UTF-8")
            .content("errorCode", is("SERVER_ERROR"))
            .content("errors", IsNull.nullValue());
    }
}
