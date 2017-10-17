package uk.gov.hmcts.reform.draftstore.service.secrets;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.reform.draftstore.utils.Lists;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static uk.gov.hmcts.reform.draftstore.service.secrets.Secrets.MIN_SECRET_LENGTH;

public class SecretsBuilder {

    private static final Logger logger = LoggerFactory.getLogger(SecretsBuilder.class);

    public static final String SEPARATOR = ",";

    public static Secrets fromHeader(String header) {

        // only during transition stage.
        if (Strings.isNullOrEmpty(header)) {
            logger.info("No encryption secrets received.");
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
