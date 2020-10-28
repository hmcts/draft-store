package uk.gov.hmcts.reform.draftstore.controllers;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.VersionSelector;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.draftstore.data.DraftStoreDao;
import uk.gov.hmcts.reform.draftstore.data.model.CreateDraft;
import uk.gov.hmcts.reform.draftstore.data.model.Draft;
import uk.gov.hmcts.reform.draftstore.service.AuthService;
import uk.gov.hmcts.reform.draftstore.service.DraftService;
import uk.gov.hmcts.reform.draftstore.service.UserAndService;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Provider("draftStore_draft")
@PactBroker(scheme = "${PACT_BROKER_SCHEME:http}",
    host = "${PACT_BROKER_URL:localhost}",
    port = "${PACT_BROKER_PORT:80}",
    consumerVersionSelectors = {
    @VersionSelector(tag = "${PACT_BRANCH_NAME:Dev}")})
@Import(DraftControllerProviderTestConfiguration.class)
public class DivorceDraftControllerProviderTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    DraftController draftController;
    @Autowired
    AuthService authServiceMock;
    @Autowired
    DraftService draftService;
    @Autowired
    DraftStoreDao draftStoreDaoMock;

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @BeforeEach
    void before(PactVerificationContext context) {
        System.getProperties().setProperty("pact.verifier.publishResults", "true");
        MockMvcTestTarget testTarget = new MockMvcTestTarget();
        testTarget.setControllers(draftController);
        context.setTarget(testTarget);
    }

    @State({"A draft exists for a logged in user"})
    public void toRetreiveDraftsForLoggedInUser(Map<String, Object> draftMap) throws IOException, JSONException {
        setUpMockInteractions(draftMap, null);

    }

    @State({"A draft exists after a given page for a logged in user"})
    public void toRetreiveDraftsAfterPageForLoggedInUser(Map<String, Object> draftMap)
        throws IOException, JSONException {
        setUpMockInteractions(draftMap, 1);
    }

    @State({"A logged in user requests to create a draft"})
    public void toCreateADraftForLoggedInUser(Map<String, Object> draftMap) throws IOException, JSONException {
        setUpMockInteractions(draftMap, 1);
    }


    @State({"A logged in user requests to update a draft"})
    public void toUpdateADraftForLoggedInUser(Map<String, Object> draftMap) throws IOException, JSONException {
        setUpMockInteractions(draftMap, 1);
    }

    @State({"Drafts exists for a logged in user and delete is requested"})
    public void toDeleteADraftsForLoggedInUser(Map<String, Object> draftMap) throws IOException, JSONException {
        setUpMockInteractions(draftMap, 1);
    }


    private void setUpMockInteractions(Map<String, Object> draftMap, Integer after) throws JsonProcessingException {
        UserAndService userAndService = new UserAndService("12345", "divorce");
        when(authServiceMock.authenticate(anyString(), anyString())).thenReturn(userAndService);
        String document = objectMapper.writeValueAsString(draftMap);
        Draft draft = new Draft(
            "12345",
            userAndService.userId,
            userAndService.service,
            document,
            null,
            "some_type",
            ZonedDateTime.now(),
            ZonedDateTime.now()
        );
        when(draftStoreDaoMock.readAll(userAndService.userId, userAndService.service, after, 10))
            .thenReturn(Arrays.asList(draft));
        when(draftStoreDaoMock.insert(anyString(), anyString(), any(CreateDraft.class))).thenReturn(10001);

        when(draftStoreDaoMock.read(12345)).thenReturn(java.util.Optional.of(draft));

    }


}
