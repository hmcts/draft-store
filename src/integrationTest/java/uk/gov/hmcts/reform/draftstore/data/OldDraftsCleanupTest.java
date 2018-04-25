package uk.gov.hmcts.reform.draftstore.data;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.reform.draftstore.controllers.helpers.SampleData;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.function.Consumer;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource("/database.properties")
@ActiveProfiles("test")
public class OldDraftsCleanupTest {

    @Autowired private NamedParameterJdbcTemplate jdbcTemplate;

    private final Instant now = now();

    @Test
    public void should_remove_stale_drafts() throws Exception {
        DraftStoreDao nowRepo = repoAtTime(now);

        int d1 = nowRepo.insert("", "", SampleData.createDraft(5));
        int d2 = nowRepo.insert("", "", SampleData.createDraft(15));

        after(
            Duration.of(10, DAYS),
            repo -> {
                repo.deleteStaleDrafts();
                assertThat(repo.read(d1)).isEmpty();
                assertThat(repo.read(d2)).isNotEmpty();
            }
        );
    }

    @Test
    public void should_NOT_remove_old_drafts_that_were_recently_updated() throws Exception {

        int newDraftId = repoAtTime(now).insert("", "", SampleData.createDraft(10));

        after(
            Duration.of(9, DAYS),
            repo -> repo.update(newDraftId, SampleData.updateDraft())
        );

        after(
            Duration.of(11, DAYS),
            repo -> {
                repo.deleteStaleDrafts();
                assertThat(repo.read(newDraftId)).isNotEmpty();
            }
        );
    }

    private DraftStoreDao repoAtTime(Instant instant) {
        return new DraftStoreDao(
            jdbcTemplate,
            0,
            Clock.fixed(instant, ZoneId.systemDefault())
        );
    }

    private void after(Duration duration, Consumer<DraftStoreDao> daoOperation) {
        DraftStoreDao dao = repoAtTime(now.plus(duration));
        daoOperation.accept(dao);
    }
}
