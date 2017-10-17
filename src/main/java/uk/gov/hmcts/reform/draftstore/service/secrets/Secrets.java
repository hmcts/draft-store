package uk.gov.hmcts.reform.draftstore.service.secrets;

import com.google.common.base.Strings;
import uk.gov.hmcts.reform.draftstore.utils.Lists;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Secrets {

    public static final int MIN_SECRET_LENGTH = 16;
    public static final String SEPARATOR = ",";

    public final String primary;
    public final String secondary;

    public Secrets(String primary, String secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    public static Secrets fromHeader(String header) {
        if (Strings.isNullOrEmpty(header)) {
            return new Secrets(null, null);
        } else {
            List<String> secrets =
                Arrays
                    .stream(header.split(SEPARATOR))
                    .map(s -> s.trim())
                    .collect(toList());

            if (secrets.size() > 2) {
                throw new SecretsException("Too many secrets. Max number is 2");
            } else if (!secrets.stream().allMatch(s -> s.length() >= MIN_SECRET_LENGTH)) {
                throw new SecretsException("Min length for secret is " + MIN_SECRET_LENGTH);
            } else {
                return new Secrets(
                    secrets.get(0),
                    Lists.safeGet(secrets, 1).orElse(null)
                );
            }
        }
    }
}
