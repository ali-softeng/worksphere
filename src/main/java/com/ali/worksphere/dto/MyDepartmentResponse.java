package com.ali.worksphere.dto;

import com.ali.worksphere.myEnum.DepartmentStatus;

import java.util.List;

public class MyDepartmentResponse {

    private Long Id;
    private String name;
    private DepartmentStatus status;

    private String manager;

    private List<EmployeeSummaryResponse> employees;

    private List<ProjectSummaryResponse> projects;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DepartmentStatus getStatus() {
        return status;
    }

    public void setStatus(DepartmentStatus status) {
        this.status = status;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public List<EmployeeSummaryResponse> getEmployees() {
        return employees;
    }

    public void setEmployees(List<EmployeeSummaryResponse> employees) {
        this.employees = employees;
    }

    public List<ProjectSummaryResponse> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectSummaryResponse> projects) {
        this.projects = projects;
    }
}
