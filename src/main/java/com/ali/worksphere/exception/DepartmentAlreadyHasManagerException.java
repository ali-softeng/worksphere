package com.ali.worksphere.exception;

public class DepartmentAlreadyHasManagerException extends RuntimeException{

    public DepartmentAlreadyHasManagerException(String message) {
        super(message);
    }
}
