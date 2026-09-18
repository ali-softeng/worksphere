package com.ali.worksphere.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DepartmentAlreadyExistsException.class)
    public ResponseEntity<?> handleDepartmentAlreadyExists(
            DepartmentAlreadyExistsException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DepartmentNotFoundException.class)
    public ResponseEntity<?> handleDepartmentNotFound(
            DepartmentNotFoundException ex){
        return new ResponseEntity<>(ex.getMessage(),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DepartmentInactiveException.class)
    public ResponseEntity<?> handleDepartmentInactive(
            DepartmentInactiveException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<?> handleEmployeeNotFound(
            EmployeeNotFoundException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmployeeAlreadyInactiveException.class)
    public ResponseEntity<?> handleEmployeeAlreadyInactive(
            EmployeeAlreadyInactiveException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DepartmentHasActiveEmployeesException.class)
    public ResponseEntity<?> handleDepartmentHasActiveEmployees(
            DepartmentHasActiveEmployeesException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(SameDepartmentException.class)
    public ResponseEntity<?> handleSameDepartment(
            SameDepartmentException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmployeeInactiveException.class)
    public ResponseEntity<?> handleEmployeeInactive(
            EmployeeInactiveException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DifferentDepartmentException.class)
    public ResponseEntity<?> handleDifferentDepartment(
            DifferentDepartmentException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmployeeAlreadyManager.class)
    public ResponseEntity<?> handleEmployeeAlreadyManager(
            EmployeeAlreadyManager ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }


    @ExceptionHandler(DepartmentAlreadyHasManagerException.class)
    public ResponseEntity<?> handleDepartmentAlreadyHasManager(
            DepartmentAlreadyHasManagerException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DepartmentManagerAlreadyNull.class)
    public ResponseEntity<?> handleDepartmentManagerAlreadyNull(
            DepartmentManagerAlreadyNull ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<?> handleProjectNotFound(
            ProjectNotFoundException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ProjectInactiveException.class)
    public ResponseEntity<?> handleProjectInactive(
            ProjectInactiveException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ProjectAlreadyActiveException.class)
    public ResponseEntity<?> handleProjectAlreadyActive(
            ProjectAlreadyActiveException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ProjectManagerAlreadyNull.class)
    public ResponseEntity<?> handleProjectManagerAlreadyNull(
            ProjectManagerAlreadyNull ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ProjectNotActiveException.class)
    public ResponseEntity<?> handleProjectNotActive(
            ProjectNotActiveException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ProjectAlreadyHasManagerException.class)
    public ResponseEntity<?> handleProjectAlreadyHasManager(
            ProjectAlreadyHasManagerException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmployeeAlreadyAssign.class)
    public ResponseEntity<?> handleEmployeeAlreadyAssign(
            EmployeeAlreadyAssign ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmployeeNotAssignedException.class)
    public ResponseEntity<?> handleEmployeeNotAssigned(
            EmployeeNotAssignedException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmployeeManagerCannotBeRemoved.class)
    public ResponseEntity<?> handleEmployeeManagerCannotBeRemoved(
            EmployeeManagerCannotBeRemoved ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ProjectNameAlreadyExistsException.class)
    public ResponseEntity<?> handleProjectNameAlreadyExists(
            ProjectNameAlreadyExistsException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<?> handleEmailAlreadyExists(
            EmailAlreadyExistsException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationErrorResponse>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex){
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<ValidationErrorResponse> errorResponses = new ArrayList<>();

        for(FieldError error : fieldErrors){
            errorResponses.add(new ValidationErrorResponse(error.getField(), error.getDefaultMessage()));
        }

        return new ResponseEntity<>(errorResponses, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentials(
            InvalidCredentialsException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmployeePendingException.class)
    public ResponseEntity<?> handleEmployeePendingException(
            EmployeePendingException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmployeeNotPendingException.class)
    public ResponseEntity<?> handleEmployeeNotPendingException(
            EmployeeNotPendingException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnauthorizedRoleAssignmentException.class)
    public ResponseEntity<?> handleUnauthorizedRoleAssignmentException(
            UnauthorizedRoleAssignmentException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<?> handleUnauthorizedAccessException(
            UnauthorizedAccessException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<?> handleInvalidRefreshToken(
            InvalidRefreshTokenException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ProjectNotAssignedException.class)
    public ResponseEntity<?> handleNotAssignedException (
            ProjectNotAssignedException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }
}
