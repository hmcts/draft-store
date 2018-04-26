package uk.gov.hmcts.reform.draftstore.info;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.draftstore.data.DraftStoreDao;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.time.LocalDateTime.now;

@Component
public class UnencryptedDraftsInfoContributor implements InfoContributor {

    private final DraftStoreDao repo;

    private LocalDateTime lastCheckDate;
    private List<Map<String, Object>> lastResult = new ArrayList<>();

    public UnencryptedDraftsInfoContributor(DraftStoreDao repo) {
        this.repo = repo;
    }

    @Override
    public void contribute(Info.Builder builder) {
        if (lastCheckDate == null || Duration.between(lastCheckDate, now()).getSeconds() >= 60) {
            this.lastResult = repo.getUnencryptedDrafts();
            this.lastCheckDate = now();
        }

        builder.withDetail("unencrypted_drafts", this.lastResult);
    }
}
