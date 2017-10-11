package uk.gov.hmcts.reform.draftstore.service.crypto;

public enum Length {

    BITS_96(96),
    BITS_128(128);

    private final int bits;

    Length(int bits) {
        this.bits = bits;
    }

    public int bits() {
        return bits;
    }

    public int bytes() {
        return bits >> 3;
    }
}
