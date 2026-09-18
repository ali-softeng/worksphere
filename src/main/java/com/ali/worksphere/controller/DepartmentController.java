package com.ali.worksphere.controller;

import com.ali.worksphere.dto.*;
import com.ali.worksphere.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService){
        this.departmentService = departmentService;
    }

    @PostMapping("/departments")
    public ResponseEntity<DepartmentResponse> createDepartment(@Valid @RequestBody DepartmentRequest request){
        DepartmentResponse response =  departmentService.createDepartment(request.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }


    @GetMapping("/departments/{id}")
    public ResponseEntity<DepartmentDetailResponse> getDepartmentById(@PathVariable Long id){
        DepartmentDetailResponse response = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments(){
        List<DepartmentResponse> responses = departmentService.getAllDepartments();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/departments/{id}")
    public ResponseEntity<DepartmentResponse> updateDepartment(@Valid @PathVariable Long id, @RequestBody DepartmentRequest request){
        DepartmentResponse response = departmentService.updateDepartment(id,request.getName());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/departments/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id){
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/departments/{id}/manager")
    public ResponseEntity<ManagerResponse> assignManager(@Valid @PathVariable Long id, @RequestBody ManagerRequest request){
        ManagerResponse response = departmentService.assignManager(id,request.getEmployeeId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/departments/{id}/manager")
    public ResponseEntity<ManagerResponse> changeManager(@Valid @PathVariable Long id, @RequestBody ManagerRequest request){
        ManagerResponse response = departmentService.changeManager(id,request.getEmployeeId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/departments/{id}/manager")
    public ResponseEntity<Void> deleteManager(@PathVariable Long id){
        departmentService.deleteManager(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/departments/me")
    public ResponseEntity<MyDepartmentResponse> getMyDepartment(){
        MyDepartmentResponse response = departmentService.getMyDepartment();
        return ResponseEntity.ok(response);
    }

}
