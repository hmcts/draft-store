package uk.gov.hmcts.reform.draftstore.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import uk.gov.hmcts.reform.draftstore.data.DraftStoreDAO;
import uk.gov.hmcts.reform.draftstore.service.idam.IdamClient;
import uk.gov.hmcts.reform.draftstore.service.idam.IdamClientImpl;
import uk.gov.hmcts.reform.draftstore.service.idam.IdamClientStub;
import uk.gov.hmcts.reform.draftstore.service.s2s.S2sClient;
import uk.gov.hmcts.reform.draftstore.service.s2s.S2sClientImpl;
import uk.gov.hmcts.reform.draftstore.service.s2s.S2sClientStub;
import uk.gov.hmcts.reform.logging.filters.RequestIdsSettingFilter;
import uk.gov.hmcts.reform.logging.filters.RequestStatusLoggingFilter;

import java.time.Clock;
import javax.servlet.Filter;

@Configuration
public class DraftStoreConfig {

    @Value("${idam.url}") private String idamUrl;
    @Value("${s2s.url}") private String s2sUrl;
    @Value("${maxStaleDays.default}") private int maxStaleDaysDefault;

    @Bean
    MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Bean
    public Filter etagFilter() {
        return new ShallowEtagHeaderFilter();
    }

    @Bean
    public DraftStoreDAO draftDocumentDAO(NamedParameterJdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        return new DraftStoreDAO(
            jdbcTemplate,
            objectMapper,
            maxStaleDaysDefault,
            Clock.systemDefaultZone()
        );
    }

    @Bean
    @ConditionalOnProperty(name = "idam.useStub", havingValue = "true")
    public IdamClient idamClientStub() {
        return new IdamClientStub();
    }

    @Bean
    @ConditionalOnProperty(name = "idam.useStub", havingValue = "false")
    public IdamClient idamClient() {
        return new IdamClientImpl(idamUrl);
    }

    @Bean
    @ConditionalOnProperty(name = "s2s.useStub", havingValue = "false")
    public S2sClient s2sClient() {
        return new S2sClientImpl(s2sUrl);
    }

    @Bean
    @ConditionalOnProperty(name = "s2s.useStub", havingValue = "true")
    public S2sClient s2sClientStub() {
        return new S2sClientStub();
    }

    @Bean
    public RequestIdsSettingFilter requestIdLoggingFilter() {
        return new RequestIdsSettingFilter();
    }

    @Bean
    public RequestStatusLoggingFilter requestStatusLoggingFilter() {
        return new RequestStatusLoggingFilter();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setDateFormat(new ISO8601DateFormat());
    }
}
