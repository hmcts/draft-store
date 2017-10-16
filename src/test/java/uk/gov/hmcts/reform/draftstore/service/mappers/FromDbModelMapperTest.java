package uk.gov.hmcts.reform.draftstore.service.mappers;

import org.junit.Test;
import uk.gov.hmcts.reform.draftstore.data.model.Draft;
import uk.gov.hmcts.reform.draftstore.service.crypto.CryptoService;
import uk.gov.hmcts.reform.draftstore.service.secrets.Secrets;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class FromDbModelMapperTest {

    private uk.gov.hmcts.reform.draftstore.domain.Draft result;

    @Test
    public void should_use_plaintext_document_if_it_was_not_encrypted() throws Exception {

        result = FromDbModelMapper
            .fromDb(
                dbDraft("hello", null),
                new Secrets(SampleSecret.get(), null)
            );

        assertThat(result.document).isEqualTo("hello");
    }

    @Test
    public void should_use_encrypted_document_when_it_is_available() throws Exception {
        String secret = SampleSecret.get();
        result = FromDbModelMapper
            .fromDb(
                dbDraft(null, CryptoService.encrypt("hello", secret)),
                new Secrets(secret, null)
            );

        assertThat(result.document).isEqualTo("hello");
    }

    @Test
    public void should_use_secondary_secret_if_primary_is_invalid() throws Exception {
        String primarySecret = "primary" + SampleSecret.get();
        String secondarySecret = "secondary" + SampleSecret.get();

        result = FromDbModelMapper
            .fromDb(
                dbDraft(null, CryptoService.encrypt("hello", secondarySecret)),
                new Secrets(primarySecret, secondarySecret)
            );

        assertThat(result.document).isEqualTo("hello");
    }

    private Draft dbDraft(String plaintextDoc, byte[] encryptedDoc) {
        return new Draft(
            "id",
            "user",
            "service",
            plaintextDoc,
            encryptedDoc,
            "type",
            ZonedDateTime.now(),
            ZonedDateTime.now()
        );
    }
}
