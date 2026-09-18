package com.ali.worksphere.dto;

import com.ali.worksphere.myEnum.DepartmentStatus;

import java.util.List;

public class DepartmentDetailResponse {
    private Long id;
    private String name;
    private DepartmentStatus status;
    private String managerName;
    private List<EmployeeSummaryResponse> employees;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public List<EmployeeSummaryResponse> getEmployees() {
        return employees;
    }

    public void setEmployees(List<EmployeeSummaryResponse> employees) {
        this.employees = employees;
    }
}
