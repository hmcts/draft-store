package uk.gov.hmcts.reform.draftstore.service.idam;

public class IdamClientStub implements IdamClient {

    @Override
    public User getUserDetails(String authHeader) {
        return new User(authHeader, "example@example.com");
    }
}
