package uk.gov.hmcts.reform.draftstore.endpoint.v3;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import uk.gov.hmcts.reform.draftstore.data.DraftStoreDAO;
import uk.gov.hmcts.reform.draftstore.domain.Draft;
import uk.gov.hmcts.reform.draftstore.service.AuthService;

import java.util.Optional;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.reform.draftstore.service.AuthService.SERVICE_HEADER;

@RunWith(SpringRunner.class)
@WebMvcTest(DraftController.class)
public class DeleteTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private DraftStoreDAO draftRepo;
    @MockBean private AuthService authService;

    private static final int existingDraftId = 123;
    private final Draft existingDraft = new Draft(Integer.toString(existingDraftId), "user", "serviceA", "doc", "type");

    @Before
    public void setUp() throws Exception {
        BDDMockito
            .given(draftRepo.read(anyInt()))
            .willReturn(Optional.empty());

        BDDMockito
            .given(draftRepo.read(eq(existingDraftId)))
            .willReturn(Optional.of(existingDraft));

        BDDMockito
            .given(authService.getUserId(anyString()))
            .willReturn("definitely_not_" + existingDraft.userId);

        BDDMockito
            .given(authService.getUserId(existingDraft.userId))
            .willReturn(existingDraft.userId);

        BDDMockito
            .given(authService.getServiceName(anyString()))
            .willReturn("definitely_not_" + existingDraft.service);

        BDDMockito
            .given(authService.getServiceName(existingDraft.service))
            .willReturn(existingDraft.service);
    }

    @Test
    public void should_return_403_when_trying_to_delete_somebody_elses_draft() throws Exception {
        remove(existingDraftId, "villain", existingDraft.service)
            .andExpect(status().isForbidden());
    }

    @Test
    public void should_return_403_when_trying_to_delete_draft_from_aonther_service() throws Exception {
        remove(existingDraftId, existingDraft.userId, "NOT_" + existingDraft.service)
            .andExpect(status().isForbidden());
    }

    @Test
    public void should_return_204_when_deleting_own_draft() throws Exception {
        remove(existingDraftId, existingDraft.userId, existingDraft.service)
            .andExpect(status().isNoContent());
    }

    @Test
    public void should_return_204_when_draft_with_given_id_doesnt_exist() throws Exception {
        remove(existingDraftId + 123, "some_user", "some_service")
            .andExpect(status().isNoContent());
    }

    private ResultActions remove(int draftId, String userId, String service) throws Exception {
        return mockMvc
            .perform(
                delete("/drafts/" + draftId)
                    .header(AUTHORIZATION, userId)
                    .header(SERVICE_HEADER, service)
                    .contentType(MediaType.APPLICATION_JSON)
            );
    }
}
