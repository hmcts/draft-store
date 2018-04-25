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
import uk.gov.hmcts.reform.draftstore.data.model.Draft;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("/database.properties")
@Transactional
public class DraftStoreDaoTest {
    private static final String USER_ID = "a user";
    private static final String ANOTHER_USER_ID = "another user";

    @Autowired
    private DraftStoreDao underTest;

    @Autowired
    private uk.gov.hmcts.reform.draftstore.data.DataAgent dataAgent;

    @Before
    public void cleanDb() {
        dataAgent.deleteDocuments(USER_ID);
        dataAgent.deleteDocuments(ANOTHER_USER_ID);
    }

    @Test
    public void deleteAll_should_delete_all_drafts_for_given_user_and_service() {
        String draftType = "some_type";
        CreateDraft draft = new CreateDraft("{ \"a\": 123 }", null, draftType, 123);
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
        dataAgent.setupDocumentForUser("id", "t", "[1]");
        dataAgent.setupDocumentForUser("id", "t", "[2]");

        List<Draft> drafts = underTest.readAll("id", "cmc", null, 10);

        assertThat(drafts).hasSize(2);
        assertThat(drafts).extracting("document").contains("[1]", "[2]");
    }
}
