package com.techfix.app.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.techfix.app.data.local.entities.ServiceEntity;

import java.util.List;

@Dao
public interface ServiceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ServiceEntity> services);

    @Query("SELECT * FROM cached_services ORDER BY id ASC")
    List<ServiceEntity> getAllServices();

    @Query("SELECT * FROM cached_services WHERE categoryId = :categoryId ORDER BY id ASC")
    List<ServiceEntity> getServicesByCategory(Long categoryId);

    @Query("DELETE FROM cached_services")
    void clearAll();
}
