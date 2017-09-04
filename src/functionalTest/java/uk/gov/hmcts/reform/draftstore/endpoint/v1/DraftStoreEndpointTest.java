package uk.gov.hmcts.reform.draftstore.endpoint.v1;

import uk.gov.hmcts.reform.draftstore.endpoint.AbstractDraftStoreEndpointTest;

public class DraftStoreEndpointTest extends AbstractDraftStoreEndpointTest {

    public DraftStoreEndpointTest() {
        super("/api/v1/draft", "default");
    }
}
