package uk.gov.hmcts.reform.draftstore.filters;

import org.junit.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class RequestTraceFilterTest {

    @Test
    public void should_filter_out_sensitive_headers() throws Exception {

        Map<String, Object> headers = new HashMap<>();
        headers.put("a", "xxx");
        headers.put("b", "xxx");
        headers.put("c", "xxx");
        RequestTraceFilter
            .SENSITIVE_HEADERS
            .forEach(h -> {
                headers.put(h, "some_value");
                headers.put(h.toLowerCase(Locale.ENGLISH), "some_value");
                headers.put(h.toUpperCase(Locale.ENGLISH), "some_value");
            });

        new RequestTraceFilter(null, null).postProcessRequestHeaders(headers);

        assertThat(headers)
            .containsExactly(
                entry("a", "xxx"),
                entry("b", "xxx"),
                entry("c", "xxx")
            );
    }
}
