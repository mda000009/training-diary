package com.isia.tfm.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleValidationException(MethodArgumentNotValidException ex) {
        Error errorResponse = new Error("400", "Bad Request");
        errorResponse.setMessage("The request does not meet the validations");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Error> handleConstraintViolationException(ConstraintViolationException ex) {
        Error errorResponse = new Error("400", "Bad Request");
        errorResponse.setMessage("The request does not meet the validations");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Error> handleCustomException(CustomException ex) {
        Error errorResponse = ex.getError();
        return switch (errorResponse.getStatus()) {
            case "400" -> new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            case "401" -> new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
            case "404" -> new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            case "409" -> new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
            case "500" -> new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            default -> new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
        };
    }

    @ExceptionHandler(UnrecognizedPropertyException.class)
    public ResponseEntity<Error> handleInvalidAttributeName(UnrecognizedPropertyException ex) {
        Error errorResponse = new Error("400", "Bad Request");
        errorResponse.setMessage("Attribute name '" + ex.getPropertyName() + "' is incorrect.");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
