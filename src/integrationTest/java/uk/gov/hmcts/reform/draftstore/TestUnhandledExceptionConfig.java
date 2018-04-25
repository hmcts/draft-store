package uk.gov.hmcts.reform.draftstore;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import uk.gov.hmcts.reform.draftstore.data.DraftStoreDao;

import static org.mockito.Mockito.mock;

@Configuration
@Profile("test-unhandled-exception")
public class TestUnhandledExceptionConfig {
    @Bean
    @Primary
    public DraftStoreDao dao() {
        return mock(DraftStoreDao.class);
    }
}
