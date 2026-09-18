package com.ali.worksphere.dto;

import com.ali.worksphere.myEnum.DepartmentStatus;

public class DepartmentResponse {

    private Long id;
    private String name;
    private DepartmentStatus status;

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
}
