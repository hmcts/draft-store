package uk.gov.hmcts.reform.draftstore;

import net.bytebuddy.utility.RandomString;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.draftstore.client.DraftStoreClient;
import uk.gov.hmcts.reform.draftstore.client.IdamClient;
import uk.gov.hmcts.reform.draftstore.client.S2sClient;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
@TestPropertySource("classpath:application.properties")
public abstract class SmokeTestSuite {

    @Value("${s2s-url}")
    protected String s2sUrl;

    @Value("${s2s-name}")
    protected String s2sName;

    @Value("${s2s-secret}")
    protected String s2sSecret;

    @Value("${idam-url}")
    protected String idamUrl;

    @Value("${idam-user-email}")
    protected String idamUserEmail;

    @Value("${idam-password}")
    protected String idamPassword;

    @Value("${idam-client-id}")
    protected String idamClientId;

    @Value("${idam-client-secret}")
    protected String idamClientSecret;

    @Value("${idam-redirect-uri}")
    protected String idamRedirectUri;

    @Value("${draft-store-url}")
    protected String draftStoreUrl;

    protected DraftStoreClient draftStoreClient;

    @Before
    public void setUp() {
        String s2sToken = new S2sClient(s2sUrl, s2sName, s2sSecret).signIntoS2S();
        String idamToken = signIntoIdam(createIdamClient());

        String encryptionSecret = RandomString.make(20);

        draftStoreClient = new DraftStoreClient(
            draftStoreUrl,
            s2sToken,
            idamToken,
            encryptionSecret
        );
    }

    @After
    public void tearDown() {
        draftStoreClient.deleteAllUsersDrafts();
    }

    /**
     * Signs into Idam. If 401 response is returned, registers the user and retries.
     *
     * @return Idam access token
     */
    private String signIntoIdam(IdamClient idamClient) {
        Optional<String> authorisationCode = idamClient.getAuthorisationCode(false);

        return authorisationCode
            .map(code -> idamClient.getIdamToken(code))
            .orElseGet(() -> {
                idamClient.registerUser();
                String code = idamClient.getAuthorisationCode(true).get();
                return idamClient.getIdamToken(code);
            });
    }

    private IdamClient createIdamClient() {
        return new IdamClient(
            idamUrl,
            idamUserEmail,
            idamPassword,
            idamClientId,
            idamClientSecret,
            idamRedirectUri
        );
    }
}
