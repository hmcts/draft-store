package uk.gov.hmcts.reform.draftstore.data;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.reform.draftstore.data.model.Draft;
import uk.gov.hmcts.reform.draftstore.endpoint.v3.helpers.SampleData;

import java.util.List;
import java.util.function.Supplier;

import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource("/database.properties")
@ActiveProfiles("test")
public class PaginationTest {

    @Autowired
    private DraftStoreDAO repo;

    @Test
    public void should_limit_number_of_results() throws Exception {
        // given
        int limit = 2;
        repeat(5, () -> insert());

        // when
        List<Draft> drafts = readAfter(null, limit);

        // then
        assertThat(drafts).hasSize(limit);
    }

    @Test
    public void should_return_elements_after_given_position() throws Exception {
        // given
        List<Integer> createdDraftIds = repeat(10, () -> insert());
        Integer fifthElement = createdDraftIds.get(4);
        List<Integer> idsAfterFifthElement = createdDraftIds.stream().skip(5).collect(toList());

        // when
        List<Draft> drafts = readAfter(fifthElement, 100);

        // then
        assertThat(drafts.stream().map(d -> parseInt(d.id))).hasSameElementsAs(idsAfterFifthElement);
    }

    private int insert() {
        return this.repo.insert("123", "abc", SampleData.createDraft(1));
    }

    private List<Draft> readAfter(Integer after, int limit) {
        return this.repo.readAll("123", "abc", after, limit);
    }

    private <T> List<T> repeat(int times, Supplier<T> action) {
        return range(0, times - 1)
            .boxed()
            .map(index -> action.get())
            .collect(toList());
    }
}
