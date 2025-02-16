package com.isia.tfm.exception;

import com.isia.tfm.model.Error;

public class CustomException extends RuntimeException {

    private Error error;

    public CustomException(String status, String error, String message) {
        super(message);
        this.error = new Error(status, error);
        this.error.setMessage(message);
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

}

