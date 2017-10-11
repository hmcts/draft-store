package uk.gov.hmcts.reform.draftstore.service.draftservice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.reform.draftstore.exception.NoDraftFoundException;
import uk.gov.hmcts.reform.draftstore.service.UserAndService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@RunWith(MockitoJUnitRunner.class)
public class ReadSingleTest extends BaseTest {

    @Test
    public void should_throw_exception_if_draft_with_given_id_does_not_exist() throws Exception {
        // given
        thereExists(null);

        // when
        Throwable exception = catchThrowable(
            () -> draftService.read("123", new UserAndService("john", "service"))
        );

        // then
        assertThat(exception).isInstanceOf(NoDraftFoundException.class);
    }

    @Test
    public void should_throw_exception_if_trying_to_read_draft_assigned_to_different_user() throws Exception {
        // given
        thereExists(
            draftCreatedBy(new UserAndService("john", "service"))
        );

        // when
        Throwable exception = catchThrowable(
            () -> draftService.read("123", new UserAndService("definitely not john", "service"))
        );

        // then
        assertThat(exception).isInstanceOf(NoDraftFoundException.class);
    }

    @Test
    public void should_throw_exception_if_trying_to_read_draft_assigned_to_different_service() throws Exception {
        // given
        thereExists(
            draftCreatedBy(new UserAndService("john", "serviceA"))
        );

        // when
        Throwable exception = catchThrowable(
            () -> draftService.read("123", new UserAndService("john", "serviceBBBBBBB"))
        );

        // then
        assertThat(exception)
            .isInstanceOf(NoDraftFoundException.class);
    }

    @Test
    public void should_NOT_throw_exception_for_correct_user_and_service() throws Exception {
        // given
        UserAndService john = new UserAndService("john", "service");
        thereExists(
            draftCreatedBy(john)
        );

        // when
        Throwable exception = catchThrowable(() -> draftService.read("123", john));

        // then
        assertThat(exception).isNull();
    }
}
