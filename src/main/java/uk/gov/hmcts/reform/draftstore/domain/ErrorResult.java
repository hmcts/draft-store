package uk.gov.hmcts.reform.draftstore.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorResult {

    public final ErrorCode errorCode;
    public final List<String> errors;

    public ErrorResult(ErrorCode errorCode, List<String> errors) {
        this.errorCode = errorCode;
        this.errors = errors;
    }
}
