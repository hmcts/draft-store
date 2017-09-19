package uk.gov.hmcts.reform.draftstore.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.gov.hmcts.reform.draftstore.exception.AuthorizationException;
import uk.gov.hmcts.reform.draftstore.service.idam.IdamClientStub;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.rules.ExpectedException.none;

public class AuthServiceTest {
    private AuthService underTest;

    @Rule
    public ExpectedException expected = none();

    @Before
    public void setUp() {
        underTest = new AuthService(new IdamClientStub());
    }

    @Test
    public void shouldThrowAuthorizationExceptionWhenTokenIsEmpty() {
        expected.expect( AuthorizationException.class );
        expected.expectMessage( is( "Authorization token must be given in following format: 'hmcts-id <userId>'") );

        underTest.userIdFromAuthToken( "" );
    }

    @Test
    public void shouldThrowAuthorizationExceptionWhenTokenIsInvalid() {
        expected.expect( AuthorizationException.class );
        expected.expectMessage( is( "Authorization token must be given in following format: 'hmcts-id <userId>'") );

        underTest.userIdFromAuthToken( "not-hmcts-id a user id" );
    }

    @Test
    public void shouldThrowAuthorizationExceptionWhenUserIdNotGiven() {
        expected.expect( AuthorizationException.class );
        expected.expectMessage( is( "Authorization token must be given in following format: 'hmcts-id <userId>'") );

        underTest.userIdFromAuthToken( "hmcts-id " );
    }

    @Test
    public void shouldReturnUserId() {
        String userId = underTest.userIdFromAuthToken( "hmcts-id a user id" );
        assertThat(userId).isEqualTo( "a user id" );
    }
}
