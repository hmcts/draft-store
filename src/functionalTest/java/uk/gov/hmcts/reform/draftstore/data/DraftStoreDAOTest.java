package uk.gov.hmcts.reform.draftstore.data;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.draftstore.domain.Draft;
import uk.gov.hmcts.reform.draftstore.domain.SaveStatus;
import uk.gov.hmcts.reform.draftstore.exception.NoDraftFoundException;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.draftstore.domain.SaveStatus.Created;
import static uk.gov.hmcts.reform.draftstore.domain.SaveStatus.Updated;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class DraftStoreDAOTest {
    private static final String USER_ID = "a user";
    private static final String PETITION = "{\"documenterName\": \"Donald Trump\"}";
    private static final String PETITION_UPDATE = "{\"documenterName\": \"Bad Hair Day\"}";
    private static final String ANOTHER_USER_ID = "another user";
    private static final String ANOTHER_PETITION = "{\"documenterName\": \"Different User\"}";

    @Autowired
    private DraftStoreDAO underTest;

    @Autowired
    private uk.gov.hmcts.reform.draftstore.data.DataAgent dataAgent;

    @Before
    public void cleanDb() {
        dataAgent.deleteDocument(USER_ID, "default");
        dataAgent.deleteDocument(ANOTHER_USER_ID, "default");
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldThrowDataAccessExceptionWhenUserIdIsNull() {
        underTest.insertOrUpdate(null, "default", PETITION);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldThrowDataAccessExceptionWhenDocumentIsInvalidJson() {
        underTest.insertOrUpdate(USER_ID, "default", "{not valid json}");
    }

    @Test
    public void shouldCreateDraftDocument() {
        givenExistingDocument(ANOTHER_USER_ID, ANOTHER_PETITION);

        SaveStatus saveStatus = underTest.insertOrUpdate(USER_ID, "default", PETITION);

        assertThat(saveStatus).isEqualTo(Created);
        String actual = dataAgent.documentForUser(USER_ID, "default");
        assertThat(actual).isEqualTo(PETITION);
        assertNoOtherUserDataHasBeenAffected();
    }

    @Test
    public void shouldUpdateExistingDocument() {
        givenExistingDocument(USER_ID, PETITION);
        givenExistingDocument(ANOTHER_USER_ID, ANOTHER_PETITION);

        SaveStatus saveStatus = underTest.insertOrUpdate(USER_ID, "default", PETITION_UPDATE);

        assertThat(saveStatus).isEqualTo(Updated);
        String actual = dataAgent.documentForUser(USER_ID, "default");
        assertThat(actual).isEqualTo(PETITION_UPDATE);
        assertNoOtherUserDataHasBeenAffected();
    }

    @Test
    public void shouldDeleteDraftDocument() {
        givenExistingDocument(USER_ID, PETITION);
        givenExistingDocument(ANOTHER_USER_ID, ANOTHER_PETITION);

        underTest.delete(USER_ID, "default");

        assertThat(dataAgent.countForUser(USER_ID, "default")).isEqualTo(0);
        assertNoOtherUserDataHasBeenAffected();
    }

    @Test(expected = NoDraftFoundException.class)
    public void shouldThrowOnDeleteWhenNoDocumentForUser() throws SQLException {
        underTest.delete(USER_ID, "default");
    }

    @Test
    public void shouldRetrieve() throws SQLException {
        dataAgent.setupDocumentForUser(USER_ID, "default", "{ \"test\":\"1234\"}");
        givenExistingDocument(ANOTHER_USER_ID, ANOTHER_PETITION);

        List<Draft> drafts = underTest.readAll(USER_ID, "default");

        assertThat(drafts.size()).isEqualTo(1);
        assertThat(drafts.get(0).document).isEqualTo("{ \"test\":\"1234\"}");
        assertNoOtherUserDataHasBeenAffected();
    }

    @Test
    public void readAll_should_return_empty_list_when_no_drafts_found() {
        List<Draft> drafts = underTest.readAll("abc", "xyz");
        assertThat(drafts).isEmpty();
    }

    @Test
    public void readAll_should_return_all_matching_drafts() throws SQLException {
        dataAgent.setupDocumentForUser("id", "t", "[1]");
        dataAgent.setupDocumentForUser("id", "t", "[2]");

        List<Draft> drafts = underTest.readAll("id", "t");

        assertThat(drafts).hasSize(2);
        assertThat(drafts).extracting("document").contains("[1]", "[2]");
    }

    private void givenExistingDocument(String userId, String document) {
        underTest.insertOrUpdate(userId, "default", document);
    }

    private void assertNoOtherUserDataHasBeenAffected() {
        List<Draft> otherUserDrafts = underTest.readAll(ANOTHER_USER_ID, "default");
        assertThat(otherUserDrafts.size()).isEqualTo(1);
        assertThat(otherUserDrafts.get(0).document).isEqualTo(ANOTHER_PETITION);
    }
}
