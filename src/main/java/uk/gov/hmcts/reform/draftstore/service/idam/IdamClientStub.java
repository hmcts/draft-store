package uk.gov.hmcts.reform.draftstore.service.idam;

public class IdamClientStub implements IdamClient {

    private static final int MAX_USER_ID_LENGTH = 256; // enforced by database column

    @Override
    public User getUserDetails(String authHeader) {
        return new User(authHeader.substring(0, MAX_USER_ID_LENGTH), "example@example.com");
    }
}
