package com.ali.worksphere.entity;


import com.ali.worksphere.myEnum.ProjectStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status;


    @ManyToOne
    @JoinColumn(name = "department_id",nullable = false)
    private Department department;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Employee manager;


    @ManyToMany
    @JoinTable(
            name = "project_employee",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private List<Employee> employees = new ArrayList<>();

    public Project() {
    }

    public Project(Long id, String name, ProjectStatus status, Department department, Employee manager, List<Employee> employees) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.department = department;
        this.manager = manager;
        this.employees = employees;
    }

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

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Employee getManager() {
        return manager;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }
}
