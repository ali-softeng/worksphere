package com.ali.worksphere.exception;

public class ProjectNameAlreadyExistsException extends RuntimeException {
    public ProjectNameAlreadyExistsException(String message) {
        super(message);
    }
}
