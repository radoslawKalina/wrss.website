package wrss.wz.website.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import wrss.wz.website.exception.custom.PromEnrollmentDoesNotExist;
import wrss.wz.website.exception.custom.PromPersonEntityNotExistException;
import wrss.wz.website.exception.custom.RecordBelongingException;
import wrss.wz.website.exception.custom.UserAlreadyExistException;
import wrss.wz.website.exception.custom.UserDoesNotExistException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static wrss.wz.website.exception.ErrorType.InvalidRequestArgumentValue;
import static wrss.wz.website.exception.ErrorType.OperationNotAllowedForUser;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                               HttpHeaders headers, HttpStatus status, WebRequest request) {

        List<String> messages = exception.getBindingResult().getFieldErrors()
                                         .stream()
                                         .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                                         .collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse(InvalidRequestArgumentValue, messages);
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistException(UserAlreadyExistException exception) {

        ErrorResponse errorResponse = new ErrorResponse(InvalidRequestArgumentValue, Collections.singletonList(exception.getMessage()));
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

    @ExceptionHandler(RecordBelongingException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistException(RecordBelongingException exception) {

        ErrorResponse errorResponse = new ErrorResponse(OperationNotAllowedForUser, Collections.singletonList(exception.getMessage()));
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

    @ExceptionHandler(PromEnrollmentDoesNotExist.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handlePromEnrollmentDoesNotExist(PromEnrollmentDoesNotExist exception) {

        ErrorResponse errorResponse = new ErrorResponse(InvalidRequestArgumentValue, Collections.singletonList(exception.getMessage()));
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

    @ExceptionHandler(UserDoesNotExistException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleUserDoesNotExistException(UserDoesNotExistException exception) {

        ErrorResponse errorResponse = new ErrorResponse(InvalidRequestArgumentValue, Collections.singletonList(exception.getMessage()));
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

    @ExceptionHandler(PromPersonEntityNotExistException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handlePromPersonEntityNotExistException(PromPersonEntityNotExistException exception) {

        ErrorResponse errorResponse = new ErrorResponse(InvalidRequestArgumentValue, Collections.singletonList(exception.getMessage()));
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }
}