package com.ali.worksphere.entity;

import com.ali.worksphere.myEnum.DepartmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Entity
@Table(name = "department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private DepartmentStatus status;

    @OneToOne
    @JoinColumn(name = "manager_id", unique = true)
    private Employee manager;

    @OneToMany(mappedBy = "department")
    private List<Employee> employees;

    @OneToMany(mappedBy = "department")
    private List<Project> projects;

    public Department() {

    }


    public Department(Long id, String name, DepartmentStatus status) {
        this.id = id;
        this.name = name;
        this.status = status;
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

    public DepartmentStatus getStatus() {
        return status;
    }

    public void setStatus(DepartmentStatus status) {
        this.status = status;
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

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}
