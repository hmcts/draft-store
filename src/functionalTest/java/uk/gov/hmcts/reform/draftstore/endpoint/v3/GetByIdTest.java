package uk.gov.hmcts.reform.draftstore.endpoint.v3;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.hmcts.reform.draftstore.data.DraftStoreDAO;
import uk.gov.hmcts.reform.draftstore.domain.Draft;
import uk.gov.hmcts.reform.draftstore.endpoint.v3.helpers.SampleData;
import uk.gov.hmcts.reform.draftstore.service.AuthService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static uk.gov.hmcts.reform.draftstore.service.AuthService.SERVICE_HEADER;

@RunWith(SpringRunner.class)
@WebMvcTest(DraftController.class)
public class GetByIdTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private DraftStoreDAO draftRepo;
    @MockBean private AuthService authService;

    private final Draft sampleDraft = SampleData.draft("123");

    @Test
    public void reading_not_existing_draft_returns_404() throws Exception {
        testStatus("abc", "serviceA", null, HttpStatus.NOT_FOUND);
    }

    @Test
    public void reading_own_existing_draft_returns_200() throws Exception {
        testStatus("abc", "serviceA", sampleDraft, HttpStatus.OK);
    }

    @Test
    public void reading_somebody_elses_draft_returns_404() throws Exception {
        testStatus("XXX", "serviceA", sampleDraft, HttpStatus.NOT_FOUND);
    }

    @Test
    public void reading_own_draft_from_different_service_returns_404() throws Exception {
        testStatus("abc", "WRONG_SERVICE", sampleDraft, HttpStatus.NOT_FOUND);
    }

    private void testStatus(
        String userId,
        String service,
        Draft draftInDb,
        HttpStatus expectedStatus
    ) throws Exception {
        // given
        BDDMockito
            .given(draftRepo.read(anyInt()))
            .willReturn(Optional.ofNullable(draftInDb));

        BDDMockito
            .given(authService.getUserId(anyString()))
            .willReturn(userId);

        BDDMockito
            .given(authService.getServiceName(anyString()))
            .willReturn(service);

        // when
        MvcResult result =
            mockMvc
                .perform(
                    get("/drafts/123")
                        .header(AUTHORIZATION, "irrelevant-header")
                        .header(SERVICE_HEADER, "irrelevant-service-name")
                ).andReturn();

        // then
        assertThat(result.getResponse().getStatus()).isEqualTo(expectedStatus.value());
    }
}
