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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import uk.gov.hmcts.reform.draftstore.data.DraftStoreDAO;
import uk.gov.hmcts.reform.draftstore.domain.CreateDraft;
import uk.gov.hmcts.reform.draftstore.service.AuthService;
import uk.gov.hmcts.reform.draftstore.service.idam.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.reform.draftstore.service.AuthService.SERVICE_HEADER;

@RunWith(SpringRunner.class)
@WebMvcTest(DraftController.class)
public class CreateTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private DraftStoreDAO draftRepo;
    @MockBean private AuthService authService;

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
        send("{ \"type\": \"some_type\", \"document\": {\"a\":\"b\"} }")
            .andExpect(status().is(201));
    }

    @Test
    public void should_fill_location_header_on_successful_save() throws Exception {
        final int newClaimId = 444;

        BDDMockito
            .given(draftRepo.insert(anyString(), anyString(), any(CreateDraft.class)))
            .willReturn(newClaimId);

        MvcResult result = send("{ \"type\": \"some_type\", \"document\": {\"a\":\"b\"} }").andReturn();

        assertThat(result.getResponse().getHeader(LOCATION)).endsWith("/drafts/" + newClaimId);
    }

    private ResultActions send(String content) throws Exception {
        return mockMvc
            .perform(
                post("/drafts")
                    .header(AUTHORIZATION, "auth-header-value")
                    .header(SERVICE_HEADER, "some_service_name")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(content)
            );
    }
}
