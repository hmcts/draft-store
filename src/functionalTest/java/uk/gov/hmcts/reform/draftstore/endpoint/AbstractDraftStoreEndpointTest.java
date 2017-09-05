package uk.gov.hmcts.reform.draftstore.endpoint;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.draftstore.data.DataAgent;

import java.util.UUID;

import static com.jayway.restassured.RestAssured.given;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class AbstractDraftStoreEndpointTest {

    protected static final String DRAFT_DOCUMENT = "{\"name\": \"anyone\"}";
    protected static final String USER_ID = UUID.randomUUID().toString();
    protected static final String AUTH_TOKEN = "hmcts-id " + USER_ID;

    @LocalServerPort
    protected int port;
    @Autowired
    private DataAgent dataAgent;

    private final String endpointPath;
    private final String draftType;

    public AbstractDraftStoreEndpointTest(String endpointPath, String draftType) {
        this.endpointPath = endpointPath;
        this.draftType = draftType;
    }

    @Before
    public void setUp() {
        dataAgent.deleteDocument(USER_ID, draftType);
    }

    @After
    public void cleanTestData() {
        dataAgent.deleteDocument(USER_ID, draftType);
    }

    @Test
    public void save_shouldCreateDraftDocumentGivenAValidAuthTokenAndAValidDraftDocument() throws Exception {
        given()
            .port(port)
            .accept(APPLICATION_JSON_VALUE)
            .contentType(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, AUTH_TOKEN)
            .content(DRAFT_DOCUMENT)
            .when()
            .post(endpointPath)
            .then()
            .statusCode(SC_CREATED);

        assertDraftDocument(USER_ID, DRAFT_DOCUMENT);
    }

    @Test
    public void save_shouldUpdateDraftStoreGivenAValidAuthTokenAndAValidDraftStore() throws Exception {
        dataAgent.setupDocumentForUser(USER_ID, draftType, "{\"name\": \"Lulu White\"}");

        given()
            .port(port)
            .accept(APPLICATION_JSON_VALUE)
            .contentType(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, AUTH_TOKEN)
            .content(DRAFT_DOCUMENT)
            .when()
            .post(endpointPath)
            .then()
            .statusCode(SC_NO_CONTENT);

        assertDraftDocument(USER_ID, DRAFT_DOCUMENT);
    }

    @Test
    public void save_shouldReturnBadRequestWhenNoAuthHeader() throws Exception {
        given()
            .port(port)
            .accept(APPLICATION_JSON_VALUE)
            .contentType(APPLICATION_JSON_VALUE)
            .content(DRAFT_DOCUMENT)
            .when()
            .post(endpointPath)
            .then()
            .statusCode(SC_BAD_REQUEST)
            .content("errorCode", is("INVALID_AUTH_TOKEN"))
            .content("errors[0]", is("Authorization header is required."));
    }

    @Test
    public void save_shouldReturnForbiddenWhenAuthTokenIsInvalid() throws Exception {
        given()
            .port(port)
            .accept(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, "not a valid auth token")
            .contentType(APPLICATION_JSON_VALUE)
            .content(DRAFT_DOCUMENT)
            .when()
            .post(endpointPath)
            .then()
            .statusCode(SC_FORBIDDEN)
            .content("errorCode", is("USER_DETAILS_SERVICE_ERROR"))
            .content("errors[0]",
                is("Authorization token must be given in following format: 'hmcts-id <userId>'")
            );
    }

    @Test
    public void save_shouldReturnBadRequestWhenDraftDocumentIsInvalidJson() throws Exception {
        given()
            .port(port)
            .accept(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, AUTH_TOKEN)
            .contentType(APPLICATION_JSON_VALUE)
            .content("{invalid json}")
            .when()
            .post(endpointPath)
            .then()
            .statusCode(SC_BAD_REQUEST)
            .content("errorCode", is("BAD_ARGUMENT"))
            .content("errors[0]", is("Invalid Json. Value given: '{invalid json}'"));
    }

    @Test
    public void save_shouldReturnBadRequestWhenNoDraftDocument() throws Exception {
        given()
            .port(port)
            .header(AUTHORIZATION, AUTH_TOKEN)
            .accept(APPLICATION_JSON_VALUE)
            .contentType(APPLICATION_JSON_VALUE)
            .when()
            .post(endpointPath)
            .then()
            .statusCode(SC_BAD_REQUEST)
            .contentType(APPLICATION_JSON_VALUE)
            .content("errorCode", is("BAD_ARGUMENT"))
            .content("errors[0]", is("The draft document is required."));
    }

    @Test
    public void retrieve_shouldReturnADocument() throws Exception {
        dataAgent.setupDocumentForUser(USER_ID, draftType, "{\"name\": \"Lulu White\"}");

        given()
            .port(port)
            .accept(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, AUTH_TOKEN)
            .when()
            .get(endpointPath)
            .then()
            .statusCode(SC_OK)
            .contentType("application/json;charset=UTF-8")
            .content("name", is("Lulu White"));
    }

    @Test
    public void retrieve_shouldReturnNoRecordFoundError() throws Exception {
        given()
            .port(port)
            .accept(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, AUTH_TOKEN)
            .when()
            .get(endpointPath)
            .then()
            .statusCode(SC_NOT_FOUND)
            .contentType("application/json;charset=UTF-8")
            .content("errorCode", is("NO_RECORD_FOUND"));
    }

    @Test
    public void retrieve_shouldReturnBadRequestWhenNoAuthHeader() throws Exception {
        given()
            .port(port)
            .accept(APPLICATION_JSON_VALUE)
            .when()
            .get(endpointPath)
            .then()
            .statusCode(SC_BAD_REQUEST)
            .contentType("application/json;charset=UTF-8")
            .content("errorCode", is("INVALID_AUTH_TOKEN"))
            .content("errors[0]", is("Authorization header is required."));
    }

    @Test
    public void retrieve_shouldReturnForbiddenWhenAuthTokenIsInvalid() throws Exception {
        given()
            .port(port)
            .accept(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, "not a valid auth token")
            .when()
            .get(endpointPath)
            .then()
            .statusCode(SC_FORBIDDEN)
            .contentType("application/json;charset=UTF-8")
            .content("errorCode", is("USER_DETAILS_SERVICE_ERROR"))
            .content("errors[0]", is("Authorization token must be given in following format: 'hmcts-id <userId>'"));
    }

    @Test
    /*
    Given that a document exists
    When I call the service to delete that document
    Then the document is deleted from the data store.
     */
    public void delete_shouldDeleteDraftDocumentGivenAValidAuthTokenAndGivenUserHasADraftDocument() throws Exception {
        dataAgent.setupDocumentForUser(USER_ID, draftType, DRAFT_DOCUMENT);
        given()
            .port(port)
            .header(AUTHORIZATION, AUTH_TOKEN)
            .when()
            .delete(endpointPath)
            .then()
            .statusCode(SC_NO_CONTENT);
        assertThat(dataAgent.countForUser(USER_ID, draftType)).isEqualTo(0);
    }

    @Test
    /*
    Given that a document does not exist
    When I call the service to delete that non-existent document
    Then an error occurs indicating that the document does not exist AND the error is logged.
    */
    public void delete_shouldReturnNotFoundWhenNoDocumentForUser() throws Exception {
        given()
            .port(port)
            .header(AUTHORIZATION, AUTH_TOKEN)
            .when()
            .delete(endpointPath)
            .then()
            .statusCode(SC_NOT_FOUND)
            .contentType("application/json;charset=UTF-8")
            .content("errorCode", is("NO_RECORD_FOUND"));
    }

    @Test
    public void delete_shouldReturnBadRequestWhenNoAuthHeader() throws Exception {
        given()
            .port(port)
            .when()
            .delete(endpointPath)
            .then()
            .statusCode(SC_BAD_REQUEST)
            .contentType("application/json;charset=UTF-8")
            .content("errorCode", is("INVALID_AUTH_TOKEN"))
            .content("errors[0]", is("Authorization header is required."));
    }

    @Test
    public void delete_shouldReturnForbiddenWhenAuthTokenIsInvalid() throws Exception {
        given()
            .port(port)
            .header(AUTHORIZATION, "not a valid auth token")
            .when()
            .delete(endpointPath)
            .then()
            .statusCode(SC_FORBIDDEN)
            .contentType("application/json;charset=UTF-8")
            .content("errorCode", is("USER_DETAILS_SERVICE_ERROR"))
            .content("errors[0]", is("Authorization token must be given in following format: 'hmcts-id <userId>'"));
    }

    private void assertDraftDocument(String userId, String expectedDraftDocument) {
        String actualDocument = dataAgent.documentForUser(userId, draftType);
        assertThat(actualDocument).isEqualTo(expectedDraftDocument);
    }
}
