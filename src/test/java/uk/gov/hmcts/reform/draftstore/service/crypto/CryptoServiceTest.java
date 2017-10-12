package uk.gov.hmcts.reform.draftstore.service.crypto;

import org.junit.Test;

import java.util.stream.Stream;
import javax.crypto.AEADBadTagException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class CryptoServiceTest {

    @Test
    public void should_decrypt_back_to_original_input() throws Exception {
        final String secret = "afowh7f9a68efag68efowe";
        Stream.of(
            "Hello, world!",
            "{ \"abc\": 123 }",
            "ąćęłńóśźż 的",
            ""
        ).forEach(message -> {
            // given
            byte[] encryptedBytes = CryptoService.encrypt(message, secret);

            // when
            String backToPlainText = CryptoService.decrypt(encryptedBytes, secret);

            // then
            assertThat(encryptedBytes).isNotEqualTo(message.getBytes());
            assertThat(backToPlainText).isEqualTo(message);
        });
    }

    @Test
    public void should_throw_an_exception_when_wrong_secret_is_used() throws Exception {
        // given
        String secret = "ha0s9fnuiaw4a7s9dhfad9agsg";
        byte[] encryptedBytes = CryptoService.encrypt("hello", secret);

        // when
        Throwable thrown = catchThrowable(() -> CryptoService.decrypt(encryptedBytes, "not" + secret));

        // then
        assertThat(thrown)
            .isInstanceOf(InvalidKeyException.class);
    }
}
