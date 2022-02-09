package uk.gov.hmcts.reform.draftstore.service.draftservice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.reform.draftstore.domain.DraftList;
import uk.gov.hmcts.reform.draftstore.service.UserAndService;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class ReadManyTest extends BaseTest {

    @Test
    public void should_return_empty_list_when_no_drafts_were_found() {
        // given
        BDDMockito
            .given(repo.readAll(anyString(), anyString(), anyInt(), anyInt()))
            .willReturn(emptyList());

        // when
        DraftList result = draftService.read(new UserAndService("john", "service"), 10, 10);

        // then
        assertThat(result).isNotNull();
        assertThat(result.data).isEmpty();
    }

    @Test
    public void should_return_list_of_drafts() {
        // given
        BDDMockito
            .given(repo.readAll(anyString(), anyString(), anyInt(), anyInt()))
            .willReturn(singletonList(draftCreatedBy(new UserAndService("john", "service"))));

        // when
        DraftList result = draftService.read(new UserAndService("john", "service"), 10, 10);

        // then
        assertThat(result).isNotNull();
        assertThat(result.data).hasSize(1);
    }
}
