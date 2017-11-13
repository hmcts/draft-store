package uk.gov.hmcts.reform.draftstore.info;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.draftstore.data.DraftStoreDAO;

@Component
public class UnencryptedDraftsInfoContributor implements InfoContributor {

    private final DraftStoreDAO repo;

    public UnencryptedDraftsInfoContributor(DraftStoreDAO repo) {
        this.repo = repo;
    }

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("unecrypted_drafts", repo.getUnencryptedDrafts());
    }
}
