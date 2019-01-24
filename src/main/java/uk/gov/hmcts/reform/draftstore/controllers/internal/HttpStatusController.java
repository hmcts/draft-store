package uk.gov.hmcts.reform.draftstore.controllers.internal;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Used for debugging http status and triggering alerts for testing
 */
@RestController
public class HttpStatusController {

    @GetMapping(
        path = "/internal/debug/status",
        produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<HttpStatusResponse> status(
        @RequestParam(name = "statusCode", required = false, defaultValue = "500") Integer statusCode
    ) {
        return ResponseEntity.status(statusCode)
            .body(new HttpStatusResponse("Responded with " + statusCode));
    }
}
