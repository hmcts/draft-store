package uk.gov.hmcts.reform.draftstore.features.store;

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
public class RemovingDraftTest extends SmokeTestSuite {

    @Test
    public void user_can_remove_own_draft() throws IOException, JSONException {
        String jsonDocument = "{\"testing\":\"functional\"}";
        String draftId = draftStoreClient.createDraft(jsonDocument);

        draftStoreClient.deleteDraft(draftId);

        Optional<Draft> draft = draftStoreClient.readDraft(draftId);

        assertThat(draft.isPresent()).isFalse();
    }
}
