package uk.gov.hmcts.reform.draftstore.service.secrets;

import com.google.common.base.Strings;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
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

    public List<String> getNonEmpty() {
        return Stream.of(primary, secondary)
            .filter(s -> s != null)
            .collect(toList());
    }

    public static Secrets fromHeader(String header) {
        if (Strings.isNullOrEmpty(header)) {
            return new Secrets(null, null);
        } else {
            List<String> secrets = asList(header.split(SEPARATOR));
            if (secrets.size() > 2) {
                throw new SecretsException("Too many secrets. Max number is 2");
            } else {
                if (!secrets.stream().allMatch(s -> s.length() >= MIN_SECRET_LENGTH)) {
                    throw new SecretsException("Min length for secret is " + MIN_SECRET_LENGTH);
                } else {
                    return new Secrets(secrets.get(0), secrets.get(1));
                }
            }
        }
    }
}
