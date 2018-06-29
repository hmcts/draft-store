package uk.gov.hmcts.reform.draftstore.service.idam;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class IdamClientImpl implements IdamClient {

    private final RestTemplate restTemplate;
    private final String idamUrl;

    public IdamClientImpl(final String idamUrl) {
        this.restTemplate = new RestTemplate();
        this.idamUrl = idamUrl;
    }

    @Override
    public User getUserDetails(String authHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION, authHeader);

        try {
            return restTemplate
                .exchange(
                    idamUrl + "/details",
                    HttpMethod.GET,
                    new HttpEntity<String>(headers),
                    User.class
                ).getBody();

        } catch (HttpClientErrorException exc) {
            throw new InvalidIdamTokenException(exc.getMessage(), exc);
        }
    }
}
