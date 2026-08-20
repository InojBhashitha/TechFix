package com.techfix.api.repositories;

import com.techfix.api.entities.BranchInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchInventoryRepository extends JpaRepository<BranchInventory, Long> {
    List<BranchInventory> findByBranchId(Long branchId);
    Optional<BranchInventory> findByBranchIdAndSparePartId(Long branchId, Long sparePartId);
}
