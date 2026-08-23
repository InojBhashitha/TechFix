package com.techfix.app.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.techfix.app.data.local.entities.BookingEntity;

import java.util.List;

@Dao
public interface BookingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<BookingEntity> bookings);

    @Query("SELECT * FROM cached_bookings ORDER BY id DESC")
    List<BookingEntity> getAllBookings();

    @Query("SELECT * FROM cached_bookings WHERE bookingReference = :reference LIMIT 1")
    BookingEntity getBookingByReference(String reference);

    @Query("DELETE FROM cached_bookings")
    void clearAll();
}
