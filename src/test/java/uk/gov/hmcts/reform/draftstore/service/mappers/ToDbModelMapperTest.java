package uk.gov.hmcts.reform.draftstore.service.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.hmcts.reform.draftstore.domain.CreateDraft;

import static org.assertj.core.api.Assertions.assertThat;

public class ToDbModelMapperTest {

    private uk.gov.hmcts.reform.draftstore.data.model.CreateDraft result;

    @Test
    public void should_set_only_encrypted_document_when_secret_is_passed() throws Exception {
        result = ToDbModelMapper.toDb(createDraft(), "a98sdf8asd7f");

        assertThat(result.document).isNull();
        assertThat(result.encryptedDocument).isNotNull();
    }

    @Test
    public void should_set_only_unencrypted_document_when_secret_is_NOT_passed() throws Exception {
        result = ToDbModelMapper.toDb(createDraft(), null);

        assertThat(result.document).isNotNull();
        assertThat(result.encryptedDocument).isNull();
    }

    private CreateDraft createDraft() {
        return new CreateDraft(
            new ObjectMapper().createObjectNode(),
            "type",
            123
        );
    }
}
