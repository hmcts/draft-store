package uk.gov.hmcts.reform.draftstore.service.draftservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.reform.draftstore.domain.UpdateDraft;
import uk.gov.hmcts.reform.draftstore.exception.AuthorizationException;
import uk.gov.hmcts.reform.draftstore.exception.NoDraftFoundException;
import uk.gov.hmcts.reform.draftstore.service.UserAndService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@RunWith(MockitoJUnitRunner.class)
public class UpdateTest extends BaseTest {

    @Test
    public void should_throw_exception_when_draft_with_given_id_does_not_exist() throws Exception {
        // given
        thereExists(null);

        // when
        Throwable exception = catchThrowable(() ->
            callUpdateAs(new UserAndService("john", "service"))
        );

        // then
        assertThat(exception)
            .isInstanceOf(NoDraftFoundException.class);
    }

    @Test
    public void should_throw_exception_when_trying_to_update_draft_assigned_to_different_user() throws Exception {

        // given
        thereExists(
            draftCreatedBy(new UserAndService("john", "service"))
        );

        // when
        Throwable exception = catchThrowable(() ->
            callUpdateAs(new UserAndService("definitely not john", "service"))
        );

        // then
        assertThat(exception).isInstanceOf(AuthorizationException.class);
    }

    @Test
    public void should_throw_exception_when_trying_to_update_draft_assigned_to_different_service() throws Exception {
        // given
        thereExists(
            draftCreatedBy(new UserAndService("john", "service_A"))
        );

        // when
        Throwable exception = catchThrowable(() ->
            callUpdateAs(new UserAndService("john", "service_BBBB"))
        );

        // then
        assertThat(exception).isInstanceOf(AuthorizationException.class);
    }

    @Test
    public void should_NOT_throw_exception_when_updating_own_draft() throws Exception {
        // given
        UserAndService john = new UserAndService("john", "service");
        thereExists(
            draftCreatedBy(john)
        );

        // when
        Throwable exception = catchThrowable(() -> callUpdateAs(john));

        // then
        assertThat(exception).isNull();
    }

    private void callUpdateAs(UserAndService userAndService) {
        draftService.update(
            "123",
            new UpdateDraft(new ObjectMapper().createObjectNode(), "some_type"),
            userAndService
        );
    }
}
