package uk.gov.hmcts.reform.draftstore.endpoint.v2;

import org.junit.Test;
import uk.gov.hmcts.reform.draftstore.endpoint.AbstractDraftStoreEndpointTest;

import static com.jayway.restassured.RestAssured.given;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class DraftStoreEndpointTest extends AbstractDraftStoreEndpointTest {

    public DraftStoreEndpointTest() {
        super(endpointPathForDraftType("petition"), "petition");
    }

    @Test
    public void save_shouldReturnNotFoundWhenDocumentTypeIsMissing() throws Exception {
        given()
            .port(port)
            .accept(APPLICATION_JSON_VALUE)
            .contentType(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, AUTH_TOKEN)
            .content(DRAFT_DOCUMENT)
            .when()
            .post(endpointPathForDraftType(null))
            .then()
            .statusCode(SC_NOT_FOUND);
    }

    @Test
    public void retrieve_shouldReturnNotFoundWhenDocumentTypeIsMissing() throws Exception {
        given()
            .port(port)
            .accept(APPLICATION_JSON_VALUE)
            .when()
            .get(endpointPathForDraftType(null))
            .then()
            .statusCode(SC_NOT_FOUND);
    }

    @Test
    public void delete_shouldReturnNotFoundWhenDocumentTypeIsMissing() throws Exception {
        given()
            .port(port)
            .header(AUTHORIZATION, AUTH_TOKEN)
            .when()
            .delete(endpointPathForDraftType(null))
            .then()
            .statusCode(SC_NOT_FOUND);
    }

    private static String endpointPathForDraftType(String draftType) {
        return "/api/v2/draft/" + (draftType != null ? draftType : "");
    }
}
