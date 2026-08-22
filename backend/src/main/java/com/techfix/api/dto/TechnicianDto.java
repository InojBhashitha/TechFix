package com.techfix.api.dto;

public class TechnicianDto {
    private Long id;
    private Long branchId;
    private String branchName;
    private String fullName;
    private String specialization;
    private Boolean isAvailable;
    private Integer activeRepairsCount;

    public TechnicianDto() {}

    public TechnicianDto(Long id, Long branchId, String branchName, String fullName,
                         String specialization, Boolean isAvailable, Integer activeRepairsCount) {
        this.id = id;
        this.branchId = branchId;
        this.branchName = branchName;
        this.fullName = fullName;
        this.specialization = specialization;
        this.isAvailable = isAvailable;
        this.activeRepairsCount = activeRepairsCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean available) { isAvailable = available; }

    public Integer getActiveRepairsCount() { return activeRepairsCount; }
    public void setActiveRepairsCount(Integer activeRepairsCount) { this.activeRepairsCount = activeRepairsCount; }
}
