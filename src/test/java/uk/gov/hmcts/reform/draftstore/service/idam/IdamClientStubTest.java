package uk.gov.hmcts.reform.draftstore.service.idam;

import com.google.common.base.Strings;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IdamClientStubTest {

    @Test
    public void should_use_auth_header_as_user_id() throws Exception {
        // given
        IdamClientStub idamClientStub = new IdamClientStub();
        String authHeader = "sample header";
        // when
        User user = idamClientStub.getUserDetails(authHeader);
        // then
        assertThat(user.id).isEqualTo(authHeader);
    }

    @Test
    public void should_shorten_long_auth_headers_to_create_valid_user_id() throws Exception {
        // given
        IdamClientStub idamClientStub = new IdamClientStub();
        String veryLongAuthHeader = Strings.repeat("x", IdamClientStub.MAX_USER_ID_LENGTH * 2);
        // when
        User user = idamClientStub.getUserDetails(veryLongAuthHeader);
        // then
        assertThat(user.id.length()).isEqualTo(IdamClientStub.MAX_USER_ID_LENGTH);
    }
}
