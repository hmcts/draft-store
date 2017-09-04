package uk.gov.hmcts.reform.draftstore.endpoint.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorResult {
    private ErrorCode errorCode;
    private List<String> errors = new ArrayList<>();

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public List<String> getErrors() {
        return errors;
    }

    public static class Builder {
        private ErrorCode errorCode;
        private List<String> errors;

        public static Builder errorResultBuilder(ErrorCode errorCode) {
            Builder builder = new Builder();
            builder.errorCode = errorCode;
            builder.errors = new ArrayList<>();
            return builder;
        }

        public Builder withError(String error) {
            if (!StringUtils.isEmpty(error)) {
                errors.add(error);
            }
            return this;
        }

        public ErrorResult build() {
            ErrorResult instance = new ErrorResult();
            instance.errorCode = errorCode;
            instance.errors = errors;
            return instance;
        }
    }
}
