package uk.gov.hmcts.reform.draftstore.service.draftservice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.reform.draftstore.data.model.DocumentTypeCount;

import java.util.Arrays;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserReportTest extends BaseTest {
    @Test
    public void should_return_empty_map() {
        when(repo.getDraftTypeCountsByUser(anyString()))
            .thenReturn(emptyList());

        Map<String, Integer> results = draftService.userReport("1");

        assertThat(results).isEmpty();
    }

    @Test
    public void should_pass_through_results() {
        when(repo.getDraftTypeCountsByUser(anyString()))
            .thenReturn(Arrays.asList(
                new DocumentTypeCount("claim", 1),
                new DocumentTypeCount("response", 2)
            ));

        Map<String, Integer> results = draftService.userReport("1");

        assertThat(results).containsOnlyKeys("claim", "response");
        assertThat(results.get("claim")).isEqualTo(1);
        assertThat(results.get("response")).isEqualTo(2);
    }
}
