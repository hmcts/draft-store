package uk.gov.hmcts.reform.draftstore.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.IOException;
import java.util.Map;

public class JsonConstraintValidator implements ConstraintValidator<ValidJson, String> {
    private ObjectMapper objectMapper;

    public JsonConstraintValidator() {
        objectMapper = new ObjectMapper();
    }

    @Override
    public void initialize(ValidJson constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            objectMapper.readValue(value, Map.class);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
