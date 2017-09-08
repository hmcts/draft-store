package uk.gov.hmcts.reform.draftstore.endpoint.v3;

import org.springframework.http.HttpStatus;
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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("drafts")
public class DraftController {

    private final DraftStoreDAO draftRepo;
    private final UserIdentificationService userIdService;

    public DraftController(DraftStoreDAO draftRepo, UserIdentificationService userIdService) {
        this.draftRepo = draftRepo;
        this.userIdService = userIdService;
    }

    @GetMapping(path = "/{id}")
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
    public ResponseEntity create(
        @RequestHeader(AUTHORIZATION) String authHeader,
        @RequestBody @Valid CreateDraft newDraft
    ) {
        String currentUserId = userIdService.userIdFromAuthToken(authHeader);
        draftRepo.insert(currentUserId, newDraft);

        return status(HttpStatus.CREATED).build();
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity update(
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

                return status(HttpStatus.NO_CONTENT).build();
            })
            .orElseThrow(() -> new NoDraftFoundException());
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity delete(
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
