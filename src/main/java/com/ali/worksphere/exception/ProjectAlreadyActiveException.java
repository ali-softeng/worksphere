package com.ali.worksphere.exception;

public class ProjectAlreadyActiveException extends RuntimeException {
    public ProjectAlreadyActiveException(String message) {

        super(message);
    }
}
