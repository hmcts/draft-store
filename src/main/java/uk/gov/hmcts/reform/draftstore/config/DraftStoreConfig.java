package uk.gov.hmcts.reform.draftstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import uk.gov.hmcts.reform.draftstore.data.DraftStoreDAO;
import uk.gov.hmcts.reform.draftstore.service.AuthService;
import uk.gov.hmcts.reform.logging.filters.RequestIdsSettingFilter;
import uk.gov.hmcts.reform.logging.filters.RequestStatusLoggingFilter;

import javax.servlet.Filter;

@Configuration
public class DraftStoreConfig {
    @Bean
    MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Bean
    public Filter etagFilter() {
        return new ShallowEtagHeaderFilter();
    }

    @Bean
    public DraftStoreDAO draftDocumentDAO(NamedParameterJdbcTemplate jdbcTemplate) {
        return new DraftStoreDAO(jdbcTemplate);
    }

    @Bean
    public AuthService authService() {
        return new AuthService();
    }

    @Bean
    public RequestIdsSettingFilter requestIdLoggingFilter() {
        return new RequestIdsSettingFilter();
    }

    @Bean
    public RequestStatusLoggingFilter requestStatusLoggingFilter() {
        return new RequestStatusLoggingFilter();
    }
}
