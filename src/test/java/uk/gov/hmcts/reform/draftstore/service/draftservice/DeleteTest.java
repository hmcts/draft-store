package uk.gov.hmcts.reform.draftstore.service.draftservice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.reform.draftstore.exception.AuthorizationException;
import uk.gov.hmcts.reform.draftstore.service.UserAndService;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DeleteTest extends BaseTest {

    @Test
    public void should_throw_an_exception_when_trying_to_remove_draft_assigned_to_a_different_user() throws Exception {
        // given
        thereExists(
            draftCreatedBy(new UserAndService("john", "service"))
        );

        // when
        Throwable exception = catchThrowable(() ->
            draftService.delete("123", new UserAndService("definitely not john", "service"))
        );

        // then
        assertThat(exception)
            .isInstanceOf(AuthorizationException.class);
    }

    @Test
    public void should_throw_an_exception_when_trying_to_remove_draft_assigned_to_a_different_service() throws Exception {
        // given
        thereExists(
            draftCreatedBy(new UserAndService("john", "serviceA"))
        );

        // when
        Throwable exception = catchThrowable(() ->
            draftService.delete("123", new UserAndService("john", "serviceBBBBBBB"))
        );

        // then
        assertThat(exception)
            .isInstanceOf(AuthorizationException.class);
    }

    @Test
    public void should_NOT_throw_an_exception_when_draft_assigned_to_user_and_service_exists() {
        // given
        UserAndService john = new UserAndService("john", "service");
        thereExists(
            draftCreatedBy(john)
        );

        // when
        Throwable exception = catchThrowable(() -> draftService.delete("123", john));

        // then
        assertThat(exception).isNull();
    }

    @Test
    public void should_NOT_throw_an_exception_when_draft_does_not_exist() {
        // given
        thereExists(
            null
        );

        // when
        Throwable exception = catchThrowable(() ->
            draftService.delete("123", new UserAndService("john", "service"))
        );

        // then
        assertThat(exception).isNull();
    }
}
