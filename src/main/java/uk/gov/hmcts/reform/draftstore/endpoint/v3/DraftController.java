package uk.gov.hmcts.reform.draftstore.endpoint.v3;

import io.swagger.annotations.ApiOperation;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.draftstore.data.DraftStoreDAO;
import uk.gov.hmcts.reform.draftstore.domain.CreateDraft;
import uk.gov.hmcts.reform.draftstore.domain.Draft;
import uk.gov.hmcts.reform.draftstore.domain.DraftList;
import uk.gov.hmcts.reform.draftstore.domain.UpdateDraft;
import uk.gov.hmcts.reform.draftstore.exception.AuthorizationException;
import uk.gov.hmcts.reform.draftstore.exception.NoDraftFoundException;
import uk.gov.hmcts.reform.draftstore.service.UserIdentificationService;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping(
    path = "drafts",
    produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE
)
public class DraftController {

    private final DraftStoreDAO draftRepo;
    private final UserIdentificationService userIdService;

    public DraftController(DraftStoreDAO draftRepo, UserIdentificationService userIdService) {
        this.draftRepo = draftRepo;
        this.userIdService = userIdService;
    }

    @GetMapping(path = "/{id}")
    @ApiOperation("Find draft by ID")
    public Draft read(
        @PathVariable int id,
        @RequestHeader(AUTHORIZATION) String authHeader
    ) {
        String currentUserId = userIdService.userIdFromAuthToken(authHeader);
        return draftRepo
            .read(id)
            .filter(draft -> Objects.equals(draft.userId, currentUserId))
            .orElseThrow(() -> new NoDraftFoundException());
    }

    @GetMapping
    @ApiOperation("Find all your drafts")
    public DraftList readAll(
        @RequestParam(value = "type", required = false) String type,
        @RequestHeader(AUTHORIZATION) String authHeader
    ) {
        String currentUserId = userIdService.userIdFromAuthToken(authHeader);

        List<Draft> drafts =
            Optional.ofNullable(type)
                .map(t -> draftRepo.readAll(currentUserId, t))
                .orElseGet(() -> draftRepo.readAll(currentUserId));

        return new DraftList(drafts);
    }

    @PostMapping
    @ApiOperation("Create a new draft")
    public ResponseEntity<Void> create(
        @RequestHeader(AUTHORIZATION) String authHeader,
        @RequestBody @Valid CreateDraft newDraft
    ) {
        String currentUserId = userIdService.userIdFromAuthToken(authHeader);
        int id = draftRepo.insert(currentUserId, newDraft);

        URI newClaimUri = fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();

        return created(newClaimUri).build();
    }

    @PutMapping(path = "/{id}")
    @ApiOperation("Update existing draft")
    public ResponseEntity<Void> update(
        @PathVariable int id,
        @RequestHeader(AUTHORIZATION) String authHeader,
        @RequestBody @Valid UpdateDraft updatedDraft
    ) {
        String currentUserId = userIdService.userIdFromAuthToken(authHeader);

        return draftRepo
            .read(id)
            .map(d -> {
                assertCanEdit(d, currentUserId);
                draftRepo.update(id, updatedDraft);

                return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
            })
            .orElseThrow(() -> new NoDraftFoundException());
    }

    @DeleteMapping(path = "/{id}")
    @ApiOperation("Delete draft")
    public ResponseEntity<Void> delete(
        @PathVariable int id,
        @RequestHeader(AUTHORIZATION) String authHeader
    ) {
        String currentUserId = userIdService.userIdFromAuthToken(authHeader);

        draftRepo
            .read(id)
            .ifPresent(d -> {
                assertCanEdit(d, currentUserId);
                draftRepo.delete(id);
            });

        return noContent().build();
    }

    private void assertCanEdit(Draft draft, String userId) {
        if (!Objects.equals(draft.userId, userId)) {
            throw new AuthorizationException();
        }
    }
}
