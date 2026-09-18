package com.ali.worksphere.exception;

public class EmployeeAlreadyInactiveException extends RuntimeException{

    public EmployeeAlreadyInactiveException(String message) {
        super(message);
    }
}
