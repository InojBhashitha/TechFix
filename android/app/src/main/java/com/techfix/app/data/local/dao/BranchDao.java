package com.techfix.app.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.techfix.app.data.local.entities.BranchEntity;

import java.util.List;

@Dao
public interface BranchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<BranchEntity> branches);

    @Query("SELECT * FROM cached_branches ORDER BY id ASC")
    List<BranchEntity> getAllBranches();

    @Query("DELETE FROM cached_branches")
    void clearAll();
}
