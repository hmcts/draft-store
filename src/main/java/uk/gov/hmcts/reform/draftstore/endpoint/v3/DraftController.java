package uk.gov.hmcts.reform.draftstore.endpoint.v3;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.draftstore.data.DraftStoreDAO;
import uk.gov.hmcts.reform.draftstore.domain.CreateDraft;
import uk.gov.hmcts.reform.draftstore.domain.Draft;
import uk.gov.hmcts.reform.draftstore.domain.DraftList;
import uk.gov.hmcts.reform.draftstore.domain.UpdateDraft;
import uk.gov.hmcts.reform.draftstore.endpoint.domain.ErrorResult;
import uk.gov.hmcts.reform.draftstore.exception.AuthorizationException;
import uk.gov.hmcts.reform.draftstore.exception.NoDraftFoundException;
import uk.gov.hmcts.reform.draftstore.service.AuthService;
import uk.gov.hmcts.reform.draftstore.service.UserAndService;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;
import static uk.gov.hmcts.reform.draftstore.service.AuthService.SERVICE_HEADER;

@RestController
@RequestMapping(
    path = "drafts",
    produces = MediaType.APPLICATION_JSON_VALUE
)
public class DraftController {

    private final DraftStoreDAO draftRepo;
    private final AuthService authService;

    public DraftController(DraftStoreDAO draftRepo, AuthService authService) {
        this.draftRepo = draftRepo;
        this.authService = authService;
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
        @RequestHeader(SERVICE_HEADER) String serviceHeader
    ) {
        UserAndService userAndService = authService.authenticate(authHeader, serviceHeader);

        return draftRepo
            .read(toInternalId(id))
            .filter(draft -> Objects.equals(draft.userId, userAndService.userId))
            .filter(draft -> Objects.equals(draft.service, userAndService.service))
            .orElseThrow(() -> new NoDraftFoundException());
    }

    @GetMapping
    @ApiOperation(value = "Find all your drafts", notes = "Returns an empty array when no drafts were found")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Success"),
    })
    public DraftList readAll(
        @RequestHeader(AUTHORIZATION) String authHeader,
        @RequestHeader(SERVICE_HEADER) String serviceHeader
    ) {
        UserAndService userAndService = authService.authenticate(authHeader, serviceHeader);
        List<Draft> drafts = draftRepo.readAll(userAndService.userId, userAndService.service);

        return new DraftList(drafts);
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
        @RequestBody @Valid CreateDraft newDraft
    ) {
        UserAndService userAndService = authService.authenticate(authHeader, serviceHeader);

        int id = draftRepo.insert(userAndService.userId, userAndService.service, newDraft);

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
        @RequestBody @Valid UpdateDraft updatedDraft
    ) {
        UserAndService userAndService = authService.authenticate(authHeader, serviceHeader);

        return draftRepo
            .read(toInternalId(id))
            .map(d -> {
                assertCanEdit(d, userAndService);
                draftRepo.update(toInternalId(id), updatedDraft);

                return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
            })
            .orElseThrow(() -> new NoDraftFoundException());
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

        draftRepo
            .read(toInternalId(id))
            .ifPresent(d -> {
                assertCanEdit(d, userAndService);
                draftRepo.delete(toInternalId(id));
            });

        return noContent().build();
    }

    private void assertCanEdit(Draft draft, UserAndService userAndService) {
        boolean userOk = Objects.equals(draft.userId, userAndService.userId);
        boolean serviceOk = Objects.equals(draft.service, userAndService.service);

        if (!(userOk && serviceOk)) {
            throw new AuthorizationException();
        }
    }

    /**
     * Converts external API id to internally used id.
     */
    private int toInternalId(String apiId) {
        try {
            // currently database ID is an int
            return Integer.parseInt(apiId);
        } catch (NumberFormatException exc) {
            throw new NoDraftFoundException();
        }
    }
}
