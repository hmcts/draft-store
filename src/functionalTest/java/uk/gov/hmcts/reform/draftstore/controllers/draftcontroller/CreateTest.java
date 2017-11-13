package uk.gov.hmcts.reform.draftstore.controllers.draftcontroller;

import com.google.common.base.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import uk.gov.hmcts.reform.draftstore.controllers.DraftController;
import uk.gov.hmcts.reform.draftstore.domain.CreateDraft;
import uk.gov.hmcts.reform.draftstore.service.AuthService;
import uk.gov.hmcts.reform.draftstore.service.DraftService;
import uk.gov.hmcts.reform.draftstore.service.UserAndService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpHeaders.WARNING;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.reform.draftstore.service.AuthService.SECRET_HEADER;
import static uk.gov.hmcts.reform.draftstore.service.AuthService.SERVICE_HEADER;
import static uk.gov.hmcts.reform.draftstore.service.secrets.Secrets.MIN_SECRET_LENGTH;

@RunWith(SpringRunner.class)
@WebMvcTest(DraftController.class)
public class CreateTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private DraftService draftService;
    @MockBean private AuthService authService;

    private static String validDraft = "{ \"type\": \"some_type\", \"document\": {\"a\":\"b\"} }";

    // region document
    @Test
    public void should_return_400_when_document_is_not_provided() throws Exception {
        send("{ \"type\": \"some_type\" }")
            .andExpect(status().is(400));
    }

    @Test
    public void should_return_400_when_document_is_null() throws Exception {
        send("{ \"type\": \"some_type\", \"document\": null }")
            .andExpect(status().is(400));
    }

    @Test
    public void should_return_400_when_document_is_not_json_object() throws Exception {
        send("{ \"type\": \"some_type\", \"document\": 123 }")
            .andExpect(status().is(400));
    }
    // endregion document

    // region type

    @Test
    public void should_return_400_when_type_is_not_provided() throws Exception {
        send("{\"document\": {\"a\":\"b\"} }")
            .andExpect(status().is(400));
    }

    // endregion

    @Test
    public void should_return_201_when_valid_draft_is_sent() throws Exception {
        send(validDraft)
            .andExpect(status().is(201));
    }

    @Test
    public void should_return_400_when_secret_is_not_long_enough() throws Exception {
        send(
            validDraft,
            Strings.repeat("x", MIN_SECRET_LENGTH - 1)
        ).andExpect(status().is(400));
    }

    @Test
    public void should_fill_location_header_on_successful_save() throws Exception {
        final int newClaimId = 444;

        BDDMockito
            .given(draftService.create(any(CreateDraft.class), any(UserAndService.class)))
            .willReturn(newClaimId);

        MvcResult result = send(validDraft).andReturn();

        assertThat(result.getResponse().getHeader(LOCATION)).endsWith("/drafts/" + newClaimId);
    }

    @Test
    public void should_add_warning_header_when_encryption_secret_is_not_provided() throws Exception {
        MvcResult result = send(validDraft, null).andReturn();

        assertThat(result.getResponse().getHeader(WARNING))
            .as("Warning header")
            .isNotBlank();
    }

    @Test
    public void should_NOT_add_warning_header_when_encryption_secret_is_provided() throws Exception {
        MvcResult result =
            send(
                validDraft,
                Strings.repeat("x", MIN_SECRET_LENGTH)
            ).andReturn();

        assertThat(result.getResponse().getHeader(WARNING)).isNull();
    }

    private ResultActions send(String content) throws Exception {
        return send(content, null);
    }

    private ResultActions send(String content, String secret) throws Exception {
        BDDMockito
            .given(authService.authenticate(anyString(), anyString()))
            .willReturn(new UserAndService("john", "service"));

        MockHttpServletRequestBuilder request =
            post("/drafts")
                .header(AUTHORIZATION, "auth-header-value")
                .header(SERVICE_HEADER, "some_service_name")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        return mockMvc.perform(secret == null ? request : request.header(SECRET_HEADER, secret));
    }
}
