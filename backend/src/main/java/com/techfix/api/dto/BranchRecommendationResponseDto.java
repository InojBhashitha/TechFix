package com.techfix.api.dto;

import java.util.List;

public class BranchRecommendationResponseDto {
    private BranchDto recommendedBranch;
    private Double distanceKm;
    private Boolean isTechnicianAvailable;
    private Boolean isPartAvailable;
    private String openingHours;
    private String reason;
    private List<BranchDetailDto> allBranches;

    public BranchRecommendationResponseDto() {}

    public BranchRecommendationResponseDto(BranchDto recommendedBranch, Double distanceKm, Boolean isTechnicianAvailable, Boolean isPartAvailable, String openingHours, String reason, List<BranchDetailDto> allBranches) {
        this.recommendedBranch = recommendedBranch;
        this.distanceKm = distanceKm;
        this.isTechnicianAvailable = isTechnicianAvailable;
        this.isPartAvailable = isPartAvailable;
        this.openingHours = openingHours;
        this.reason = reason;
        this.allBranches = allBranches;
    }

    public BranchDto getRecommendedBranch() { return recommendedBranch; }
    public void setRecommendedBranch(BranchDto recommendedBranch) { this.recommendedBranch = recommendedBranch; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public Boolean getIsTechnicianAvailable() { return isTechnicianAvailable; }
    public void setIsTechnicianAvailable(Boolean technicianAvailable) { isTechnicianAvailable = technicianAvailable; }

    public Boolean getIsPartAvailable() { return isPartAvailable; }
    public void setIsPartAvailable(Boolean partAvailable) { isPartAvailable = partAvailable; }

    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public List<BranchDetailDto> getAllBranches() { return allBranches; }
    public void setAllBranches(List<BranchDetailDto> allBranches) { this.allBranches = allBranches; }

    public static class BranchDetailDto {
        private BranchDto branch;
        private Double distanceKm;
        private Boolean isTechnicianAvailable;
        private Boolean isPartAvailable;
        private String openingHours;

        public BranchDetailDto() {}

        public BranchDetailDto(BranchDto branch, Double distanceKm, Boolean isTechnicianAvailable, Boolean isPartAvailable, String openingHours) {
            this.branch = branch;
            this.distanceKm = distanceKm;
            this.isTechnicianAvailable = isTechnicianAvailable;
            this.isPartAvailable = isPartAvailable;
            this.openingHours = openingHours;
        }

        public BranchDto getBranch() { return branch; }
        public void setBranch(BranchDto branch) { this.branch = branch; }

        public Double getDistanceKm() { return distanceKm; }
        public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

        public Boolean getIsTechnicianAvailable() { return isTechnicianAvailable; }
        public void setIsTechnicianAvailable(Boolean technicianAvailable) { isTechnicianAvailable = technicianAvailable; }

        public Boolean getIsPartAvailable() { return isPartAvailable; }
        public void setIsPartAvailable(Boolean partAvailable) { isPartAvailable = partAvailable; }

        public String getOpeningHours() { return openingHours; }
        public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }
    }
}
