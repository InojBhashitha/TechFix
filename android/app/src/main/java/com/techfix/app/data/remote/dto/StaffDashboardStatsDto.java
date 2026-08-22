package com.techfix.app.data.remote.dto;

public class StaffDashboardStatsDto {
    private Long totalRepairs;
    private Long pendingRepairs;
    private Long inProgressRepairs;
    private Long completedRepairs;
    private Long cancelledRepairs;
    private Long lowStockAlerts;
    private Long availableTechnicians;

    public StaffDashboardStatsDto() {}

    public StaffDashboardStatsDto(Long totalRepairs, Long pendingRepairs, Long inProgressRepairs,
                                 Long completedRepairs, Long cancelledRepairs,
                                 Long lowStockAlerts, Long availableTechnicians) {
        this.totalRepairs = totalRepairs;
        this.pendingRepairs = pendingRepairs;
        this.inProgressRepairs = inProgressRepairs;
        this.completedRepairs = completedRepairs;
        this.cancelledRepairs = cancelledRepairs;
        this.lowStockAlerts = lowStockAlerts;
        this.availableTechnicians = availableTechnicians;
    }

    public Long getTotalRepairs() { return totalRepairs; }
    public void setTotalRepairs(Long totalRepairs) { this.totalRepairs = totalRepairs; }

    public Long getPendingRepairs() { return pendingRepairs; }
    public void setPendingRepairs(Long pendingRepairs) { this.pendingRepairs = pendingRepairs; }

    public Long getInProgressRepairs() { return inProgressRepairs; }
    public void setInProgressRepairs(Long inProgressRepairs) { this.inProgressRepairs = inProgressRepairs; }

    public Long getCompletedRepairs() { return completedRepairs; }
    public void setCompletedRepairs(Long completedRepairs) { this.completedRepairs = completedRepairs; }

    public Long getCancelledRepairs() { return cancelledRepairs; }
    public void setCancelledRepairs(Long cancelledRepairs) { this.cancelledRepairs = cancelledRepairs; }

    public Long getLowStockAlerts() { return lowStockAlerts; }
    public void setLowStockAlerts(Long lowStockAlerts) { this.lowStockAlerts = lowStockAlerts; }

    public Long getAvailableTechnicians() { return availableTechnicians; }
    public void setAvailableTechnicians(Long availableTechnicians) { this.availableTechnicians = availableTechnicians; }
}
