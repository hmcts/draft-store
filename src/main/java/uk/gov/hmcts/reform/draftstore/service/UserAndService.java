package uk.gov.hmcts.reform.draftstore.service;

import uk.gov.hmcts.reform.draftstore.service.secrets.Secrets;

public class UserAndService {

    public final String userId;
    public final String service;
    public final Secrets secrets;

    public UserAndService(String userId, String service, Secrets secrets) {
        this.userId = userId;
        this.service = service;
        this.secrets = secrets;
    }

    public UserAndService(String userId, String service) {
        this(userId, service, new Secrets(null, null));
    }

    public UserAndService withSecrets(Secrets secrets) {
        return new UserAndService(this.userId, this.service, secrets);
    }
}
