package wrss.wz.website.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static wrss.wz.website.exception.ErrorType.InvalidRequestArgumentValue;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                      HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> messageList = new ArrayList<>();

        exception.getBindingResult().getFieldErrors().forEach(error ->
                messageList.add(String.format("%s: %s", error.getField(), error.getDefaultMessage())));

        ErrorResponseList errorResponse = new ErrorResponseList(InvalidRequestArgumentValue, messageList);
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }
}
