package org.example.backend.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.backend.exception.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class MyErrorHandler {

    private void logException(Exception ex) {
        log.error("Exception occurred: {}", ex.getMessage(), ex);
    }

    @ExceptionHandler({NoSuchClientException.class,
            NoSuchRoomException.class,
            NoSuchReservationException.class
    })
    public ResponseEntity<Object> handleException(Exception ex) {
        ex.printStackTrace();
        logException(ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ClientEmailAlreadyExistsException.class)
    public ResponseEntity<Object> handleClientEmailAlreadyExists(ClientEmailAlreadyExistsException ex) {
        ex.printStackTrace();
        logException(ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RoomNumberAlreadyExistsException.class)
    public ResponseEntity<Object> handleRoomNumberAlreadyExists(RoomNumberAlreadyExistsException ex) {
        ex.printStackTrace();
        logException(ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidNumberOfGuestsException.class)
    public ResponseEntity<Object> handleLimitException(Exception ex) {
        ex.printStackTrace();
        logException(ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidEmailOrPasswordException.class)
    public ResponseEntity<Object> handleInvalidEmailOrPassword(InvalidEmailOrPasswordException ex) {
        ex.printStackTrace();
        logException(ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(AuthenticationException e) {
        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }
}
