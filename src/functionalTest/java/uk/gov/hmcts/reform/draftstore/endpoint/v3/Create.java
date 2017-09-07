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
import uk.gov.hmcts.reform.draftstore.data.DraftStoreDAO;
import uk.gov.hmcts.reform.draftstore.service.UserIdentificationService;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(DraftController.class)
public class Create {

    @Autowired private MockMvc mockMvc;

    @MockBean private DraftStoreDAO draftRepo;
    @MockBean private UserIdentificationService userIdentificationService;

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

    @Test
    public void should_return_201_when_valid_draft_is_sent() throws Exception {
        send("{ \"type\": \"some_type\", \"document\": {\"a\":\"b\"} }")
            .andExpect(status().is(201));
    }

    private ResultActions send(String content) throws Exception {
        return mockMvc
            .perform(
                post("/drafts")
                    .header(AUTHORIZATION, "auth-header-value")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(content)
            );
    }
}
