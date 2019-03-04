package uk.gov.hmcts.reform.draftstore.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;
import uk.gov.hmcts.reform.draftstore.service.AuthService;
import uk.gov.hmcts.reform.draftstore.service.DraftService;
import uk.gov.hmcts.reform.draftstore.service.idam.InvalidIdamTokenException;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReportControllerTest {

    @Mock
    private DraftService draftService;

    @Mock
    private AuthService authService;

    private ReportController controller;

    @Before
    public void setUp() {
        controller = new ReportController(draftService, authService);
    }

    @Test
    public void shouldThrowExceptionIfUserNotAuthenticated() {
        // given
        when(authService.authenticate(anyString()))
            .thenThrow(getAuthenticationFailedException());

        // when
        assertThatThrownBy(() -> controller.getDocumentTypeCounts("userId", "authHeader"))
            .isInstanceOf(InvalidIdamTokenException.class);
    }

    @Test
    public void shouldReturnEmptyMapIfNoResults() {
        // given
        when(draftService.userReport(anyString()))
            .thenReturn(Collections.emptyMap());

        // when
        Map<String, Integer> results = controller.getDocumentTypeCounts("userId", "authHeader");

        // then
        assertThat(results).isEmpty();
    }

    @Test
    public void shouldPassThroughResults() {
        // given
        when(draftService.userReport(anyString()))
            .thenReturn(ImmutableMap.of("some_type", 2, "another_type", 3));

        // when
        Map<String, Integer> results = controller.getDocumentTypeCounts("userId", "authHeader");

        // then
        assertThat(results).containsOnlyKeys("some_type", "another_type");
        assertThat(results.get("some_type")).isEqualTo(2);
        assertThat(results.get("another_type")).isEqualTo(3);
    }

    private InvalidIdamTokenException getAuthenticationFailedException() {
        return new InvalidIdamTokenException("expected exception", new HttpClientErrorException(HttpStatus.FORBIDDEN));
    }
}
