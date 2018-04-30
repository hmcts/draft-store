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
public class ReadingDraftTest extends SmokeTestSuite {

    @Test
    public void user_can_read_own_draft() throws IOException, JSONException {
        String documentJson = "{\"testing\":\"functional\"}";
        String draftId = draftStoreClient.createDraft(documentJson);
        Optional<Draft> response = draftStoreClient.readDraft(draftId);
        assertThat(response.isPresent()).isTrue();
        Draft draft = response.get();
        assertThat(draft.id).isEqualTo(draftId);
        assertThat(draft.document).isNotNull();
        assertThat(draft.document.hasNonNull("testing")).isTrue();
        assertThat(draft.document.get("testing")).isEqualTo("functional");
        assertThat(draft.created).isNotNull();
        assertThat(draft.updated).isNotNull();
    }
}
