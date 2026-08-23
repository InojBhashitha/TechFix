package com.techfix.app.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.techfix.app.data.local.entities.CategoryEntity;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CategoryEntity> categories);

    @Query("SELECT * FROM cached_categories ORDER BY id ASC")
    List<CategoryEntity> getAllCategories();

    @Query("DELETE FROM cached_categories")
    void clearAll();
}
