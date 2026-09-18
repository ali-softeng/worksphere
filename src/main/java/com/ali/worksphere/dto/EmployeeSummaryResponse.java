package com.ali.worksphere.dto;

import com.ali.worksphere.myEnum.EmployeeRole;

public class EmployeeSummaryResponse {
    private Long Id;
    private String name;
    private EmployeeRole role;

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

    public EmployeeRole getRole() {
        return role;
    }

    public void setRole(EmployeeRole role) {
        this.role = role;
    }
}
