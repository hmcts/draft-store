package uk.gov.hmcts.reform.draftstore;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import uk.gov.hmcts.reform.draftstore.data.DataAgent;

@Configuration
@Profile("test")
public class TestConfig {
    @Bean
    public DataAgent dataAgent(NamedParameterJdbcTemplate jdbcTemplate) {
        return new DataAgent( jdbcTemplate );
    }
}
