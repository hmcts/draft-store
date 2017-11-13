package uk.gov.hmcts.reform.draftstore.controllers.draftcontroller;

import com.google.common.base.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.internal.matchers.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import uk.gov.hmcts.reform.draftstore.controllers.DraftController;
import uk.gov.hmcts.reform.draftstore.domain.UpdateDraft;
import uk.gov.hmcts.reform.draftstore.exception.AuthorizationException;
import uk.gov.hmcts.reform.draftstore.exception.NoDraftFoundException;
import uk.gov.hmcts.reform.draftstore.service.AuthService;
import uk.gov.hmcts.reform.draftstore.service.DraftService;
import uk.gov.hmcts.reform.draftstore.service.UserAndService;

import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isNull;
import static org.mockito.internal.matchers.Null.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.WARNING;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.reform.draftstore.service.AuthService.SECRET_HEADER;
import static uk.gov.hmcts.reform.draftstore.service.AuthService.SERVICE_HEADER;
import static uk.gov.hmcts.reform.draftstore.service.secrets.Secrets.MIN_SECRET_LENGTH;

@RunWith(SpringRunner.class)
@WebMvcTest(DraftController.class)
public class UpdateTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private DraftService draftService;
    @MockBean private AuthService authService;

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
    public void should_return_400_when_secret_is_not_long_enough() throws Exception {
        sendUpdate(Strings.repeat("x", MIN_SECRET_LENGTH - 1)).andExpect(status().isBadRequest());
    }

    @Test
    public void should_return_404_when_no_draft_found_exception_is_thrown() throws Exception {
        willThrow(new NoDraftFoundException())
            .given(draftService)
            .update(anyString(), any(UpdateDraft.class), any(UserAndService.class));

        sendUpdate().andExpect(status().isNotFound());
    }

    @Test
    public void should_set_warning_header_when_encryption_header_is_not_provided() throws Exception {
        sendUpdate()
            .andExpect(header().string(WARNING, NULL));
    }

    @Test
    public void should_NOT_set_warning_header_when_encryption_header_is_provided() throws Exception {
        sendUpdate(Strings.repeat("?", MIN_SECRET_LENGTH))
            .andExpect(status().isBadRequest());

    }

    private ResultActions sendUpdate() throws Exception {
        return sendUpdate(null);
    }

    private ResultActions sendUpdate(String secret) throws Exception {
        BDDMockito
            .given(authService.authenticate(anyString(), anyString()))
            .willReturn(new UserAndService("john", "service"));

        MockHttpServletRequestBuilder request =
            put("/drafts/123")
                .header(AUTHORIZATION, "abc")
                .header(SERVICE_HEADER, "xyz")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"type\": \"some_type\", \"document\": {\"a\":\"b\"} }");

        return mockMvc.perform(secret == null ? request : request.header(SECRET_HEADER, secret));
    }
}
