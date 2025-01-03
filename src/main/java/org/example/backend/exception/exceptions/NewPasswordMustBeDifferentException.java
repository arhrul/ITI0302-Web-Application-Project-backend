package org.example.backend.exception.exceptions;

public class NewPasswordMustBeDifferentException extends RuntimeException {
  public NewPasswordMustBeDifferentException(String message) {
    super(message);
  }
}
