package com.ali.worksphere.exception;

public class DepartmentAlreadyExistsException extends RuntimeException{

    public DepartmentAlreadyExistsException(String message) {
        super(message);
    }
}
