package uk.gov.hmcts.reform.draftstore.service.draftservice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.reform.draftstore.exception.NoDraftFoundException;
import uk.gov.hmcts.reform.draftstore.service.UserAndService;
import uk.gov.hmcts.reform.draftstore.service.mappers.SampleSecret;

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
            () -> draftService.read("123", new UserAndService("john", "service", SampleSecret.getObject()))
        );

        // then
        assertThat(exception).isInstanceOf(NoDraftFoundException.class);
    }

    @Test
    public void should_throw_exception_if_trying_to_read_draft_assigned_to_different_user() throws Exception {
        // given
        UserAndService john = new UserAndService("john", "service", SampleSecret.getObject());
        UserAndService someoneElse = new UserAndService("definitely not john", "service", SampleSecret.getObject());

        thereExists(
            draftCreatedBy(john)
        );

        // when
        Throwable exception = catchThrowable(
            () -> draftService.read("123", someoneElse)
        );

        // then
        assertThat(exception).isInstanceOf(NoDraftFoundException.class);
    }

    @Test
    public void should_throw_exception_if_trying_to_read_draft_assigned_to_different_service() throws Exception {
        // given
        UserAndService john1 = new UserAndService("john", "serviceA", SampleSecret.getObject());
        UserAndService john2 = new UserAndService("john", "serviceBBBBBBB", SampleSecret.getObject());

        thereExists(
            draftCreatedBy(john1)
        );

        // when
        Throwable exception = catchThrowable(
            () -> draftService.read("123", john2)
        );

        // then
        assertThat(exception)
            .isInstanceOf(NoDraftFoundException.class);
    }

    @Test
    public void should_not_throw_exception_for_correct_user_and_service() throws Exception {
        // given
        UserAndService john = new UserAndService("john", "service", SampleSecret.getObject());
        thereExists(
            draftCreatedBy(john)
        );

        // when
        Throwable exception = catchThrowable(() -> draftService.read("123", john));

        // then
        assertThat(exception).isNull();
    }
}
