package uk.gov.hmcts.reform.draftstore.utils;

import org.junit.Test;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RetryTest {

    @Test
    public void should_return_result_of_calling_an_action_with_first_param_if_it_succeeded() throws Exception {
        int result = Retry.with(asList(1, 2, 3), n -> failOnZero(n));
        assertThat(result).isEqualTo(1);
    }

    @Test
    public void should_return_result_of_calling_an_action_with_second_param_if_first_one_failed() throws Exception {
        int result = Retry.with(asList(0, 24), n -> failOnZero(n));
        assertThat(result).isEqualTo(24);
    }

    @Test
    public void should_throw_an_exception_when_action_fails_for_all_params() throws Exception {
        assertThatThrownBy(
            () -> Retry.with(asList(0, 0, 0), n -> failOnZero(n))
        ).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void should_require_a_nonempty_list_of_params() throws Exception {
        assertThatThrownBy(
            () -> Retry.with(Collections.<Integer>emptyList(), n -> failOnZero(n))
        ).isInstanceOf(RuntimeException.class);
    }

    private static int failOnZero(int number) {
        if (number == 0) {
            throw new RuntimeException();
        } else {
            return number;
        }
    }
}
