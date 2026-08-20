package com.techfix.api.repositories;

import com.techfix.api.entities.RepairService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairServiceRepository extends JpaRepository<RepairService, Long> {
    List<RepairService> findByCategoryIdAndIsActiveTrue(Long categoryId);
    List<RepairService> findByIsActiveTrue();
}
