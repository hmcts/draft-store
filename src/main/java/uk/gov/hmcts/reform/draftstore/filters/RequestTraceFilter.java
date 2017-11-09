package uk.gov.hmcts.reform.draftstore.filters;

import org.springframework.boot.actuate.trace.TraceProperties;
import org.springframework.boot.actuate.trace.TraceRepository;
import org.springframework.boot.actuate.trace.WebRequestTraceFilter;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static uk.gov.hmcts.reform.draftstore.service.AuthService.SECRET_HEADER;
import static uk.gov.hmcts.reform.draftstore.service.AuthService.SERVICE_HEADER;

public class RequestTraceFilter extends WebRequestTraceFilter {

    public static final List<String> SENSITIVE_HEADERS =
        asList(
            SECRET_HEADER,
            SERVICE_HEADER
        );

    public RequestTraceFilter(TraceRepository repository, TraceProperties properties) {
        super(repository, properties);
    }

    @Override
    protected void postProcessRequestHeaders(Map<String, Object> headers) {
        headers
            .keySet()
            .stream()
            .filter(header -> shouldBeRemoved(header))
            .collect(toSet())
            .forEach(headers::remove);
    }

    private boolean shouldBeRemoved(String header) {
        return SENSITIVE_HEADERS
            .stream()
            .anyMatch(sensitiveHeader -> header.equalsIgnoreCase(sensitiveHeader));
    }
}
