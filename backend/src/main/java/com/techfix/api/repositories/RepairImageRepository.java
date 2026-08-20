package com.techfix.api.repositories;

import com.techfix.api.entities.RepairImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairImageRepository extends JpaRepository<RepairImage, Long> {
    List<RepairImage> findByRepairRequestId(Long repairRequestId);
}
