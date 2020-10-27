package uk.gov.hmcts.reform.draftstore.controllers;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import uk.gov.hmcts.reform.draftstore.data.DraftStoreDao;
import uk.gov.hmcts.reform.draftstore.service.AuthService;
import uk.gov.hmcts.reform.draftstore.service.DraftService;

@TestConfiguration
public class DraftControllerProviderTestConfiguration {

    @MockBean
    public DraftStoreDao draftStoreDao;

    @MockBean
    public AuthService authService;

    @Bean
    @Primary
    public DraftService draftService() {
        return new DraftService(draftStoreDao);
    }

    @Bean
    @Primary
    public DraftController draftController() {
        return new DraftController(authService, draftService());
    }

}
