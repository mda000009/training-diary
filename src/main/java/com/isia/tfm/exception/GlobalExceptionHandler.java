package com.isia.tfm.exception;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.isia.tfm.model.ErrorDetailsError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import com.isia.tfm.model.ErrorDetails;

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
    public ResponseEntity<ErrorDetails> handleValidationException(MethodArgumentNotValidException ex) {
        return buildErrorResponse(VALIDATION_ERROR_MESSAGE);
    }

    /**
     * Handles ConstraintViolationException.
     *
     * @param ex the exception
     * @return the response entity with error details
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDetails> handleConstraintViolationException(ConstraintViolationException ex) {
        return buildErrorResponse(VALIDATION_ERROR_MESSAGE);
    }

    /**
     * Handles CustomException.
     *
     * @param ex the exception
     * @return the response entity with error details
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorDetails> handleCustomException(CustomException ex) {
        ErrorDetailsError errorDetailsError = ex.getErrorDetails().getError();
        HttpStatus status = switch (errorDetailsError.getStatus()) {
            case "400" -> HttpStatus.BAD_REQUEST;
            case "404" -> HttpStatus.NOT_FOUND;
            case "409" -> HttpStatus.CONFLICT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        return new ResponseEntity<>(ex.getErrorDetails(), status);
    }

    /**
     * Handles UnrecognizedPropertyException.
     *
     * @param ex the exception
     * @return the response entity with error details
     */
    @ExceptionHandler(UnrecognizedPropertyException.class)
    public ResponseEntity<ErrorDetails> handleInvalidAttributeName(UnrecognizedPropertyException ex) {
        String message = "Attribute name '" + ex.getPropertyName() + "' is incorrect.";
        return buildErrorResponse(message);
    }

    private ResponseEntity<ErrorDetails> buildErrorResponse(String message) {
        ErrorDetailsError errorDetails = new ErrorDetailsError("400", "Bad Request", message);
        ErrorDetails errorResponse = new ErrorDetails(errorDetails);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}