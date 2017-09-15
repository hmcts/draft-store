package uk.gov.hmcts.reform.draftstore.endpoint.v2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.draftstore.data.DraftStoreDAO;
import uk.gov.hmcts.reform.draftstore.domain.SaveStatus;
import uk.gov.hmcts.reform.draftstore.exception.NoDraftFoundException;
import uk.gov.hmcts.reform.draftstore.service.UserIdentificationService;
import uk.gov.hmcts.reform.draftstore.service.ValidJson;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static uk.gov.hmcts.reform.draftstore.domain.SaveStatus.Updated;

@RestController("v2/DraftStoreEndpoint")
@RequestMapping(path = "/api/v2/draft/{type}")
@Validated
@Api
public class DraftStoreEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(DraftStoreEndpoint.class);

    private static final String TYPE = "type";

    private DraftStoreDAO draftStoreDao;
    private UserIdentificationService userIdService;

    public DraftStoreEndpoint(DraftStoreDAO draftStoreDao, UserIdentificationService userIdService) {
        this.draftStoreDao = draftStoreDao;
        this.userIdService = userIdService;
    }

    @RequestMapping(method = POST, consumes = APPLICATION_JSON_VALUE)
    @ApiOperation("Save document")
    public ResponseEntity<String> saveDocument(
        @RequestHeader(AUTHORIZATION) String authToken,
        @PathVariable(TYPE) String type,
        @RequestBody @ValidJson String document
    ) {
        LOGGER.info("saving draft document");

        String userId = userIdService.userIdFromAuthToken(authToken);
        SaveStatus saveStatus = draftStoreDao.insertOrUpdate(userId, "cmc", type, document);
        return new ResponseEntity<>(saveStatus == Updated ? NO_CONTENT : CREATED);
    }

    @RequestMapping(method = GET, produces = APPLICATION_JSON_VALUE)
    @ApiOperation("Retrieve document")
    public ResponseEntity<String> retrieveDocument(
        @RequestHeader(AUTHORIZATION) String authToken,
        @PathVariable(TYPE) String type
    ) {
        LOGGER.info("retrieving draft document");

        String userId = userIdService.userIdFromAuthToken(authToken);

        String document =
            draftStoreDao
                .readAll(userId, "cmc", type)
                .stream()
                .map(draft -> draft.document)
                .findFirst()
                .orElseThrow(() -> new NoDraftFoundException());

        return new ResponseEntity<>(document, OK);
    }

    @RequestMapping(method = DELETE)
    @ApiOperation("Delete document")
    public ResponseEntity<String> deleteDocument(
        @RequestHeader(AUTHORIZATION) String authToken,
        @PathVariable(TYPE) String type
    ) {
        LOGGER.info("deleting draft document");

        String userId = userIdService.userIdFromAuthToken(authToken);
        draftStoreDao.delete(userId, type);
        return new ResponseEntity<>(NO_CONTENT);
    }

}
