package uk.gov.hmcts.reform.draftstore.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.draftstore.domain.CreateDraft;
import uk.gov.hmcts.reform.draftstore.domain.UpdateDraft;
import uk.gov.hmcts.reform.draftstore.response.Draft;
import uk.gov.hmcts.reform.draftstore.response.DraftList;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.isOneOf;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


public class DraftStoreClient {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String SECRET_HEADER = "Secret";
    private static final String SERVICE_HEADER = "ServiceAuthorization";

    private final String draftStoreUrl;
    private final String s2sToken;
    private final String idamToken;
    private final String encryptionSecret;

    public DraftStoreClient(
        String draftStoreUrl,
        String s2sToken,
        String idamToken,
        String encryptionSecret
    ) {
        this.draftStoreUrl = draftStoreUrl;
        this.s2sToken = s2sToken;
        this.idamToken = idamToken;
        this.encryptionSecret = encryptionSecret;
    }

    /**
     * Sends an API request to create a draft.
     * @param documentJson JSON document to store inside the draft
     * @return Draft ID
     */
    public String createDraft(String documentJson) throws JSONException, IOException {
        Response response = withAuthenticatedRequest()
            .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .header(SECRET_HEADER, encryptionSecret)
            .body(new CreateDraft(parseJson(documentJson), "smoke_test", 1))
            .post("/drafts")
            .andReturn();

        response
            .then()
            .assertThat()
            .statusCode(HttpStatus.CREATED.value());

        String location = response.header("Location");
        assertThat(location).isNotBlank();

        return extractIdFromUrl(location);
    }

    public void updateDraft(String draftId, String documentJson) throws IOException {
        withAuthenticatedRequest()
            .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .header(SECRET_HEADER, encryptionSecret)
            .body(new UpdateDraft(parseJson(documentJson), "smoke_test"))
            .put("/drafts/" + draftId)
            .then()
            .assertThat()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    public void deleteDraft(String draftId) {
        withAuthenticatedRequest()
            .delete("/drafts/" + draftId)
            .then()
            .assertThat()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    public void deleteAllUsersDrafts() {
        withAuthenticatedRequest()
            .delete("/drafts")
            .then()
            .assertThat()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    public Optional<Draft> readDraft(String draftId) {
        Response response = withAuthenticatedRequest()
            .header(SECRET_HEADER, encryptionSecret)
            .get("/drafts/" + draftId)
            .andReturn();

        response
            .then()
            .statusCode(isOneOf(OK.value(), NOT_FOUND.value()));

        return response.statusCode() == OK.value()
            ? Optional.of(response.as(Draft.class))
            : Optional.empty();
    }

    public DraftList readDraftPage() {
        Response response = withAuthenticatedRequest()
            .header(SECRET_HEADER, encryptionSecret)
            .get("/drafts")
            .andReturn();

        response
            .then()
            .assertThat()
            .statusCode(OK.value());

        return response.as(DraftList.class);
    }

    private RequestSpecification withAuthenticatedRequest() {

        return RestAssured.given()
            .relaxedHTTPSValidation()
            .baseUri(draftStoreUrl)
            .header(SERVICE_HEADER, "Bearer " + s2sToken)
            .header(AUTHORIZATION, "Bearer " + idamToken);
    }

    private JsonNode parseJson(String jsonString) throws IOException {
        return mapper.readTree(jsonString);
    }

    private String extractIdFromUrl(String uri) {
        String[] segments = uri.split("/");
        return segments[segments.length - 1];
    }
}
