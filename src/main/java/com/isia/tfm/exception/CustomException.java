package com.isia.tfm.exception;

import com.isia.tfm.model.Error;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomException extends RuntimeException {

    private final transient Error error;

    public CustomException(String status, String error, String message) {
        super(message);
        this.error = new Error(status, error);
        this.error.setMessage(message);
    }

}

