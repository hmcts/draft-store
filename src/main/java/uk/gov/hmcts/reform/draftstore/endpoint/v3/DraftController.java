package uk.gov.hmcts.reform.draftstore.endpoint.v3;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.draftstore.domain.CreateDraft;
import uk.gov.hmcts.reform.draftstore.domain.Draft;
import uk.gov.hmcts.reform.draftstore.domain.DraftList;
import uk.gov.hmcts.reform.draftstore.domain.UpdateDraft;
import uk.gov.hmcts.reform.draftstore.endpoint.domain.ErrorResult;
import uk.gov.hmcts.reform.draftstore.service.AuthService;
import uk.gov.hmcts.reform.draftstore.service.DraftService;
import uk.gov.hmcts.reform.draftstore.service.UserAndService;
import uk.gov.hmcts.reform.draftstore.service.secrets.Secrets;
import uk.gov.hmcts.reform.draftstore.service.secrets.SecretsBuilder;

import java.net.URI;
import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;
import static uk.gov.hmcts.reform.draftstore.service.AuthService.SECRET_HEADER;
import static uk.gov.hmcts.reform.draftstore.service.AuthService.SERVICE_HEADER;

@RestController
@Validated
@RequestMapping(
    path = "drafts",
    produces = MediaType.APPLICATION_JSON_VALUE
)
@SuppressWarnings("checkstyle:LineLength")
public class DraftController {
    public static final int MIN_SECRET_LENGTH = 16;

    private final AuthService authService;
    private final DraftService draftService;

    public DraftController(AuthService authService, DraftService draftService) {
        this.authService = authService;
        this.draftService = draftService;
    }

    @GetMapping(path = "/{id}")
    @ApiOperation("Find draft by ID")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 404, message = "Not found", response = ErrorResult.class),
    })
    public Draft read(
        @PathVariable String id,
        @RequestHeader(AUTHORIZATION) String authHeader,
        @RequestHeader(SERVICE_HEADER) String serviceHeader,
        @RequestHeader(name = SECRET_HEADER, required = false) String secretHeader
    ) {
        Secrets secrets = SecretsBuilder.fromHeader(secretHeader);
        UserAndService userAndService = authService.authenticate(authHeader, serviceHeader);

        return draftService.read(id, userAndService.withSecrets(secrets));
    }

    @GetMapping
    @ApiOperation(value = "Find all your drafts", notes = "Returns an empty array when no drafts were found")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Success"),
    })
    public DraftList readAll(
        @RequestHeader(AUTHORIZATION) String authHeader,
        @RequestHeader(SERVICE_HEADER) String serviceHeader,
        @RequestHeader(name = SECRET_HEADER, required = false) String secretHeader,
        @RequestParam(name = "after", required = false) Integer after,
        @RequestParam(name = "limit", required = false, defaultValue = "10") int limit
    ) {
        Secrets secrets = SecretsBuilder.fromHeader(secretHeader);
        UserAndService userAndService = authService.authenticate(authHeader, serviceHeader);

        return draftService.read(userAndService.withSecrets(secrets), after, limit);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("Create a new draft")
    @ApiResponses({
        @ApiResponse(code = 201, message = "Draft successfully created"),
        @ApiResponse(code = 400, message = "Bad request", response = ErrorResult.class),
    })
    public ResponseEntity<Void> create(
        @RequestHeader(AUTHORIZATION) String authHeader,
        @RequestHeader(SERVICE_HEADER) String serviceHeader,
        @RequestHeader(name = SECRET_HEADER, required = false) @Valid @Length(min = MIN_SECRET_LENGTH) String secretHeader,
        @RequestBody @Valid CreateDraft newDraft
    ) {
        Secrets secrets = SecretsBuilder.fromHeader(secretHeader);
        UserAndService userAndService = authService.authenticate(authHeader, serviceHeader);

        int id = draftService.create(newDraft, userAndService.withSecrets(secrets));

        URI newClaimUri = fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();

        return created(newClaimUri).build();
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("Update existing draft")
    @ApiResponses({
        @ApiResponse(code = 204, message = "Draft updated"),
        @ApiResponse(code = 400, message = "Bad request", response = ErrorResult.class),
        @ApiResponse(code = 404, message = "Not found", response = ErrorResult.class),
    })
    public ResponseEntity<Void> update(
        @PathVariable String id,
        @RequestHeader(AUTHORIZATION) String authHeader,
        @RequestHeader(SERVICE_HEADER) String serviceHeader,
        @RequestHeader(name = SECRET_HEADER, required = false) @Valid @Length(min = MIN_SECRET_LENGTH) String secretHeader,
        @RequestBody @Valid UpdateDraft updatedDraft
    ) {
        Secrets secrets = SecretsBuilder.fromHeader(secretHeader);
        UserAndService userAndService = authService.authenticate(authHeader, serviceHeader);

        draftService.update(id, updatedDraft, userAndService.withSecrets(secrets));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "/{id}")
    @ApiOperation("Delete draft")
    @ApiResponses({
        @ApiResponse(code = 204, message = "Draft deleted")
    })
    public ResponseEntity<Void> delete(
        @PathVariable String id,
        @RequestHeader(AUTHORIZATION) String authHeader,
        @RequestHeader(SERVICE_HEADER) String serviceHeader
    ) {
        UserAndService userAndService = authService.authenticate(authHeader, serviceHeader);
        draftService.delete(id, userAndService);

        return noContent().build();
    }
}
