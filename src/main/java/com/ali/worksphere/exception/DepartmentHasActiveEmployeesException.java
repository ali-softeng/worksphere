package com.ali.worksphere.exception;

public class DepartmentHasActiveEmployeesException extends  RuntimeException{

    public DepartmentHasActiveEmployeesException(String message) {
        super(message);
    }
}
