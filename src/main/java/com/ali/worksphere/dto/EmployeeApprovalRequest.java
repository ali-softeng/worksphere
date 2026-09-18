package com.ali.worksphere.dto;

import com.ali.worksphere.myEnum.EmployeeRole;

public class EmployeeApprovalRequest {

    private Long departmentId;

    private EmployeeRole role;

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public EmployeeRole getRole() {
        return role;
    }

    public void setRole(EmployeeRole role) {
        this.role = role;
    }
}
