package com.techfix.api.enums;

public enum RepairStatus {
    REQUEST_SUBMITTED("Request Submitted", "Customer submitted repair request online", 1),
    BRANCH_ASSIGNED("Branch Assigned", "Nearest branch and technician allocated", 2),
    DEVICE_RECEIVED("Device Received", "Device handed over at branch counter", 3),
    DIAGNOSIS("Diagnostic & Inspection", "Technician inspecting hardware faults", 4),
    REPAIRING("Repair in Progress", "Component replacement and soldering in progress", 5),
    QUALITY_CHECK("Quality Check (QA)", "Testing functionality, display, battery & sensors", 6),
    READY_FOR_COLLECTION("Ready for Collection", "Device tested, cleaned, and ready for pickup", 7),
    COMPLETED("Repair Completed", "Device collected and payment settled", 8),
    CANCELLED("Cancelled", "Repair cancelled by customer or branch", 0);

    private final String displayName;
    private final String description;
    private final int stepNumber;

    RepairStatus(String displayName, String description, int stepNumber) {
        this.displayName = displayName;
        this.description = description;
        this.stepNumber = stepNumber;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getStepNumber() {
        return stepNumber;
    }
}
