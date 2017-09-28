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
import uk.gov.hmcts.reform.draftstore.endpoint.v3.helpers.SampleData;
import uk.gov.hmcts.reform.draftstore.service.AuthService;
import uk.gov.hmcts.reform.draftstore.service.UserAndService;

import java.util.Optional;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.reform.draftstore.service.AuthService.SERVICE_HEADER;

@RunWith(SpringRunner.class)
@WebMvcTest(DraftController.class)
public class UpdateTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private DraftStoreDAO draftRepo;
    @MockBean private AuthService authService;

    private static final int existingDraftId = 123;
    private final Draft existingDraft = SampleData.draft(Integer.toString(existingDraftId));

    @Before
    public void setUp() throws Exception {
        BDDMockito
            .given(draftRepo.read(anyInt()))
            .willReturn(Optional.empty());

        BDDMockito
            .given(draftRepo.read(eq(existingDraftId)))
            .willReturn(Optional.of(existingDraft));

        BDDMockito
            .given(authService.authenticate(anyString(), anyString()))
            .willReturn(
                new UserAndService(
                    "definitely_not_" + existingDraft.userId,
                    "definitely_not_" + existingDraft.service
                )
            );

        BDDMockito
            .given(authService.authenticate(existingDraft.userId, existingDraft.service))
            .willReturn(
                new UserAndService(
                    existingDraft.userId,
                    existingDraft.service
                )
            );
    }

    @Test
    public void should_return_403_when_trying_to_update_somebody_elses_draft() throws Exception {
        update(existingDraftId, "villain", existingDraft.service)
            .andExpect(status().isForbidden());
    }

    public void should_return_403_when_trying_to_update_draft_from_a_different_service() throws Exception {
        update(existingDraftId, existingDraft.userId, "wrong_service");
    }

    @Test
    public void should_return_204_when_updating_own_draft() throws Exception {
        update(existingDraftId, existingDraft.userId, existingDraft.service)
            .andExpect(status().isNoContent());
    }

    @Test
    public void should_return_404_when_draft_with_given_id_doesnt_exist() throws Exception {
        update(existingDraftId + 123, "some_user", "some_service")
            .andExpect(status().isNotFound());
    }

    private ResultActions update(int draftId, String userId, String service) throws Exception {
        return mockMvc
            .perform(
                put("/drafts/" + draftId)
                    .header(AUTHORIZATION, userId)
                    .header(SERVICE_HEADER, service)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{ \"type\": \"some_type\", \"document\": {\"a\":\"b\"} }")
            );
    }
}
