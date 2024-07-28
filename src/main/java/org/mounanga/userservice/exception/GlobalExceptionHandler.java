package org.mounanga.userservice.exception;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NotNull BadCredentialsException exception) {
        return ResponseEntity.status(UNAUTHORIZED).body(new ExceptionResponse(
                UNAUTHORIZED.value(),
                exception.getMessage(),
                exception.getLocalizedMessage(),
                new HashSet<>(),
                new HashMap<>()
        ));
    }

    @ExceptionHandler(FieldValidationException.class)
    public ResponseEntity<FieldErrorResponse> handleException(@NotNull FieldValidationException exception) {
        return ResponseEntity.status(CONFLICT).body(new FieldErrorResponse(
                CONFLICT.value(),
                exception.getMessage(),
                exception.getLocalizedMessage(),
                exception.getFieldErrors()
        ));
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NotNull NotAuthorizedException exception) {
        return ResponseEntity.status(UNAUTHORIZED).body(new ExceptionResponse(
                UNAUTHORIZED.value(),
                exception.getMessage(),
                exception.getLocalizedMessage(),
                new HashSet<>(),
                new HashMap<>()
        ));
    }

    @ExceptionHandler(ResourceAlreadyExistException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NotNull ResourceAlreadyExistException exception) {
        return ResponseEntity.status(CONFLICT).body(new ExceptionResponse(
                CONFLICT.value(),
                exception.getMessage(),
                exception.getLocalizedMessage(),
                new HashSet<>(),
                new HashMap<>()
        ));
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NotNull RoleNotFoundException exception) {
        return ResponseEntity.status(NOT_FOUND).body(new ExceptionResponse(
                NOT_FOUND.value(),
                exception.getMessage(),
                exception.getLocalizedMessage(),
                new HashSet<>(),
                new HashMap<>()
        ));
    }

    @ExceptionHandler(UserNotAuthenticatedException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NotNull UserNotAuthenticatedException exception) {
        return ResponseEntity.status(UNAUTHORIZED).body(new ExceptionResponse(
                UNAUTHORIZED.value(),
                exception.getMessage(),
                exception.getLocalizedMessage(),
                new HashSet<>(),
                new HashMap<>()
        ));
    }


    @ExceptionHandler(UserNotEnabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NotNull UserNotEnabledException exception) {
        return ResponseEntity.status(FORBIDDEN).body(new ExceptionResponse(
                FORBIDDEN.value(),
                exception.getMessage(),
                exception.getLocalizedMessage(),
                new HashSet<>(),
                new HashMap<>()
        ));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NotNull UserNotFoundException exception) {
        return ResponseEntity.status(NOT_FOUND).body(new ExceptionResponse(
                NOT_FOUND.value(),
                exception.getMessage(),
                exception.getLocalizedMessage(),
                new HashSet<>(),
                new HashMap<>()
        ));
    }

    @ExceptionHandler(VerificationExpiredException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NotNull VerificationExpiredException exception) {
        return ResponseEntity.status(GONE).body(new ExceptionResponse(
                GONE.value(),
                exception.getMessage(),
                exception.getLocalizedMessage(),
                new HashSet<>(),
                new HashMap<>()
        ));
    }

    @ExceptionHandler(VerificationNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NotNull VerificationNotFoundException exception) {
       return ResponseEntity.status(NOT_MODIFIED).body(new ExceptionResponse(
                NOT_FOUND.value(),
                exception.getMessage(),
                exception.getLocalizedMessage(),
                new HashSet<>(),
                new HashMap<>()
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NotNull MethodArgumentNotValidException exception) {
        Set<String> validationErrors = new HashSet<>();
        exception.getBindingResult().getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));

        return ResponseEntity.status(BAD_REQUEST).body(new ExceptionResponse(
                BAD_REQUEST.value(),
                exception.getMessage(),
                exception.getLocalizedMessage(),
                validationErrors,
                new HashMap<>()
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NotNull IllegalArgumentException exception) {
        return ResponseEntity.status(BAD_REQUEST).body(new ExceptionResponse(
                BAD_REQUEST.value(),
                exception.getMessage(),
                exception.getLocalizedMessage(),
                new HashSet<>(),
                new HashMap<>()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(@NotNull Exception exception) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ExceptionResponse(
                INTERNAL_SERVER_ERROR.value(),
                exception.getMessage(),
                exception.getLocalizedMessage(),
                new HashSet<>(),
                new HashMap<>()
        ));
    }
}
