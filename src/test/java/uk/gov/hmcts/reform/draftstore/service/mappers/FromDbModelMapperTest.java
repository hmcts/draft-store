package uk.gov.hmcts.reform.draftstore.service.mappers;

import org.junit.Test;
import uk.gov.hmcts.reform.draftstore.data.model.Draft;
import uk.gov.hmcts.reform.draftstore.service.crypto.CryptoService;
import uk.gov.hmcts.reform.draftstore.service.crypto.InvalidKeyException;
import uk.gov.hmcts.reform.draftstore.service.secrets.Secrets;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FromDbModelMapperTest {

    private uk.gov.hmcts.reform.draftstore.domain.Draft result;

    @Test
    public void should_use_encrypted_document_when_it_is_available() throws Exception {
        String secret = SampleSecret.get();
        result = FromDbModelMapper
            .fromDb(
                dbDraft(CryptoService.encrypt("hello", secret)),
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
                dbDraft(CryptoService.encrypt("hello", secondarySecret)),
                new Secrets(primarySecret, secondarySecret)
            );

        assertThat(result.document).isEqualTo("hello");
    }

    @Test
    public void should_throw_an_exception_if_both_secrets_are_invalid() throws Exception {
        String valid = SampleSecret.get();
        String invalid1 = SampleSecret.get() + "xxx";
        String invalid2 = SampleSecret.get() + "yyy";

        Draft draft = dbDraft(CryptoService.encrypt("hello", valid));

        assertThatThrownBy(
            () -> FromDbModelMapper.fromDb(draft, new Secrets(invalid1, invalid2))
        ).isInstanceOf(InvalidKeyException.class);
    }

    private Draft dbDraft(byte[] encryptedDoc) {
        return new Draft(
            "id",
            "user",
            "service",
            encryptedDoc,
            "type",
            ZonedDateTime.now(),
            ZonedDateTime.now()
        );
    }
}
