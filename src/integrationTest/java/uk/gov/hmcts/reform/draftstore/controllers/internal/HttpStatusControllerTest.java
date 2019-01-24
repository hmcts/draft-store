package uk.gov.hmcts.reform.draftstore.controllers.internal;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(HttpStatusController.class)
public class HttpStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void should_return_500_when_no_status_code_is_passed() throws Exception {
        mockMvc.perform(get("/internal/debug/status"))
            .andExpect(status().is(500));
    }

    @Test
    public void should_return_418_when_you_ask_for_teapot_status() throws Exception {
        mockMvc.perform(get("/internal/debug/status?statusCode=418"))
            .andExpect(status().is(418));
    }
}
