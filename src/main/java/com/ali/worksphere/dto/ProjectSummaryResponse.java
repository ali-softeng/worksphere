package com.ali.worksphere.dto;

import com.ali.worksphere.myEnum.ProjectStatus;

public class ProjectSummaryResponse {
    private Long Id;
    private String name;
    private ProjectStatus status;

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

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }
}
