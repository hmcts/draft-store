package uk.gov.hmcts.reform.draftstore.service.draftservice;

import org.junit.Before;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import uk.gov.hmcts.reform.draftstore.data.DraftStoreDao;
import uk.gov.hmcts.reform.draftstore.data.model.Draft;
import uk.gov.hmcts.reform.draftstore.service.DraftService;
import uk.gov.hmcts.reform.draftstore.service.UserAndService;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;

public class BaseTest {

    @Mock
    DraftStoreDao repo;
    DraftService draftService;

    @Before
    public void setUp() throws Exception {
        this.draftService = new DraftService(repo);
    }

    Draft draftCreatedBy(UserAndService userAndService) {
        return new Draft(
            "123",
            userAndService.userId,
            userAndService.service,
            "{}",
            null,
            "some_type",
            ZonedDateTime.now(),
            ZonedDateTime.now()
        );
    }

    void thereExists(Draft draft) {
        BDDMockito
            .given(repo.read(anyInt()))
            .willReturn(Optional.ofNullable(draft));
    }
}
