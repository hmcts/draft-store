package uk.gov.hmcts.reform.draftstore.service.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.hmcts.reform.draftstore.domain.CreateDraft;
import uk.gov.hmcts.reform.draftstore.service.crypto.CryptoService;
import uk.gov.hmcts.reform.draftstore.service.secrets.Secrets;

import static org.assertj.core.api.Assertions.assertThat;

public class ToDbModelMapperTest {

    @Test
    public void should_use_primary_secret_for_encryption() throws Exception {
        String primarySecret = "primary" + SampleSecret.get();
        String secondarySecret = "secondary" + SampleSecret.get();

        CreateDraft draft = createDraft();

        uk.gov.hmcts.reform.draftstore.data.model.CreateDraft result =
            ToDbModelMapper.toDb(draft, new Secrets(primarySecret, secondarySecret));

        assertThat(CryptoService.decrypt(result.encryptedDocument, primarySecret))
            .isEqualTo(draft.document.toString());
    }

    private CreateDraft createDraft() {
        return new CreateDraft(
            new ObjectMapper().createObjectNode(),
            "type",
            123
        );
    }
}
