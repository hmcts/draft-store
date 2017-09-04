package uk.gov.hmcts.reform.draftstore.endpoint.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.draftstore.data.DraftStoreDAO;
import uk.gov.hmcts.reform.draftstore.domain.SaveStatus;
import uk.gov.hmcts.reform.draftstore.service.UserIdentificationService;
import uk.gov.hmcts.reform.draftstore.service.ValidJson;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static uk.gov.hmcts.reform.draftstore.domain.SaveStatus.Updated;

@RestController("v1/DraftStoreEndpoint")
@RequestMapping(path = "/api/v1/draft")
@Validated
@Api
public class DraftStoreEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(DraftStoreEndpoint.class);

    private DraftStoreDAO draftStoreDao;
    private UserIdentificationService userIdService;

    public DraftStoreEndpoint(DraftStoreDAO draftStoreDao, UserIdentificationService userIdService) {
        this.draftStoreDao = draftStoreDao;
        this.userIdService = userIdService;
    }

    @RequestMapping(method = POST, consumes = APPLICATION_JSON_VALUE)
    @ApiOperation("Save document")
    public ResponseEntity<String> saveDocument(@RequestHeader(AUTHORIZATION) String authToken,
                                               @RequestBody @ValidJson String draftDocument) {
        LOGGER.info("saving draft document");

        String userId = userIdService.userIdFromAuthToken(authToken);
        SaveStatus saveStatus = draftStoreDao.insertOrUpdate(userId, "default", draftDocument);
        return new ResponseEntity<>(saveStatus == Updated ? NO_CONTENT : CREATED);
    }

    @RequestMapping(method = GET, produces = APPLICATION_JSON_VALUE)
    @ApiOperation("Retrieve document")
    public ResponseEntity<String> retrieveDocument(@RequestHeader(AUTHORIZATION) String authToken) {
        LOGGER.info("retrieving draft document");

        String userId = userIdService.userIdFromAuthToken(authToken);
        String document = draftStoreDao.retrieve(userId, "default");
        return new ResponseEntity<>(document, OK);
    }

    @RequestMapping(method = DELETE)
    @ApiOperation("Delete document")
    public ResponseEntity<String> deleteDocument(@RequestHeader(AUTHORIZATION) String authToken) {
        LOGGER.info("deleting draft document");

        String userId = userIdService.userIdFromAuthToken(authToken);
        draftStoreDao.delete(userId, "default");
        return new ResponseEntity<>(NO_CONTENT);
    }
}
