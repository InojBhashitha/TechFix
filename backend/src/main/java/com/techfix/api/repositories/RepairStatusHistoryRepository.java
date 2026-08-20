package com.techfix.api.repositories;

import com.techfix.api.entities.RepairStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairStatusHistoryRepository extends JpaRepository<RepairStatusHistory, Long> {
    List<RepairStatusHistory> findByRepairRequestIdOrderByTimestampAsc(Long repairRequestId);
}
