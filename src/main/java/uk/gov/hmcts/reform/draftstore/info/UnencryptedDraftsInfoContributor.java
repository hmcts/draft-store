package uk.gov.hmcts.reform.draftstore.info;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.draftstore.data.DraftStoreDAO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.time.LocalDateTime.now;

@Component
public class UnencryptedDraftsInfoContributor implements InfoContributor {

    private final DraftStoreDAO repo;

    private LocalDateTime lastCheckDate = null;
    private List<Map<String, Object>> lastResult = new ArrayList<>();

    public UnencryptedDraftsInfoContributor(DraftStoreDAO repo) {
        this.repo = repo;
    }

    @Override
    public void contribute(Info.Builder builder) {
        if (lastResult.isEmpty() || shouldRefresh()) {
            this.lastResult = repo.getUnencryptedDrafts();
            this.lastCheckDate = now();
        }

        builder.withDetail("unencrypted_drafts", this.lastResult);
    }

    private boolean shouldRefresh() {
        return lastCheckDate == null || now().isAfter(lastCheckDate.plusMinutes(1));
    }
}
