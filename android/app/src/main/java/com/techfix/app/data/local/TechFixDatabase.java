package com.techfix.app.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.techfix.app.data.local.dao.BookingDao;
import com.techfix.app.data.local.dao.BranchDao;
import com.techfix.app.data.local.dao.CategoryDao;
import com.techfix.app.data.local.dao.ServiceDao;
import com.techfix.app.data.local.entities.BookingEntity;
import com.techfix.app.data.local.entities.BranchEntity;
import com.techfix.app.data.local.entities.CategoryEntity;
import com.techfix.app.data.local.entities.ServiceEntity;

@Database(entities = {CategoryEntity.class, ServiceEntity.class, BookingEntity.class, BranchEntity.class}, version = 1, exportSchema = false)
public abstract class TechFixDatabase extends RoomDatabase {

    private static final String DB_NAME = "techfix_local.db";
    private static volatile TechFixDatabase instance;

    public abstract CategoryDao categoryDao();
    public abstract ServiceDao serviceDao();
    public abstract BookingDao bookingDao();
    public abstract BranchDao branchDao();

    public static TechFixDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (TechFixDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            TechFixDatabase.class,
                            DB_NAME
                    ).allowMainThreadQueries().build(); // allowMainThreadQueries for seamless offline fallback
                }
            }
        }
        return instance;
    }
}
