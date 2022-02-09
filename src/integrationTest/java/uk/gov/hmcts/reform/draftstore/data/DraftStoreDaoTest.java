package uk.gov.hmcts.reform.draftstore.data;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.draftstore.data.model.CreateDraft;
import uk.gov.hmcts.reform.draftstore.data.model.DocumentTypeCount;
import uk.gov.hmcts.reform.draftstore.data.model.Draft;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("/database.properties")
@Transactional
public class DraftStoreDaoTest {
    private static final String USER_ID = "a user";
    private static final String ANOTHER_USER_ID = "another user";
    private static final String DRAFT_TYPE = "some_type";
    private static final String SERVICE_1 = "service_1";

    @Autowired
    private DraftStoreDao underTest;

    @Autowired
    private DataAgent dataAgent;

    @Before
    public void cleanDb() {
        dataAgent.deleteAll();
    }

    @Test
    public void deleteAll_should_delete_all_drafts_for_given_user_and_service() {
        CreateDraft draft = new CreateDraft("{ \"a\": 123 }", null, DRAFT_TYPE, 123);
        String service1 = "some_service";
        String service2 = "a different service";

        // given
        underTest.insert(USER_ID, service1, draft);
        underTest.insert(USER_ID, service1, draft);

        underTest.insert(USER_ID, service2, draft);

        underTest.insert(ANOTHER_USER_ID, service1, draft);

        // when
        underTest.deleteAll(USER_ID, service1);

        // then
        assertThat(underTest.readAll(USER_ID, service1, null, 10))
            .as("user's drafts in service")
            .isEmpty();

        assertThat(underTest.readAll(USER_ID, service2, null, 10))
            .as("user's drafts in another service")
            .hasSize(1);

        assertThat(underTest.readAll(ANOTHER_USER_ID, service1, null, 10))
            .as("another user's drafts")
            .hasSize(1);
    }

    @Test
    public void readAll_should_return_empty_list_when_no_drafts_found() {
        List<Draft> drafts = underTest.readAll("abc", "cmc", null, 10);
        assertThat(drafts).isEmpty();
    }

    @Test
    public void readAll_should_return_all_matching_drafts() throws SQLException {
        dataAgent.setupDocumentForUser(USER_ID, "t", "[1]");
        dataAgent.setupDocumentForUser(USER_ID, "t", "[2]");

        List<Draft> drafts = underTest.readAll(USER_ID, "cmc", null, 10);

        assertThat(drafts).hasSize(2);
        assertThat(drafts).extracting("document").contains("[1]", "[2]");
    }

    @Test
    public void getDraftCountPerService_should_return_number_of_drafts_per_service() {

        CreateDraft draft = new CreateDraft("{ \"a\": 123 }", null, DRAFT_TYPE, 123);
        String service1 = SERVICE_1;
        String service2 = "service_2";

        // given
        underTest.insert(USER_ID, service1, draft);
        underTest.insert(USER_ID, service1, draft);
        underTest.insert(USER_ID, service1, draft);
        underTest.insert(USER_ID, service1, draft);

        underTest.insert(USER_ID, service2, draft);
        underTest.insert(USER_ID, service2, draft);

        // when
        List<Map<String, Object>> result = underTest.getDraftCountPerService();

        // then
        assertThat(result.size()).isEqualTo(2);

        assertThat(result.get(0).get("service")).isEqualTo(service1);
        assertThat(result.get(0).get("count")).isEqualTo(4L);

        assertThat(result.get(1).get("service")).isEqualTo(service2);
        assertThat(result.get(1).get("count")).isEqualTo(2L);
    }

    @Test
    public void getDraftTypeCountsByUser_should_return_empty_map_when_no_tuples() {
        // given no tuples
        // when
        List<DocumentTypeCount> result = underTest.getDraftTypeCountsByUser(USER_ID);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    public void getDraftTypeCountsByUser_should_return_all_counts() {
        // given
        underTest.insert(USER_ID, SERVICE_1, new CreateDraft("{}", null, DRAFT_TYPE, 123));
        underTest.insert(USER_ID, SERVICE_1, new CreateDraft("{}", null, DRAFT_TYPE, 123));
        underTest.insert(USER_ID, SERVICE_1, new CreateDraft("{}", null, "another_type", 123));

        // when
        List<DocumentTypeCount> results = underTest.getDraftTypeCountsByUser(USER_ID);

        // then
        assertThat(results).hasSize(2);
        assertThat(results.stream()
            .filter(result -> Objects.equals(result.getDocumentType(), DRAFT_TYPE))
            .filter(result -> Objects.equals(result.getCount(), 2))
            .count())
            .isEqualTo(1);
        assertThat(results.stream()
            .filter(result -> Objects.equals(result.getDocumentType(), "another_type"))
            .filter(result -> Objects.equals(result.getCount(), 1))
            .count())
            .isEqualTo(1);
    }
}
