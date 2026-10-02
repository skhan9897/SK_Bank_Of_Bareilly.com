package com.skbank.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Admin implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long adminId;
    private Long userId;
    private Long employeeId;
    private AdminRole adminRole = AdminRole.SUPER_ADMIN;
    private String status;
    private Timestamp createdAt;

    // Joined fields
    private String username;
    private String fullName;
    private String email;

    public Admin() {}

    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public AdminRole getAdminRole() { return adminRole; }
    public void setAdminRole(AdminRole adminRole) { this.adminRole = adminRole; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
