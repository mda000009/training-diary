package com.isia.tfm.exception;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import jakarta.validation.ConstraintViolationException;
import com.isia.tfm.model.Error;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureObservability
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleValidationException() {
        BindingResult bindingResult = new BeanPropertyBindingResult(null, "objectName");
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        ResponseEntity<Error> response = globalExceptionHandler.handleValidationException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("The request does not meet the validations", response.getBody().getMessage());
    }

    @Test
    void testHandleConstraintViolationException() {
        ConstraintViolationException ex = new ConstraintViolationException(null);
        ResponseEntity<Error> response = globalExceptionHandler.handleConstraintViolationException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("The request does not meet the validations", response.getBody().getMessage());
    }

    @Test
    void testHandleCustomException() {
        CustomException ex = new CustomException("409", "Conflict", "The username is already in use");
        ResponseEntity<Error> response = globalExceptionHandler.handleCustomException(ex);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("The username is already in use", response.getBody().getMessage());
    }

    @Test
    void testHandleInvalidAttributeName() {
        UnrecognizedPropertyException ex = new UnrecognizedPropertyException(null, null, null, null, "invalidProperty", null);
        ResponseEntity<Error> response = globalExceptionHandler.handleInvalidAttributeName(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Attribute name 'invalidProperty' is incorrect.", response.getBody().getMessage());
    }

}
