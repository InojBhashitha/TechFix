package com.techfix.api.services;

import com.techfix.api.dto.AssignTechnicianRequestDto;
import com.techfix.api.dto.BookingResponseDto;
import com.techfix.api.dto.InventoryStockDto;
import com.techfix.api.dto.StaffDashboardStatsDto;
import com.techfix.api.dto.TechnicianDto;
import com.techfix.api.dto.UpdateInventoryStockRequestDto;
import com.techfix.api.dto.UpdateRepairStatusRequestDto;
import com.techfix.api.entities.BranchInventory;
import com.techfix.api.entities.RepairRequest;
import com.techfix.api.entities.RepairStatusHistory;
import com.techfix.api.entities.SparePart;
import com.techfix.api.entities.Technician;
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
import java.util.stream.Collectors;

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

        RepairRequest booking = findBookingByIdentifier(identifier);

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

    @Transactional
    public BookingResponseDto assignTechnician(String identifier, AssignTechnicianRequestDto request, String staffEmail) {
        User staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new RuntimeException("Staff user not found with email: " + staffEmail));

        RepairRequest booking = findBookingByIdentifier(identifier);

        if (staff.getRole() == UserRole.STAFF) {
            if (staff.getBranchId() != null && !staff.getBranchId().equals(booking.getBranch().getId())) {
                throw new RuntimeException("Access Denied: Staff members can only assign technicians to repairs in their branch.");
            }
        }

        Technician technician = technicianRepository.findById(request.getTechnicianId())
                .orElseThrow(() -> new RuntimeException("Technician not found with ID: " + request.getTechnicianId()));

        if (!technician.getBranch().getId().equals(booking.getBranch().getId())) {
            throw new RuntimeException("Technician does not belong to branch: " + booking.getBranch().getName());
        }

        if (Boolean.FALSE.equals(technician.getIsAvailable())) {
            throw new RuntimeException("Technician " + technician.getFullName() + " is currently marked as unavailable.");
        }

        // Adjust active repairs count if replacing previous technician
        if (booking.getTechnician() != null && !booking.getTechnician().getId().equals(technician.getId())) {
            Technician prevTech = booking.getTechnician();
            if (prevTech.getActiveRepairsCount() != null && prevTech.getActiveRepairsCount() > 0) {
                prevTech.setActiveRepairsCount(prevTech.getActiveRepairsCount() - 1);
                technicianRepository.save(prevTech);
            }
        }

        if (booking.getTechnician() == null || !booking.getTechnician().getId().equals(technician.getId())) {
            technician.setActiveRepairsCount((technician.getActiveRepairsCount() != null ? technician.getActiveRepairsCount() : 0) + 1);
            technicianRepository.save(technician);
        }

        booking.setTechnician(technician);

        // Update status to BRANCH_ASSIGNED if it is currently in REQUEST_SUBMITTED
        if (booking.getCurrentStatus() == RepairStatus.REQUEST_SUBMITTED) {
            booking.setCurrentStatus(RepairStatus.BRANCH_ASSIGNED);
        }

        booking.setUpdatedAt(LocalDateTime.now());
        booking = repairRequestRepository.save(booking);

        String notes = request.getNotes() != null && !request.getNotes().isBlank()
                ? request.getNotes()
                : "Assigned technician: " + technician.getFullName();

        RepairStatusHistory history = new RepairStatusHistory(booking, booking.getCurrentStatus(), notes, staff);
        statusHistoryRepository.save(history);

        return mapToBookingResponseDto(booking);
    }

    @Transactional(readOnly = true)
    public List<TechnicianDto> getTechnicians(String staffEmail, Long requestedBranchId, Boolean availableOnly) {
        User staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + staffEmail));

        Long branchId = requestedBranchId;
        if (branchId == null && staff.getRole() == UserRole.STAFF) {
            branchId = staff.getBranchId();
        }

        List<Technician> technicians;
        if (branchId != null) {
            if (Boolean.TRUE.equals(availableOnly)) {
                technicians = technicianRepository.findByBranchIdAndIsAvailableTrue(branchId);
            } else {
                technicians = technicianRepository.findByBranchId(branchId);
            }
        } else {
            if (Boolean.TRUE.equals(availableOnly)) {
                technicians = technicianRepository.findByIsAvailableTrue();
            } else {
                technicians = technicianRepository.findAll();
            }
        }

        return technicians.stream()
                .map(this::mapToTechnicianDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InventoryStockDto> getInventory(String staffEmail, Long requestedBranchId, Boolean lowStockOnly) {
        User staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + staffEmail));

        Long branchId = requestedBranchId;
        if (branchId == null && staff.getRole() == UserRole.STAFF) {
            branchId = staff.getBranchId();
        }

        List<BranchInventory> inventoryList;
        if (branchId != null) {
            inventoryList = branchInventoryRepository.findByBranchId(branchId);
        } else {
            inventoryList = branchInventoryRepository.findAll();
        }

        return inventoryList.stream()
                .filter(i -> {
                    if (Boolean.TRUE.equals(lowStockOnly)) {
                        return i.getQuantity() != null && i.getMinimumStockAlert() != null && i.getQuantity() <= i.getMinimumStockAlert();
                    }
                    return true;
                })
                .map(this::mapToInventoryStockDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public InventoryStockDto updateInventoryStock(Long id, UpdateInventoryStockRequestDto request, String staffEmail) {
        User staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new RuntimeException("Staff user not found with email: " + staffEmail));

        BranchInventory inventory = branchInventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch inventory record not found with ID: " + id));

        if (staff.getRole() == UserRole.STAFF) {
            if (staff.getBranchId() != null && !staff.getBranchId().equals(inventory.getBranch().getId())) {
                throw new RuntimeException("Access Denied: Staff members can only update inventory for their branch.");
            }
        }

        if (request.getQuantity() != null) {
            inventory.setQuantity(request.getQuantity());
        }

        if (request.getMinimumStockAlert() != null) {
            inventory.setMinimumStockAlert(request.getMinimumStockAlert());
        }

        inventory = branchInventoryRepository.save(inventory);
        return mapToInventoryStockDto(inventory);
    }

    private RepairRequest findBookingByIdentifier(String identifier) {
        if (identifier.matches("\\d+")) {
            Long id = Long.parseLong(identifier);
            return repairRequestRepository.findById(id)
                    .orElseGet(() -> repairRequestRepository.findByBookingReference(identifier)
                            .orElseThrow(() -> new RuntimeException("Repair booking not found with ID or reference: " + identifier)));
        } else {
            return repairRequestRepository.findByBookingReference(identifier)
                    .orElseThrow(() -> new RuntimeException("Repair booking not found with reference: " + identifier));
        }
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
            return;
        }

        if (current == RepairStatus.QUALITY_CHECK && target == RepairStatus.REPAIRING) {
            return;
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
        if (booking.getTechnician() != null) {
            dto.setTechnicianId(booking.getTechnician().getId());
            dto.setTechnicianName(booking.getTechnician().getFullName());
        }
        dto.setTotalCost(booking.getTotalCost());
        dto.setCreatedAt(booking.getCreatedAt());
        return dto;
    }

    private TechnicianDto mapToTechnicianDto(Technician tech) {
        return new TechnicianDto(
                tech.getId(),
                tech.getBranch() != null ? tech.getBranch().getId() : null,
                tech.getBranch() != null ? tech.getBranch().getName() : null,
                tech.getFullName(),
                tech.getSpecialization(),
                tech.getIsAvailable(),
                tech.getActiveRepairsCount()
        );
    }

    private InventoryStockDto mapToInventoryStockDto(BranchInventory inv) {
        boolean isLow = inv.getQuantity() != null && inv.getMinimumStockAlert() != null && inv.getQuantity() <= inv.getMinimumStockAlert();
        SparePart part = inv.getSparePart();
        String categoryName = (part != null && part.getCompatibleCategory() != null) ? part.getCompatibleCategory().getName() : null;

        return new InventoryStockDto(
                inv.getId(),
                inv.getBranch() != null ? inv.getBranch().getId() : null,
                inv.getBranch() != null ? inv.getBranch().getName() : null,
                part != null ? part.getId() : null,
                part != null ? part.getName() : null,
                part != null ? part.getPartCode() : null,
                categoryName,
                inv.getQuantity(),
                inv.getMinimumStockAlert(),
                isLow,
                part != null ? part.getUnitCost() : null
        );
    }
}
