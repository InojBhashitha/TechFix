package com.techfix.api.services;

import com.techfix.api.dto.BookingResponseDto;
import com.techfix.api.dto.StaffDashboardStatsDto;
import com.techfix.api.dto.UpdateRepairStatusRequestDto;
import com.techfix.api.entities.BranchInventory;
import com.techfix.api.entities.RepairRequest;
import com.techfix.api.entities.RepairStatusHistory;
import com.techfix.api.entities.User;
import com.techfix.api.enums.RepairStatus;
import com.techfix.api.enums.UserRole;
import com.techfix.api.repositories.BranchInventoryRepository;
import com.techfix.api.repositories.RepairRequestRepository;
import com.techfix.api.repositories.RepairStatusHistoryRepository;
import com.techfix.api.repositories.TechnicianRepository;
import com.techfix.api.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StaffService {

    private final RepairRequestRepository repairRequestRepository;
    private final RepairStatusHistoryRepository statusHistoryRepository;
    private final BranchInventoryRepository branchInventoryRepository;
    private final TechnicianRepository technicianRepository;
    private final UserRepository userRepository;

    public StaffService(RepairRequestRepository repairRequestRepository,
                        RepairStatusHistoryRepository statusHistoryRepository,
                        BranchInventoryRepository branchInventoryRepository,
                        TechnicianRepository technicianRepository,
                        UserRepository userRepository) {
        this.repairRequestRepository = repairRequestRepository;
        this.statusHistoryRepository = statusHistoryRepository;
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

    @Transactional
    public BookingResponseDto updateRepairStatus(String identifier, UpdateRepairStatusRequestDto request, String staffEmail) {
        User staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new RuntimeException("Staff user not found with email: " + staffEmail));

        RepairRequest booking;
        if (identifier.matches("\\d+")) {
            Long id = Long.parseLong(identifier);
            booking = repairRequestRepository.findById(id)
                    .orElseGet(() -> repairRequestRepository.findByBookingReference(identifier)
                            .orElseThrow(() -> new RuntimeException("Repair booking not found with ID or reference: " + identifier)));
        } else {
            booking = repairRequestRepository.findByBookingReference(identifier)
                    .orElseThrow(() -> new RuntimeException("Repair booking not found with reference: " + identifier));
        }

        if (staff.getRole() == UserRole.STAFF) {
            if (staff.getBranchId() != null && !staff.getBranchId().equals(booking.getBranch().getId())) {
                throw new RuntimeException("Access Denied: Staff members can only update repairs assigned to their branch.");
            }
        }

        RepairStatus currentStatus = booking.getCurrentStatus();
        RepairStatus newStatus = request.getStatus();

        validateStatusTransition(currentStatus, newStatus);

        if (request.getAdditionalCost() != null && request.getAdditionalCost().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal currentCost = booking.getTotalCost() != null ? booking.getTotalCost() : BigDecimal.ZERO;
            booking.setTotalCost(currentCost.add(request.getAdditionalCost()));
        }

        if (currentStatus != newStatus) {
            booking.setCurrentStatus(newStatus);
            booking.setUpdatedAt(LocalDateTime.now());
            booking = repairRequestRepository.save(booking);

            String notes = request.getNotes() != null && !request.getNotes().isBlank()
                    ? request.getNotes()
                    : "Status updated to " + newStatus.getDisplayName();

            RepairStatusHistory history = new RepairStatusHistory(booking, newStatus, notes, staff);
            statusHistoryRepository.save(history);
        } else if (request.getNotes() != null && !request.getNotes().isBlank()) {
            RepairStatusHistory history = new RepairStatusHistory(booking, newStatus, request.getNotes(), staff);
            statusHistoryRepository.save(history);
        }

        return mapToBookingResponseDto(booking);
    }

    private void validateStatusTransition(RepairStatus current, RepairStatus target) {
        if (current == target) {
            return;
        }

        if (current == RepairStatus.COMPLETED) {
            throw new IllegalStateException("Cannot change status: Repair is already COMPLETED.");
        }
        if (current == RepairStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change status: Repair is already CANCELLED.");
        }

        if (target == RepairStatus.CANCELLED) {
            return; // Cancellation is allowed from any active repair state
        }

        // Exception for QA retry
        if (current == RepairStatus.QUALITY_CHECK && target == RepairStatus.REPAIRING) {
            return; // QA re-work allowed
        }

        if (target.getStepNumber() <= current.getStepNumber()) {
            throw new IllegalStateException("Invalid status transition from " + current.getDisplayName() + " to " + target.getDisplayName() + ". Status must progress forward.");
        }
    }

    public BookingResponseDto mapToBookingResponseDto(RepairRequest booking) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setBookingReference(booking.getBookingReference());
        dto.setCustomerName(booking.getCustomer() != null ? booking.getCustomer().getFullName() : null);
        dto.setCustomerEmail(booking.getCustomer() != null ? booking.getCustomer().getEmail() : null);
        dto.setBranchName(booking.getBranch() != null ? booking.getBranch().getName() : null);
        dto.setServiceName(booking.getService() != null ? booking.getService().getName() : null);
        if (booking.getService() != null && booking.getService().getCategory() != null) {
            dto.setCategoryName(booking.getService().getCategory().getName());
        }
        dto.setDeviceBrand(booking.getDeviceBrand());
        dto.setDeviceModel(booking.getDeviceModel());
        dto.setSerialNumber(booking.getSerialNumber());
        dto.setProblemDescription(booking.getProblemDescription());
        dto.setAppointmentDate(booking.getAppointmentDate());
        dto.setCurrentStatus(booking.getCurrentStatus());
        dto.setTotalCost(booking.getTotalCost());
        dto.setCreatedAt(booking.getCreatedAt());
        return dto;
    }
}
