package com.ali.worksphere.controller;


import com.ali.worksphere.dto.*;
import com.ali.worksphere.myEnum.ProjectStatus;
import com.ali.worksphere.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }


    @PostMapping("/projects")
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request){
        ProjectResponse response = projectService.createProject(request.getName(),request.getDepartmentId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/projects/{id}/manager")
    public ResponseEntity<ProjectResponse> assignManager(@Valid @PathVariable Long id, @RequestBody ProjectManagerRequest request){
        ProjectResponse response = projectService.assignProjectManager(id,request.getEmployeeId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/projects/{id}/manager")
    public ResponseEntity<ProjectResponse> changeManager(@Valid @PathVariable Long id, @RequestBody ProjectManagerRequest request){
        ProjectResponse response = projectService.changeProjectManager(id,request.getEmployeeId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/projects/{id}/manager")
    public ResponseEntity<Void> deleteManager(@PathVariable Long id){
        projectService.deleteProjectManager(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/projects/{id}/employees")
    public ResponseEntity<ProjectResponse> assignEmployee(@Valid @PathVariable Long id, @RequestBody ProjectEmployeeRequest request){
        ProjectResponse response = projectService.assignEmployeeToProject(id,request.getEmployeeId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/projects/{id}/employees/{employeeId}")
    public ResponseEntity<ProjectResponse> removeEmployee(@PathVariable Long id, @PathVariable Long employeeId){
        ProjectResponse response = projectService.removeEmployeeFromProject(id,employeeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/projects/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id){
        ProjectResponse response = projectService.getProjectById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectResponse>> getAllProjects(){
        List<ProjectResponse> responses = projectService.getAllProjects();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/projects/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@Valid @PathVariable Long id, @RequestBody ProjectRequest request){
        ProjectResponse response = projectService.updateProject(request.getName(),id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id){
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/projects/status/{status}")
    public ResponseEntity<List<ProjectResponse>> getProjectStatus(@PathVariable ProjectStatus status){
        List<ProjectResponse> responses = projectService.getProjectByStatus(status);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/projects/me")
    public ResponseEntity<List<ProjectSummaryResponse>> getMyProjects(){
        List<ProjectSummaryResponse> response = projectService.getMyProjects();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/projects/my-department")
    public ResponseEntity<List<ProjectSummaryResponse>> getDepartmentProjects(){
        List<ProjectSummaryResponse> responses = projectService.getDepartmentProjects();
        return ResponseEntity.ok(responses);
    }
}
