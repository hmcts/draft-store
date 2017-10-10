package uk.gov.hmcts.reform.draftstore.endpoint.v3;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.hmcts.reform.draftstore.exception.NoDraftFoundException;
import uk.gov.hmcts.reform.draftstore.service.AuthService;
import uk.gov.hmcts.reform.draftstore.service.DraftService;
import uk.gov.hmcts.reform.draftstore.service.UserAndService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static uk.gov.hmcts.reform.draftstore.service.AuthService.SERVICE_HEADER;

@RunWith(SpringRunner.class)
@WebMvcTest(DraftController.class)
public class GetByIdTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private DraftService draftService;
    @MockBean private AuthService authService; //NOPMD - mock declaration required

    @Test
    public void should_map_no_draft_exception_to_404() throws Exception {
        BDDMockito
            .given(draftService.read(anyString(), any(UserAndService.class)))
            .willThrow(new NoDraftFoundException());

        int status = callGet();

        assertThat(status).isEqualTo(HttpStatus.NOT_FOUND.value());

    }

    @Test
    public void should_return_200_if_draft_is_found() throws Exception {
        int status = callGet();

        assertThat(status).isEqualTo(HttpStatus.OK.value());
    }

    private int callGet() throws Exception {
        MvcResult result =
            mockMvc
                .perform(
                    get("/drafts/123")
                        .header(AUTHORIZATION, "irrelevant-header")
                        .header(SERVICE_HEADER, "irrelevant-service-name")
                ).andReturn();

        return result.getResponse().getStatus();
    }
}
