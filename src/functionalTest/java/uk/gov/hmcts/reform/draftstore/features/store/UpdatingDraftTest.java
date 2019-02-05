package uk.gov.hmcts.reform.draftstore.features.store;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.draftstore.SmokeTestSuite;
import uk.gov.hmcts.reform.draftstore.response.Draft;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource("classpath:application.properties")
@RunWith(SpringRunner.class)
public class UpdatingDraftTest extends SmokeTestSuite {

    @Test
    public void user_can_edit_own_draft() throws IOException, JSONException {
        String oldJsonDocument = "{\"testing\":\"functional\"}";
        String draftId = draftStoreClient.createDraft(oldJsonDocument);

        String newJsonDocument = "{\"done\":true}";
        draftStoreClient.updateDraft(draftId, newJsonDocument);
        Optional<Draft> response = draftStoreClient.readDraft(draftId);

        assertThat(response.isPresent()).isTrue();
        Draft newDraft = response.get();
        assertThat(newDraft.id).isEqualTo(draftId);
        JsonNode expectedDocument = new ObjectMapper().readTree(newJsonDocument);
        assertThat(newDraft.document).isEqualTo(expectedDocument);
        assertThat(newDraft.created).isNotNull();
        assertThat(newDraft.created).isAfter(newDraft.updated);
    }
}
