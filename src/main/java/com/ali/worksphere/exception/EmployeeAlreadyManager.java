package com.ali.worksphere.exception;

public class EmployeeAlreadyManager extends RuntimeException{

    public EmployeeAlreadyManager(String message) {
        super(message);
    }
}
