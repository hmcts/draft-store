package uk.gov.hmcts.reform.draftstore.service.crypto;

import org.junit.Test;

import java.util.stream.Stream;
import javax.crypto.AEADBadTagException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class CryptoServiceTest {

    @Test
    public void should_decrypt_back_to_original_input() throws Exception {
        Stream.of(
            "Hello, world!",
            "{ \"abc\": 123 }",
            "ąćęłńóśźż 的",
            ""
        ).forEach(message -> {
            // given
            byte[] encryptedBytes = CryptoService.encrypt(message, "password");

            // when
            String backToPlainText = CryptoService.decrypt(encryptedBytes, "password");

            // then
            assertThat(encryptedBytes).isNotEqualTo(message.getBytes());
            assertThat(backToPlainText).isEqualTo(message);
        });
    }

    @Test
    public void should_throw_an_exception_when_wrong_password_is_used() throws Exception {
        // given
        byte[] encryptedBytes = CryptoService.encrypt("hello", "AAAAAAAAA");

        // when
        Throwable thrown = catchThrowable(() -> CryptoService.decrypt(encryptedBytes, "BBB"));

        // then
        assertThat(thrown)
            .isInstanceOf(GeneralSecurityRuntimeException.class)
            .hasCauseInstanceOf(AEADBadTagException.class);
    }
}
