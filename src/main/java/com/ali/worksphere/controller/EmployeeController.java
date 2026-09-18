package com.ali.worksphere.controller;

import com.ali.worksphere.dto.*;
import com.ali.worksphere.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }


    @PostMapping("/employees")
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest request){
        EmployeeResponse response = employeeService.createEmployee(request.getName(), request.getDepartmentId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id){
        EmployeeResponse response = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees(){
        List<EmployeeResponse> responses = employeeService.getAllEmployees();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(@Valid @PathVariable Long id,
                                                           @RequestBody EmployeeUpdateRequest request){
        EmployeeResponse response = employeeService.updateEmployee(id, request.getDepartmentId(), request.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employees/pending")
    public ResponseEntity<List<EmployeeResponse>> getAllPendingEmployees(){
        List<EmployeeResponse> responses = employeeService.getPendingEmployees();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/employees/{id}/approve")
    public ResponseEntity<EmployeeResponse> approveEmployee(@Valid @PathVariable Long id, @RequestBody EmployeeApprovalRequest request){
        EmployeeResponse response = employeeService.approveEmployee(id, request.getDepartmentId(), request.getRole());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<EmployeeResponse> deleteEmployee(@PathVariable Long id){
        EmployeeResponse response = employeeService.deleteEmployee(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employees/me")
    public ResponseEntity<EmployeeResponse> getMyProfile(){
        EmployeeResponse response = employeeService.getMyProfile();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employees/my-team")
    public ResponseEntity<List<EmployeeSummaryResponse>> getMyTeam(){
        List<EmployeeSummaryResponse> responses = employeeService.getMyTeam();
        return ResponseEntity.ok(responses);
    }
}
