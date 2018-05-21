package uk.gov.hmcts.reform.draftstore.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.noContent;

@RestController
public class RootController {

    /**
     * Handles Azure requests related to AlwaysOn feature.
     */
    @GetMapping
    public ResponseEntity<Void> root() {
        return noContent().build();
    }
}
