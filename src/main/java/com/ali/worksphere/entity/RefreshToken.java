package com.ali.worksphere.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private boolean revoked;

    @ManyToOne
    @JoinColumn(name = "employee_id",nullable = false)
    private Employee employee;

    public RefreshToken() {
    }

    public RefreshToken(Long id, String refreshToken, LocalDateTime expiryDate, Employee employee, boolean revoked) {
        this.id = id;
        this.refreshToken = refreshToken;
        this.expiryDate = expiryDate;
        this.employee = employee;
        this.revoked = revoked;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }
}
