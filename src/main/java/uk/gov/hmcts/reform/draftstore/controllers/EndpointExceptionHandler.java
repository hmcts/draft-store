package uk.gov.hmcts.reform.draftstore.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.hmcts.reform.draftstore.domain.ErrorResult;
import uk.gov.hmcts.reform.draftstore.exception.AuthorizationException;
import uk.gov.hmcts.reform.draftstore.exception.NoDraftFoundException;
import uk.gov.hmcts.reform.draftstore.service.crypto.InvalidKeyException;
import uk.gov.hmcts.reform.draftstore.service.idam.InvalidIdamTokenException;
import uk.gov.hmcts.reform.draftstore.service.s2s.InvalidServiceTokenException;
import uk.gov.hmcts.reform.draftstore.service.secrets.SecretsException;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static uk.gov.hmcts.reform.draftstore.domain.ErrorCode.BAD_ARGUMENT;
import static uk.gov.hmcts.reform.draftstore.domain.ErrorCode.INVALID_AUTH_TOKEN;
import static uk.gov.hmcts.reform.draftstore.domain.ErrorCode.INVALID_SERVICE_AUTH_TOKEN;
import static uk.gov.hmcts.reform.draftstore.domain.ErrorCode.NO_RECORD_FOUND;
import static uk.gov.hmcts.reform.draftstore.domain.ErrorCode.SERVER_ERROR;
import static uk.gov.hmcts.reform.draftstore.domain.ErrorCode.USER_DETAILS_SERVICE_ERROR;

@ControllerAdvice
public class EndpointExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(EndpointExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(
        ServletRequestBindingException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request) {
        log.error(ex.getMessage(), ex);

        return new ResponseEntity<>(
            new ErrorResult(INVALID_AUTH_TOKEN, singletonList("Authorization header is required.")),
            BAD_REQUEST
        );
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
        HttpMessageNotReadableException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
    ) {
        log.error(ex.getMessage(), ex);

        return new ResponseEntity<>(
            new ErrorResult(BAD_ARGUMENT, singletonList("The draft document is required.")),
            BAD_REQUEST
        );
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
        Exception ex,
        Object body,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
    ) {
        log.error(ex.getMessage(), ex);
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResult> unknownConstraintViolationException(
        HttpServletRequest req,
        ConstraintViolationException exception
    ) {
        log.error(exception.getMessage(), exception);

        List<String> errors =
            exception
                .getConstraintViolations()
                .stream()
                .map(violation -> violation.getMessage())
                .collect(toList());

        errors.add(exception.getMessage());

        return new ResponseEntity<>(
            new ErrorResult(BAD_ARGUMENT, errors),
            BAD_REQUEST
        );
    }

    @ExceptionHandler(NoDraftFoundException.class)
    public ResponseEntity<ErrorResult> handleNoDocumentFoundException(HttpServletRequest req, Exception exception) {
        log.debug("no draft document found for user.");

        return new ResponseEntity<>(
            new ErrorResult(NO_RECORD_FOUND, singletonList(exception.getMessage())),
            NOT_FOUND
        );
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ErrorResult> authorizationException(HttpServletRequest req, Exception exception) {
        log.error(exception.getMessage(), exception);

        return new ResponseEntity<>(
            new ErrorResult(USER_DETAILS_SERVICE_ERROR, singletonList(exception.getMessage())),
            FORBIDDEN
        );
    }

    @ExceptionHandler(InvalidKeyException.class)
    public ResponseEntity<ErrorResult> invalidCryptoKey(HttpServletRequest req, Exception exception) {
        log.warn(exception.getMessage(), exception);
        return new ResponseEntity<>(
            new ErrorResult(BAD_ARGUMENT, singletonList(exception.getMessage())),
            BAD_REQUEST
        );
    }

    @ExceptionHandler(SecretsException.class)
    public ResponseEntity<ErrorResult> invalidSecrets(HttpServletRequest req, Exception exception) {
        log.warn(exception.getMessage(), exception);
        return new ResponseEntity<>(
            new ErrorResult(BAD_ARGUMENT, singletonList(exception.getMessage())),
            BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResult> unknownException(HttpServletRequest req, Exception exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(
            new ErrorResult(SERVER_ERROR, emptyList()),
            INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(InvalidIdamTokenException.class)
    public ResponseEntity<ErrorResult> handleInvalidIdamTokenException(HttpServletRequest req, Exception exc) {
        log.warn(exc.getMessage(), exc);
        return new ResponseEntity<>(
            new ErrorResult(INVALID_AUTH_TOKEN, emptyList()),
            UNAUTHORIZED
        );
    }

    @ExceptionHandler(InvalidServiceTokenException.class)
    public ResponseEntity<ErrorResult> handleInvalidServiceTokenException(HttpServletRequest req, Exception exc) {
        log.warn(exc.getMessage(), exc);
        return new ResponseEntity<>(
            new ErrorResult(INVALID_SERVICE_AUTH_TOKEN, emptyList()),
            UNAUTHORIZED
        );
    }
}
