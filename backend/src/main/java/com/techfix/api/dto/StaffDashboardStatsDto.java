package com.techfix.api.dto;

public class StaffDashboardStatsDto {
    private long totalRepairs;
    private long pendingRepairs;
    private long inProgressRepairs;
    private long completedRepairs;
    private long lowStockItemsCount;
    private long activeTechniciansCount;

    public StaffDashboardStatsDto() {}

    public StaffDashboardStatsDto(long totalRepairs, long pendingRepairs, long inProgressRepairs, long completedRepairs, long lowStockItemsCount, long activeTechniciansCount) {
        this.totalRepairs = totalRepairs;
        this.pendingRepairs = pendingRepairs;
        this.inProgressRepairs = inProgressRepairs;
        this.completedRepairs = completedRepairs;
        this.lowStockItemsCount = lowStockItemsCount;
        this.activeTechniciansCount = activeTechniciansCount;
    }

    public long getTotalRepairs() { return totalRepairs; }
    public void setTotalRepairs(long totalRepairs) { this.totalRepairs = totalRepairs; }

    public long getPendingRepairs() { return pendingRepairs; }
    public void setPendingRepairs(long pendingRepairs) { this.pendingRepairs = pendingRepairs; }

    public long getInProgressRepairs() { return inProgressRepairs; }
    public void setInProgressRepairs(long inProgressRepairs) { this.inProgressRepairs = inProgressRepairs; }

    public long getCompletedRepairs() { return completedRepairs; }
    public void setCompletedRepairs(long completedRepairs) { this.completedRepairs = completedRepairs; }

    public long getLowStockItemsCount() { return lowStockItemsCount; }
    public void setLowStockItemsCount(long lowStockItemsCount) { this.lowStockItemsCount = lowStockItemsCount; }

    public long getActiveTechniciansCount() { return activeTechniciansCount; }
    public void setActiveTechniciansCount(long activeTechniciansCount) { this.activeTechniciansCount = activeTechniciansCount; }
}
