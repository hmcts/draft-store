package uk.gov.hmcts.reform.draftstore.endpoint.v3;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.draftstore.data.DraftStoreDAO;
import uk.gov.hmcts.reform.draftstore.service.UserIdentificationService;

import java.util.Collections;

import static org.mockito.Matchers.anyString;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(DraftController.class)
public class GetAll {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private DraftStoreDAO draftRepo;
    @MockBean private UserIdentificationService userIdentificationService;

    @Test
    public void should_return_empty_list_and_200_when_no_drafts_were_found_in_db() throws Exception {
        BDDMockito
            .given(draftRepo.readAll(anyString(), anyString()))
            .willReturn(Collections.emptyList());

        BDDMockito
            .given(userIdentificationService.userIdFromAuthToken(anyString()))
            .willReturn("x");

        mockMvc
            .perform(get("/drafts?type=default").header(AUTHORIZATION, "auth-header-value"))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
    }
}
