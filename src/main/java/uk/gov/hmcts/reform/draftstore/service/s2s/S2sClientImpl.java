package uk.gov.hmcts.reform.draftstore.service.s2s;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class S2sClientImpl implements S2sClient {
    private final RestTemplate restTemplate;
    private final String url;

    public S2sClientImpl(final String idamUrl) {
        this.restTemplate = new RestTemplate();
        this.url = idamUrl;
    }

    @Override
    public String getServiceName(String authHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION, authHeader);

        return restTemplate
            .exchange(
                url + "/details",
                HttpMethod.GET,
                new HttpEntity<String>(headers),
                String.class
            ).getBody();
    }
}
