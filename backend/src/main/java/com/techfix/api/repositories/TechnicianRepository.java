package com.techfix.api.repositories;

import com.techfix.api.entities.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician, Long> {
    List<Technician> findByBranchId(Long branchId);
    List<Technician> findByBranchIdAndIsAvailableTrue(Long branchId);
    long countByBranchIdAndIsAvailableTrue(Long branchId);
    long countByIsAvailableTrue();
}
