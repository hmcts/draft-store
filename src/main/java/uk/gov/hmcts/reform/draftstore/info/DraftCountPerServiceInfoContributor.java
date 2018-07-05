package uk.gov.hmcts.reform.draftstore.info;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.draftstore.data.DraftStoreDao;

@Component
public class DraftCountPerServiceInfoContributor implements InfoContributor {

    private final DraftStoreDao repo;

    public DraftCountPerServiceInfoContributor(DraftStoreDao repo) {
        this.repo = repo;
    }

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("draft_count_per_service", repo.getDraftCountPerService());
    }
}
