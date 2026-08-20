package com.techfix.api.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "technicians")
public class Technician {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String specialization;

    private Boolean isAvailable = true;

    private Integer activeRepairsCount = 0;

    public Technician() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Branch getBranch() { return branch; }
    public void setBranch(Branch branch) { this.branch = branch; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean available) { isAvailable = available; }

    public Integer getActiveRepairsCount() { return activeRepairsCount; }
    public void setActiveRepairsCount(Integer activeRepairsCount) { this.activeRepairsCount = activeRepairsCount; }
}
