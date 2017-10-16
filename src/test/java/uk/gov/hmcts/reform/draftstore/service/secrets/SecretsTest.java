package uk.gov.hmcts.reform.draftstore.service.secrets;

import com.google.common.base.Strings;
import org.junit.Test;

import static java.util.Collections.nCopies;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SecretsTest {

    @Test
    public void should_validate_number_of_secrets() throws Exception {
        String header =
            String.join(
                Secrets.SEPARATOR,
                nCopies(3, secretOfLength(Secrets.MIN_SECRET_LENGTH))
            );

        assertThatThrownBy(() -> Secrets.fromHeader(header)).isInstanceOf(SecretsException.class);
    }

    @Test
    public void should_handle_null_header() throws Exception {
        Secrets secrets = Secrets.fromHeader(null);

        assertThat(secrets.primary).isNull();
        assertThat(secrets.secondary).isNull();
    }

    @Test
    public void should_validate_length_of_secrets() throws Exception {
        assertThatThrownBy(() -> {
            String ok = secretOfLength(Secrets.MIN_SECRET_LENGTH);
            String tooShort = secretOfLength(Secrets.MIN_SECRET_LENGTH - 1);

            Secrets.fromHeader(ok + Secrets.SEPARATOR + tooShort);
        }).isInstanceOf(SecretsException.class);
    }

    @Test
    public void should_convert_header_with_two_secrets_to_object() throws Exception {
        String s1 = Strings.repeat("a", Secrets.MIN_SECRET_LENGTH);
        String s2 = Strings.repeat("b", Secrets.MIN_SECRET_LENGTH);

        Secrets secrets = Secrets.fromHeader(s1 + Secrets.SEPARATOR + s2);

        assertThat(secrets.primary).isEqualTo(s1);
        assertThat(secrets.secondary).isEqualTo(s2);
    }

    @Test
    public void should_convert_header_with_one_secret_to_object() throws Exception {
        String secret = Strings.repeat("x", Secrets.MIN_SECRET_LENGTH);

        Secrets secrets = Secrets.fromHeader(secret);

        assertThat(secrets.primary).isEqualTo(secret);
        assertThat(secrets.secondary).isNull();
    }

    private static String secretOfLength(int length) {
        return Strings.repeat("x", length);
    }
}
