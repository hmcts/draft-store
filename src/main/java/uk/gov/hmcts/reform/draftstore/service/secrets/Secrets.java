package uk.gov.hmcts.reform.draftstore.service.secrets;

public class Secrets {

    public static final int MIN_SECRET_LENGTH = 16;

    public final String primary;
    public final String secondary;

    public Secrets(String primary, String secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }
}
