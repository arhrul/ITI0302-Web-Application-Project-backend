package org.example.backend.exception;

import jakarta.validation.ConstraintViolationException;
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
            NoSuchReservationException.class,
            NoSuchAmenityException.class,
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

    @ExceptionHandler(AmenityNameAlreadyExistsException.class)
    public ResponseEntity<Object> handleAmenityNameAlreadyExists(AmenityNameAlreadyExistsException ex) {
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

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RoomAlreadyBookedException.class)
    public ResponseEntity<String> handleRoomAlreadyBookedException(RoomAlreadyBookedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RoomDeletionException.class)
    public ResponseEntity<String> handleRoomDeletionException(RoomDeletionException ex) {
        logException(ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Room cannot be deleted: " + ex.getMessage());
    }

    @ExceptionHandler(ClientDeletionException.class)
    public ResponseEntity<String> handleClientDeletionException(ClientDeletionException ex) {
        logException(ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Client cannot be deleted: " + ex.getMessage());
    }

    @ExceptionHandler(AmenityAlreadyAssignedException.class)
    public ResponseEntity<String> handleAmenityAlreadyAssigned(AmenityAlreadyAssignedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(NewPasswordMustBeDifferentException.class)
    public ResponseEntity<String> handleNewPasswordMustBeDifferent(NewPasswordMustBeDifferentException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("New password cannot be the same as the old password" + ex.getMessage());
    }
}
