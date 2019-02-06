package uk.gov.hmcts.reform.draftstore;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.draftstore.response.Draft;

import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RunWith(SpringRunner.class)
public class ApplicationSmokeTest extends SmokeTestSuite {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String SAMPLE_DOCUMENT = "{\"some\": \"draft\"}";
    private static final String SMOKE_TEST_DRAFT_TYPE = "smoke_test";

    @Test
    public void should_be_able_to_create_draft() throws Exception {
        draftStoreClient.createDraft(SAMPLE_DOCUMENT);
    }

    @Test
    public void should_be_able_to_read_existing_draft() throws Exception {
        String draftId = draftStoreClient.createDraft(SAMPLE_DOCUMENT);
        Optional<Draft> draft = draftStoreClient.readDraft(draftId);
        assertThat(draft.isPresent()).isTrue();
        assertThat(draft.get().document).isEqualTo(mapper.readTree(SAMPLE_DOCUMENT));
        assertThat(draft.get().type).isEqualTo(SMOKE_TEST_DRAFT_TYPE);
        assertThat(draft.get().created).isNotNull();
        assertThat(draft.get().id).isEqualTo(draftId);
        assertThat(draft.get().updated).isNotNull();
    }

    @Test
    public void should_be_able_to_update_draft() throws Exception {
        String document1 = "{\"some\":\"draft\"}";
        String document2 = "{\"completely\": \"different draft\"}";

        String draftId = draftStoreClient.createDraft(document1);
        draftStoreClient.updateDraft(draftId, document2);
        Optional<Draft> draft = draftStoreClient.readDraft(draftId);
        assertThat(draft.isPresent()).isTrue();
        assertThat(draft.get().document).isEqualTo(mapper.readTree(document2));
    }

    @Test
    public void should_be_able_to_delete_draft() throws Exception {
        String draftId = draftStoreClient.createDraft(SAMPLE_DOCUMENT);
        draftStoreClient.deleteDraft(draftId);
        assertThat(draftStoreClient.readDraft(draftId)).isEqualTo(Optional.empty());
    }

    @Test
    public void should_be_able_to_return_page_of_drafts() throws Exception {
        // make sure there's some data to return
        draftStoreClient.createDraft(SAMPLE_DOCUMENT);

        assertThat(draftStoreClient.readDraftPage().data).isNotEmpty();
    }

    @Test
    public void should_be_able_to_delete_all_users_drafts_in_service() throws Exception {
        draftStoreClient.createDraft(SAMPLE_DOCUMENT);
        assertThat(draftStoreClient.readDraftPage().data).isNotEmpty();
        draftStoreClient.deleteAllUsersDrafts();
        assertThat(draftStoreClient.readDraftPage().data).isEmpty();
    }


    @Test
    public void should_return_UP_for_liveness_check() {
        given()
            .accept(APPLICATION_JSON_VALUE)
            .when()
            .get("http" + draftStoreUrl.substring("https".length()) + "/health/liveness")
            .then()
            .statusCode(200)
            .body("status", is("UP"));
    }

    @Test
    public void should_have_an_up_status_healthCheck() {
        given()
            .accept(APPLICATION_JSON_VALUE)
            .when()
            .get("http" + draftStoreUrl.substring("https".length()) + "/health")
            .then()
            .statusCode(200)
            .body("status", is("UP"));
    }
}
