package uk.gov.hmcts.reform.draftstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@SuppressWarnings("HideUtilityClassConstructor") // Spring needs a constructor, its not a utility class
public class DraftStoreApplication {

    public static final String BASE_PACKAGE_NAME = DraftStoreApplication.class.getPackage().getName();

    public static void main(String[] args) {
        SpringApplication.run(DraftStoreApplication.class, args);
    }
}
