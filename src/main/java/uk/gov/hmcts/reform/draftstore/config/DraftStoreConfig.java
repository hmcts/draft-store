package uk.gov.hmcts.reform.draftstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.handler.MappedInterceptor;
import uk.gov.hmcts.reform.api.deprecated.DeprecatedApiInterceptor;
import uk.gov.hmcts.reform.draftstore.data.DraftStoreDAO;
import uk.gov.hmcts.reform.draftstore.service.UserIdentificationService;
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
    public MappedInterceptor deprecatedApiInterceptor() {
        return new MappedInterceptor(null, new DeprecatedApiInterceptor());
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
    public RequestIdsSettingFilter requestIdLoggingFilter() {
        return new RequestIdsSettingFilter();
    }

    @Bean
    public RequestStatusLoggingFilter requestStatusLoggingFilter() {
        return new RequestStatusLoggingFilter();
    }
}
