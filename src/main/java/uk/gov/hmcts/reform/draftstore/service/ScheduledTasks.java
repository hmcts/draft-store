package uk.gov.hmcts.reform.draftstore.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.draftstore.data.DraftStoreDao;

@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private final DraftStoreDao repo;

    public ScheduledTasks(DraftStoreDao repo) {
        this.repo = repo;
    }

    @Scheduled(cron = "${maxStaleDays.cron}")
    public void deleteStaleDrafts() {
        log.info("Deleting stale drafts...");
        int count = repo.deleteStaleDrafts();
        log.info("Deleted {} stale drafts", count);
    }
}
