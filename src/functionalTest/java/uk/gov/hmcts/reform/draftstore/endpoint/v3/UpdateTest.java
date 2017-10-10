package uk.gov.hmcts.reform.draftstore.endpoint.v3;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import uk.gov.hmcts.reform.draftstore.domain.UpdateDraft;
import uk.gov.hmcts.reform.draftstore.exception.AuthorizationException;
import uk.gov.hmcts.reform.draftstore.exception.NoDraftFoundException;
import uk.gov.hmcts.reform.draftstore.service.AuthService;
import uk.gov.hmcts.reform.draftstore.service.DraftService;
import uk.gov.hmcts.reform.draftstore.service.UserAndService;

import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.reform.draftstore.service.AuthService.SERVICE_HEADER;

@RunWith(SpringRunner.class)
@WebMvcTest(DraftController.class)
public class UpdateTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private DraftService draftService;
    @MockBean private AuthService authService; //NOPMD


    @Test
    public void should_return_403_when_auth_exception_is_thrown() throws Exception {
        willThrow(new AuthorizationException())
            .given(draftService)
            .update(anyString(), any(UpdateDraft.class), any(UserAndService.class));

        sendUpdate().andExpect(status().isForbidden());
    }

    @Test
    public void should_return_204_when_update_went_fine() throws Exception {
        sendUpdate().andExpect(status().isNoContent());
    }

    @Test
    public void should_return_404_when_no_draft_found_exception_is_thrown() throws Exception {
        willThrow(new NoDraftFoundException())
            .given(draftService)
            .update(anyString(), any(UpdateDraft.class), any(UserAndService.class));

        sendUpdate().andExpect(status().isNotFound());
    }

    private ResultActions sendUpdate() throws Exception {
        return mockMvc
            .perform(
                put("/drafts/123")
                    .header(AUTHORIZATION, "abc")
                    .header(SERVICE_HEADER, "xyz")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{ \"type\": \"some_type\", \"document\": {\"a\":\"b\"} }")
            );
    }
}
