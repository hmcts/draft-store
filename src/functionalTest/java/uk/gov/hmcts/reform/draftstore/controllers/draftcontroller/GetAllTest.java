package uk.gov.hmcts.reform.draftstore.controllers.draftcontroller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.draftstore.controllers.DraftController;
import uk.gov.hmcts.reform.draftstore.controllers.helpers.SampleData;
import uk.gov.hmcts.reform.draftstore.domain.DraftList;
import uk.gov.hmcts.reform.draftstore.service.AuthService;
import uk.gov.hmcts.reform.draftstore.service.DraftService;
import uk.gov.hmcts.reform.draftstore.service.UserAndService;

import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.reform.draftstore.service.AuthService.SECRET_HEADER;
import static uk.gov.hmcts.reform.draftstore.service.AuthService.SERVICE_HEADER;

@RunWith(SpringRunner.class)
@WebMvcTest(DraftController.class)
public class GetAllTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private DraftService draftService;
    @MockBean private AuthService authService;

    @Test
    public void should_return_empty_list_and_200_when_no_drafts_were_found_in_db() throws Exception {
        BDDMockito
            .given(authService.authenticate(anyString(), anyString()))
            .willReturn(new UserAndService("john", "service"));

        BDDMockito
            .given(draftService.read(any(UserAndService.class), anyInt(), anyInt()))
            .willReturn(new DraftList(Collections.emptyList()));

        mockMvc
            .perform(
                get("/drafts?type=default")
                    .header(AUTHORIZATION, "auth-header-value")
                    .header(SERVICE_HEADER, "some_service_name")
                    .header(SECRET_HEADER, SampleData.secret())
            )
            .andExpect(status().isOk())
            .andExpect(content().json("{ \"data\": [] }"));
    }
}
