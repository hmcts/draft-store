package uk.gov.hmcts.reform.draftstore.controllers.draftcontroller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import uk.gov.hmcts.reform.draftstore.controllers.DraftController;
import uk.gov.hmcts.reform.draftstore.exception.AuthorizationException;
import uk.gov.hmcts.reform.draftstore.service.AuthService;
import uk.gov.hmcts.reform.draftstore.service.DraftService;
import uk.gov.hmcts.reform.draftstore.service.UserAndService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.reform.draftstore.service.AuthService.SERVICE_HEADER;

@RunWith(SpringRunner.class)
@WebMvcTest(DraftController.class)
public class DeleteTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private DraftService draftService;
    @MockBean private AuthService authService;

    @Test
    public void should_return_403_when_auth_exception_is_thrown() throws Exception {
        given(authService.authenticate(anyString(), anyString())).willReturn(mock(UserAndService.class));
        willThrow(new AuthorizationException())
            .given(draftService)
            .delete(anyString(), any(UserAndService.class));

        sendDelete().andExpect(status().isForbidden());
    }

    @Test
    public void should_return_204_when_deleting_succeeded() throws Exception {
        sendDelete().andExpect(status().isNoContent());
    }

    private ResultActions sendDelete() throws Exception {
        return mockMvc
            .perform(
                delete("/drafts/123")
                    .header(AUTHORIZATION, "abc")
                    .header(SERVICE_HEADER, "xyz")
                    .contentType(MediaType.APPLICATION_JSON)
            );
    }
}
