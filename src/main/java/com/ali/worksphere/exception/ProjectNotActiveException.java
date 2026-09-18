package com.ali.worksphere.exception;

public class ProjectNotActiveException extends RuntimeException {
    public ProjectNotActiveException(String message) {
        super(message);
    }
}
