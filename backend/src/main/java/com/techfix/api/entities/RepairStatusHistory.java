package com.techfix.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.techfix.api.enums.RepairStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "repair_status_history")
public class RepairStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repair_request_id", nullable = false)
    private RepairRequest repairRequest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RepairStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "updated_by_user_id")
    private User updatedBy;

    private LocalDateTime timestamp = LocalDateTime.now();

    public RepairStatusHistory() {}

    public RepairStatusHistory(RepairRequest repairRequest, RepairStatus status, String notes, User updatedBy) {
        this.repairRequest = repairRequest;
        this.status = status;
        this.notes = notes;
        this.updatedBy = updatedBy;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public RepairRequest getRepairRequest() { return repairRequest; }
    public void setRepairRequest(RepairRequest repairRequest) { this.repairRequest = repairRequest; }

    public RepairStatus getStatus() { return status; }
    public void setStatus(RepairStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public User getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(User updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
