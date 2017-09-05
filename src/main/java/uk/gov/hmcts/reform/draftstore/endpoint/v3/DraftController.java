package uk.gov.hmcts.reform.draftstore.endpoint.v3;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.draftstore.data.DraftStoreDAO;
import uk.gov.hmcts.reform.draftstore.domain.Draft;
import uk.gov.hmcts.reform.draftstore.exception.NoDraftFoundException;
import uk.gov.hmcts.reform.draftstore.service.UserIdentificationService;

import java.util.Objects;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

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
}
