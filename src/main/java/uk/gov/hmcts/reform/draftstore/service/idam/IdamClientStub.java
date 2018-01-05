package uk.gov.hmcts.reform.draftstore.service.idam;

public class IdamClientStub implements IdamClient {

    static final int MAX_USER_ID_LENGTH = 256; // enforced by database column

    @Override
    public User getUserDetails(String authHeader) {
        String id = authHeader.substring(0, Math.min(authHeader.length(), MAX_USER_ID_LENGTH));
        String email = "example@example.com";

        return new User(id, email);
    }
}
