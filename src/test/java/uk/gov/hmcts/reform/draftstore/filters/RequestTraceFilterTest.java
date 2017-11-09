package uk.gov.hmcts.reform.draftstore.filters;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.draftstore.service.AuthService.SECRET_HEADER;
import static uk.gov.hmcts.reform.draftstore.service.AuthService.SERVICE_HEADER;

public class RequestTraceFilterTest {

    @Test
    public void should_filter_out_sensitive_headers() throws Exception {

        Map<String, Object> headers = new HashMap<>();
        headers.put(SECRET_HEADER, "xxx");
        headers.put(SECRET_HEADER.toLowerCase(), "xxx");
        headers.put(SERVICE_HEADER, "xxx");
        headers.put(SERVICE_HEADER.toLowerCase(), "xxx");
        headers.put("a", "xxx");
        headers.put("b", "xxx");
        headers.put("c", "xxx");

        RequestTraceFilter filter = new RequestTraceFilter(null, null);
        filter.postProcessRequestHeaders(headers);

        assertThat(headers.keySet())
            .as("remaining headers")
            .containsExactly("a", "b", "c");
    }
}
