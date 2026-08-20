package com.techfix.api.repositories;

import com.techfix.api.entities.RepairRequest;
import com.techfix.api.enums.RepairStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairRequestRepository extends JpaRepository<RepairRequest, Long> {
    Optional<RepairRequest> findByBookingReference(String bookingReference);
    List<RepairRequest> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    List<RepairRequest> findByBranchIdOrderByCreatedAtDesc(Long branchId);
    List<RepairRequest> findByBranchIdAndCurrentStatus(Long branchId, RepairStatus status);
    List<RepairRequest> findByTechnicianId(Long technicianId);
    long countByBranchIdAndCurrentStatus(Long branchId, RepairStatus status);
}
