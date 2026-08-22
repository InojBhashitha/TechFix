package com.techfix.app.data.remote.dto;

public class AssignTechnicianRequestDto {
    private Long technicianId;
    private String notes;

    public AssignTechnicianRequestDto() {}

    public AssignTechnicianRequestDto(Long technicianId, String notes) {
        this.technicianId = technicianId;
        this.notes = notes;
    }

    public Long getTechnicianId() { return technicianId; }
    public void setTechnicianId(Long technicianId) { this.technicianId = technicianId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
