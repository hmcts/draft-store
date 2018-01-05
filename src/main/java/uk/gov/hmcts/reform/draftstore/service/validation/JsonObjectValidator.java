package uk.gov.hmcts.reform.draftstore.service.validation;

import com.fasterxml.jackson.databind.JsonNode;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class JsonObjectValidator implements ConstraintValidator<JsonObject, JsonNode> {

    @Override
    public void initialize(JsonObject constraintAnnotation) {
        // nothing to initialize
    }

    @Override
    public boolean isValid(JsonNode value, ConstraintValidatorContext context) {
        return value != null && value.isObject();
    }
}
