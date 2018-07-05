package uk.gov.hmcts.reform.draftstore.service.idam;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class IdamClientImplTest {

    @Test
    public void should_throw_custom_exception_on_client_errors() {
        // given
        RestTemplate restTemplate = mock(RestTemplate.class);
        HttpClientErrorException exceptionThrownByRestTemplate = new HttpClientErrorException(UNAUTHORIZED, "message");
        given(
            restTemplate.exchange(
                anyString(),
                any(),
                any(HttpEntity.class),
                any(Class.class)
            )
        ).willThrow(exceptionThrownByRestTemplate);

        IdamClientImpl idamClient = new IdamClientImpl(restTemplate, "");

        // when
        Throwable exc = catchThrowable(() -> idamClient.getUserDetails("foo"));

        // then
        assertThat(exc)
            .isInstanceOf(InvalidIdamTokenException.class)
            .hasCause(exceptionThrownByRestTemplate)
            .hasMessage(exceptionThrownByRestTemplate.getMessage());
    }
}
