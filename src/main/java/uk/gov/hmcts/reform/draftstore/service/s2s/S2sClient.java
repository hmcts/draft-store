package uk.gov.hmcts.reform.draftstore.service.s2s;

public interface S2sClient {
    String getServiceName(String authHeader);
}
