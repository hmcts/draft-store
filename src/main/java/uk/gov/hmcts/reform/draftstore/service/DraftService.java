package uk.gov.hmcts.reform.draftstore.service;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.draftstore.data.DraftStoreDAO;
import uk.gov.hmcts.reform.draftstore.domain.CreateDraft;
import uk.gov.hmcts.reform.draftstore.domain.Draft;
import uk.gov.hmcts.reform.draftstore.domain.DraftList;
import uk.gov.hmcts.reform.draftstore.domain.UpdateDraft;
import uk.gov.hmcts.reform.draftstore.exception.AuthorizationException;
import uk.gov.hmcts.reform.draftstore.exception.NoDraftFoundException;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static uk.gov.hmcts.reform.draftstore.service.mappers.FromDbModelMapper.fromDb;
import static uk.gov.hmcts.reform.draftstore.service.mappers.ToDbModelMapper.toDb;

@Service
public class DraftService {

    private final DraftStoreDAO draftRepo;

    public DraftService(DraftStoreDAO draftRepo) {
        this.draftRepo = draftRepo;
    }

    public uk.gov.hmcts.reform.draftstore.domain.Draft read(String id, UserAndService userAndService) {
        return draftRepo
            .read(toInternalId(id))
            .filter(draft -> Objects.equals(draft.userId, userAndService.userId))
            .filter(draft -> Objects.equals(draft.service, userAndService.service))
            .map(draft -> fromDb(draft, userAndService.secrets))
            .orElseThrow(() -> new NoDraftFoundException());
    }

    public DraftList read(UserAndService userAndService, Integer after, int limit) {
        List<Draft> drafts =
            draftRepo
                .readAll(userAndService.userId, userAndService.service, after, limit)
                .stream()
                .map(d -> fromDb(d, userAndService.secrets))
                .collect(toList());

        return new DraftList(drafts);
    }

    public int create(CreateDraft newDraft, UserAndService userAndService) {
        return draftRepo.insert(
            userAndService.userId,
            userAndService.service,
            toDb(newDraft, userAndService.secrets)
        );
    }

    public void update(String id, UpdateDraft updatedDraft, UserAndService userAndService) {
        draftRepo
            .read(toInternalId(id))
            .map(d -> {
                assertCanEdit(d, userAndService);
                draftRepo.update(toInternalId(id), toDb(updatedDraft, userAndService.secrets));
                return d;
            })
            .orElseThrow(() -> new NoDraftFoundException());
    }

    public void delete(String id, UserAndService userAndService) {
        draftRepo
            .read(toInternalId(id))
            .ifPresent(d -> {
                assertCanEdit(d, userAndService);
                draftRepo.delete(toInternalId(id));
            });
    }

    private void assertCanEdit(uk.gov.hmcts.reform.draftstore.data.model.Draft draft, UserAndService userAndService) {
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
