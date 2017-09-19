package uk.gov.hmcts.reform.draftstore.service.idam;

public interface IdamClient {
    User getUserDetails(String authHeader);
}
