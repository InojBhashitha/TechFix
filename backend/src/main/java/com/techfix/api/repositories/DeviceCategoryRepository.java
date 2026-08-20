package com.techfix.api.repositories;

import com.techfix.api.entities.DeviceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceCategoryRepository extends JpaRepository<DeviceCategory, Long> {
}
