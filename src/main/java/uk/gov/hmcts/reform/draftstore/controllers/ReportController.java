package uk.gov.hmcts.reform.draftstore.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.draftstore.service.AuthService;
import uk.gov.hmcts.reform.draftstore.service.DraftService;

import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@Validated
@RequestMapping(
    path = "reports",
    produces = MediaType.APPLICATION_JSON_VALUE
)
public class ReportController {

    private final DraftService draftService;
    private final AuthService authService;

    public ReportController(DraftService draftService, AuthService authService) {
        this.draftService = draftService;
        this.authService = authService;
    }

    @GetMapping(path = "/{userId}")
    @Operation(summary = "Report draft document type counts for a user")
    @ApiResponse(responseCode = "200", description = "Success")
    public Map<String, Integer> getDocumentTypeCounts(
        @PathVariable String userId,
        @RequestHeader(AUTHORIZATION) String authHeader
    ) {
        authService.authenticate(authHeader);
        return draftService.userReport(userId);
    }
}
