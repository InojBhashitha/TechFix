package com.techfix.app.data.remote.dto;

public class UserProfile {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private Long branchId;
    private String createdAt;

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }
    public Long getBranchId() { return branchId; }
    public String getCreatedAt() { return createdAt; }
}
