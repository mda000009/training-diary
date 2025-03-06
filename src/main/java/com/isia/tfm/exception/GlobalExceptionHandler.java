package com.isia.tfm.exception;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import com.isia.tfm.model.Error;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String VALIDATION_ERROR_MESSAGE = "The request does not meet the validations";

    /**
     * Handles MethodArgumentNotValidException.
     *
     * @param ex the exception
     * @return the response entity with error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleValidationException(MethodArgumentNotValidException ex) {
        return buildErrorResponse(VALIDATION_ERROR_MESSAGE);
    }

    /**
     * Handles ConstraintViolationException.
     *
     * @param ex the exception
     * @return the response entity with error details
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Error> handleConstraintViolationException(ConstraintViolationException ex) {
        return buildErrorResponse(VALIDATION_ERROR_MESSAGE);
    }

    /**
     * Handles CustomException.
     *
     * @param ex the exception
     * @return the response entity with error details
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Error> handleCustomException(CustomException ex) {
        Error errorResponse = ex.getError();
        HttpStatus status = switch (errorResponse.getStatus()) {
            case "400" -> HttpStatus.BAD_REQUEST;
            case "404" -> HttpStatus.NOT_FOUND;
            case "409" -> HttpStatus.CONFLICT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Handles UnrecognizedPropertyException.
     *
     * @param ex the exception
     * @return the response entity with error details
     */
    @ExceptionHandler(UnrecognizedPropertyException.class)
    public ResponseEntity<Error> handleInvalidAttributeName(UnrecognizedPropertyException ex) {
        String message = "Attribute name '" + ex.getPropertyName() + "' is incorrect.";
        return buildErrorResponse(message);
    }

    private ResponseEntity<Error> buildErrorResponse(String message) {
        Error errorResponse = new Error("400", "Bad Request");
        errorResponse.setMessage(message);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}