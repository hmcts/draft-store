package uk.gov.hmcts.reform.draftstore.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.draftstore.data.DraftStoreDao;

@Component
public class ScheduledTasks {

    private final DraftStoreDao repo;

    public ScheduledTasks(DraftStoreDao repo) {
        this.repo = repo;
    }

    @Scheduled(cron = "${maxStaleDays.cron}")
    public void deleteStaleDrafts() {
        repo.deleteStaleDrafts();
    }
}
