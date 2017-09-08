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
import uk.gov.hmcts.reform.draftstore.service.UserIdentificationService;

import java.util.Optional;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(DraftController.class)
public class UpdateTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private DraftStoreDAO draftRepo;
    @MockBean private UserIdentificationService userIdentificationService;

    private final Draft existingDraft = new Draft(123, "user", "doc", "type");

    @Before
    public void setUp() throws Exception {
        BDDMockito
            .given(draftRepo.read(anyInt()))
            .willReturn(Optional.empty());

        BDDMockito
            .given(draftRepo.read(eq(existingDraft.id)))
            .willReturn(Optional.of(existingDraft));

        BDDMockito
            .given(userIdentificationService.userIdFromAuthToken(anyString()))
            .willReturn("definitely_not_" + existingDraft.userId);

        BDDMockito
            .given(userIdentificationService.userIdFromAuthToken(existingDraft.userId))
            .willReturn(existingDraft.userId);
    }

    @Test
    public void should_return_404_when_trying_to_update_somebody_elses_draft() throws Exception {
        update(existingDraft.id, "villain")
            .andExpect(status().isNotFound());
    }

    @Test
    public void should_return_204_when_updating_own_draft() throws Exception {
        update(existingDraft.id, existingDraft.userId)
            .andExpect(status().isNoContent());
    }

    @Test
    public void should_return_404_when_draft_with_given_id_doesnt_exist() throws Exception {
        update(existingDraft.id + 123, "some_user")
            .andExpect(status().isNotFound());
    }

    private ResultActions update(int draftId, String userId) throws Exception {
        return mockMvc
            .perform(
                put("/drafts/" + draftId)
                    .header(AUTHORIZATION, userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{ \"type\": \"some_type\", \"document\": {\"a\":\"b\"} }")
            );
    }
}
