package uk.gov.hmcts.reform.draftstore.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.draftstore.data.DraftStoreDAO;

@Component
public class ScheduledTasks {

    private final DraftStoreDAO repo;

    public ScheduledTasks(DraftStoreDAO repo) {
        this.repo = repo;
    }

    @Scheduled(cron = "${maxAge.cron}")
    public void deleteOldDrafts() {
        repo.deleteOldDrafts();
    }
}
