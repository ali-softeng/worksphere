package com.ali.worksphere.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String message) {

        super(message);
    }
}
