package uk.gov.hmcts.reform.draftstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import uk.gov.hmcts.reform.draftstore.data.DraftStoreDAO;
import uk.gov.hmcts.reform.draftstore.service.UserIdentificationService;
import uk.gov.hmcts.reform.logging.filters.RequestIdLoggingFilter;
import uk.gov.hmcts.reform.logging.filters.RequestStatusLoggingFilter;

@Configuration
public class DraftStoreConfig {
    @Bean
    MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Bean
    public DraftStoreDAO draftDocumentDAO(NamedParameterJdbcTemplate jdbcTemplate) {
        return new DraftStoreDAO(jdbcTemplate);
    }

    @Bean
    public UserIdentificationService userIdentificationService() {
        return new UserIdentificationService();
    }

    @Bean
    public RequestIdLoggingFilter requestIdLoggingFilter() {
        return new RequestIdLoggingFilter();
    }

    @Bean
    public RequestStatusLoggingFilter requestStatusLoggingFilter() {
        return new RequestStatusLoggingFilter();
    }
}
