package uk.gov.hmcts.reform.draftstore.data.model;

public class DocumentTypeCount {
    private final String documentType;
    private final int count;

    public DocumentTypeCount(String documentType, int count) {
        this.documentType = documentType;
        this.count = count;
    }

    public String getDocumentType() {
        return documentType;
    }

    public int getCount() {
        return count;
    }
}
