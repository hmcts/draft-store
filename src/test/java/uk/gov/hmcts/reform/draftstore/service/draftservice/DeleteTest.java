package uk.gov.hmcts.reform.draftstore.service.draftservice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.reform.draftstore.exception.AuthorizationException;
import uk.gov.hmcts.reform.draftstore.service.UserAndService;
import uk.gov.hmcts.reform.draftstore.service.mappers.SampleSecret;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DeleteTest extends BaseTest {

    @Test
    public void should_throw_an_exception_when_trying_to_remove_draft_assigned_to_a_different_user() throws Exception {
        // given
        UserAndService john = new UserAndService("john", "service", SampleSecret.getObject());
        UserAndService alice = new UserAndService("alice", "service", SampleSecret.getObject());

        thereExists(
            draftCreatedBy(john)
        );

        // when
        Throwable exception = catchThrowable(() ->
            draftService.delete("123", alice)
        );

        // then
        assertThat(exception)
            .isInstanceOf(AuthorizationException.class);
    }

    @Test
    @SuppressWarnings("checkstyle:LineLength")
    public void should_throw_an_exception_when_trying_to_remove_draft_assigned_to_a_different_service() throws Exception {
        // given
        UserAndService john1 = new UserAndService("john", "serviceA", SampleSecret.getObject());
        UserAndService john2 = new UserAndService("john", "serviceBBBBBBB", SampleSecret.getObject());

        thereExists(
            draftCreatedBy(john1)
        );

        // when
        Throwable exception = catchThrowable(() ->
            draftService.delete("123", john2)
        );

        // then
        assertThat(exception)
            .isInstanceOf(AuthorizationException.class);
    }

    @Test
    public void should_not_throw_an_exception_when_draft_assigned_to_user_and_service_exists() {
        // given
        UserAndService john = new UserAndService("john", "service", SampleSecret.getObject());
        thereExists(
            draftCreatedBy(john)
        );

        // when
        Throwable exception = catchThrowable(() -> draftService.delete("123", john));

        // then
        assertThat(exception).isNull();
    }

    @Test
    public void should_not_throw_an_exception_when_draft_does_not_exist() {
        // given
        thereExists(
            null
        );

        // when
        Throwable exception = catchThrowable(() ->
            draftService.delete("123", new UserAndService("john", "service", SampleSecret.getObject()))
        );

        // then
        assertThat(exception).isNull();
    }
}
