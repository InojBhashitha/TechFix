package com.techfix.api.services;

import com.techfix.api.dto.StaffDashboardStatsDto;
import com.techfix.api.entities.BranchInventory;
import com.techfix.api.entities.User;
import com.techfix.api.enums.RepairStatus;
import com.techfix.api.enums.UserRole;
import com.techfix.api.repositories.BranchInventoryRepository;
import com.techfix.api.repositories.RepairRequestRepository;
import com.techfix.api.repositories.TechnicianRepository;
import com.techfix.api.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StaffService {

    private final RepairRequestRepository repairRequestRepository;
    private final BranchInventoryRepository branchInventoryRepository;
    private final TechnicianRepository technicianRepository;
    private final UserRepository userRepository;

    public StaffService(RepairRequestRepository repairRequestRepository,
                        BranchInventoryRepository branchInventoryRepository,
                        TechnicianRepository technicianRepository,
                        UserRepository userRepository) {
        this.repairRequestRepository = repairRequestRepository;
        this.branchInventoryRepository = branchInventoryRepository;
        this.technicianRepository = technicianRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public StaffDashboardStatsDto getDashboardStats(String userEmail, Long requestedBranchId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        Long branchId = requestedBranchId;
        if (branchId == null && user.getRole() == UserRole.STAFF) {
            branchId = user.getBranchId();
        }

        List<RepairStatus> pendingStatuses = List.of(
                RepairStatus.REQUEST_SUBMITTED,
                RepairStatus.BRANCH_ASSIGNED,
                RepairStatus.DEVICE_RECEIVED
        );

        List<RepairStatus> inProgressStatuses = List.of(
                RepairStatus.DIAGNOSIS,
                RepairStatus.REPAIRING,
                RepairStatus.QUALITY_CHECK
        );

        List<RepairStatus> completedStatuses = List.of(
                RepairStatus.READY_FOR_COLLECTION,
                RepairStatus.COMPLETED
        );

        List<RepairStatus> cancelledStatuses = List.of(
                RepairStatus.CANCELLED
        );

        long totalRepairs;
        long pendingCount;
        long inProgressCount;
        long completedCount;
        long cancelledCount;
        long lowStockCount;
        long availableTechsCount;

        if (branchId != null) {
            totalRepairs = repairRequestRepository.countByBranchId(branchId);
            pendingCount = repairRequestRepository.countByBranchIdAndCurrentStatusIn(branchId, pendingStatuses);
            inProgressCount = repairRequestRepository.countByBranchIdAndCurrentStatusIn(branchId, inProgressStatuses);
            completedCount = repairRequestRepository.countByBranchIdAndCurrentStatusIn(branchId, completedStatuses);
            cancelledCount = repairRequestRepository.countByBranchIdAndCurrentStatusIn(branchId, cancelledStatuses);

            List<BranchInventory> inventoryList = branchInventoryRepository.findByBranchId(branchId);
            lowStockCount = inventoryList.stream()
                    .filter(i -> i.getQuantity() != null && i.getMinimumStockAlert() != null && i.getQuantity() <= i.getMinimumStockAlert())
                    .count();

            availableTechsCount = technicianRepository.countByBranchIdAndIsAvailableTrue(branchId);
        } else {
            totalRepairs = repairRequestRepository.count();
            pendingCount = repairRequestRepository.countByCurrentStatusIn(pendingStatuses);
            inProgressCount = repairRequestRepository.countByCurrentStatusIn(inProgressStatuses);
            completedCount = repairRequestRepository.countByCurrentStatusIn(completedStatuses);
            cancelledCount = repairRequestRepository.countByCurrentStatusIn(cancelledStatuses);

            List<BranchInventory> inventoryList = branchInventoryRepository.findAll();
            lowStockCount = inventoryList.stream()
                    .filter(i -> i.getQuantity() != null && i.getMinimumStockAlert() != null && i.getQuantity() <= i.getMinimumStockAlert())
                    .count();

            availableTechsCount = technicianRepository.countByIsAvailableTrue();
        }

        return new StaffDashboardStatsDto(
                totalRepairs,
                pendingCount,
                inProgressCount,
                completedCount,
                cancelledCount,
                lowStockCount,
                availableTechsCount
        );
    }
}
