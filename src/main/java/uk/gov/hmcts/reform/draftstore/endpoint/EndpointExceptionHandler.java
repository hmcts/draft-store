package uk.gov.hmcts.reform.draftstore.endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.hmcts.reform.draftstore.endpoint.domain.ErrorResult;
import uk.gov.hmcts.reform.draftstore.exception.AuthorizationException;
import uk.gov.hmcts.reform.draftstore.exception.NoDraftFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static uk.gov.hmcts.reform.draftstore.endpoint.domain.ErrorCode.BAD_ARGUMENT;
import static uk.gov.hmcts.reform.draftstore.endpoint.domain.ErrorCode.INVALID_AUTH_TOKEN;
import static uk.gov.hmcts.reform.draftstore.endpoint.domain.ErrorCode.NO_RECORD_FOUND;
import static uk.gov.hmcts.reform.draftstore.endpoint.domain.ErrorCode.SERVER_ERROR;
import static uk.gov.hmcts.reform.draftstore.endpoint.domain.ErrorCode.USER_DETAILS_SERVICE_ERROR;
import static uk.gov.hmcts.reform.draftstore.endpoint.domain.ErrorResult.Builder.errorResultBuilder;

@ControllerAdvice
public class EndpointExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(EndpointExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(
            ServletRequestBindingException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        log.error(ex.getMessage(), ex);
        ErrorResult errorResult = errorResultBuilder(INVALID_AUTH_TOKEN).withError(
                "Authorization header is required.")
                .build();
        return new ResponseEntity<>(errorResult, BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        log.error(ex.getMessage(), ex);
        ErrorResult errorResult = errorResultBuilder(BAD_ARGUMENT).withError("The draft document is required.")
                .build();
        return new ResponseEntity<>(errorResult, BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
                                                             Object body,
                                                             HttpHeaders headers,
                                                             HttpStatus status,
                                                             WebRequest request) {
        log.error(ex.getMessage(), ex);
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResult> unknownConstraintViolationException(HttpServletRequest req,
                                                                           ConstraintViolationException exception) {
        log.error(exception.getMessage(), exception);
        ErrorResult.Builder errorBuilder = errorResultBuilder(BAD_ARGUMENT).withError(exception.getMessage());
        exception.getConstraintViolations().forEach(violation -> errorBuilder.withError(violation.getMessage()));

        return new ResponseEntity<>(errorBuilder.build(), BAD_REQUEST);
    }

    @ExceptionHandler(NoDraftFoundException.class)
    public ResponseEntity<ErrorResult> handleNoDocumentFoundException(HttpServletRequest req, Exception exception) {
        log.debug("no draft document found for user.");
        return new ResponseEntity<>(errorResultBuilder(NO_RECORD_FOUND).withError(exception.getMessage())
                .build(),
                NOT_FOUND);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ErrorResult> authorizationException(HttpServletRequest req, Exception exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(errorResultBuilder(USER_DETAILS_SERVICE_ERROR).withError(exception.getMessage())
                .build(),
                FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResult> unknownException(HttpServletRequest req, Exception exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(errorResultBuilder(SERVER_ERROR).build(), INTERNAL_SERVER_ERROR);
    }

}
