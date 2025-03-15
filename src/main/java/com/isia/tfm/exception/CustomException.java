package com.isia.tfm.exception;

import com.isia.tfm.model.ErrorDetails;
import com.isia.tfm.model.ErrorDetailsError;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomException extends RuntimeException {

    private final transient ErrorDetails errorDetails;

    public CustomException(String status, String description, String message) {
        ErrorDetailsError errorDetailsError = new ErrorDetailsError(status, description, message);
        this.errorDetails = new ErrorDetails(errorDetailsError);
    }

}

