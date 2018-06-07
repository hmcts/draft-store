package uk.gov.hmcts.reform.draftstore.client;

import com.google.common.collect.ImmutableMap;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.Base64;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;


public class IdamClient {

    private final String idamUrl;
    private final String email;
    private final String password;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;

    public IdamClient(
        String idamUrl,
        String email,
        String password,
        String clientId,
        String clientSecret,
        String redirectUri
    ) {
        this.idamUrl = idamUrl;
        this.email = email;
        this.password = password;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }

    /**
     * Signs into Idam using credentials from config.
     *
     * @return Idam access token
     */
    public String signIn() {
        String authorisationCode = getAuthorisationCode();
        return getIdamToken(authorisationCode);
    }

    public String getTestToken() {
        return RestAssured
            .given()
            .relaxedHTTPSValidation()
            .baseUri(this.idamUrl)
            .get("/testing-support/lease")
            .getBody()
            .asString();
    }

    private String getAuthorisationCode() {
        Response response = sendAuthorisationRequest();

        if (response.getStatusCode() == OK.value()) {
            return extractAuthorisationCodeFromIdamResponse(response);
        } else {
            throw new AssertionError(String.format(
                "Unexpected Idam response (%s) when trying to log user in. Response body: %s",
                response.getStatusCode(),
                response.getBody().print()
            ));
        }
    }

    private String getIdamToken(String code) {
        return RestAssured
            .given()
            .relaxedHTTPSValidation()
            .baseUri(this.idamUrl)
            .header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
            .queryParams(
                ImmutableMap.of(
                    "grant_type", "authorization_code",
                    "client_id", clientId,
                    "client_secret", clientSecret,
                    "redirect_uri", redirectUri,
                    "code", code
                )
            )
            .post("/oauth2/token")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .jsonPath()
            .get("access_token");
    }

    private Response sendAuthorisationRequest() {
        return RestAssured
            .given()
            .relaxedHTTPSValidation()
            .baseUri(this.idamUrl)
            .header("Authorization", "Basic " + buildIdamSignInToken())
            .queryParams(
                ImmutableMap.of(
                    "response_type", "code",
                    "client_id", clientId,
                    "client_secret", clientSecret,
                    "redirect_uri", redirectUri
                )
            )
            .post("/oauth2/authorize")
            .thenReturn();
    }

    private String buildIdamSignInToken() {
        String unencodedToken = String.format("%s:%s", email, password);

        return Base64.getEncoder().encodeToString(unencodedToken.getBytes());
    }

    private String extractAuthorisationCodeFromIdamResponse(Response response) {
        return response
            .then()
            .statusCode(200)
            .extract()
            .body()
            .jsonPath()
            .get("code");
    }
}
