package com.nimius.smartbus.views.database;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface BookingDao {

    @Query("SELECT * FROM booking_detail")
    List<BookingIdModel> getAll();

    @Query("SELECT * FROM booking_detail where customer_lastname LIKE  :firstName AND customer_lastname LIKE :lastName")
    BookingIdModel findByName(String firstName, String lastName);

    @Query("SELECT COUNT(*) from booking_detail")
    int countUsers();

    @Insert
    void insertAll(BookingIdModel... users);

    @Delete
    void delete(BookingIdModel user);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insertBooking(BookingIdModel person);

    @Update
    public void updatePerson(BookingIdModel person);

    @Delete
    public void deleteBooking(BookingIdModel person);

    @Query("SELECT * FROM booking_detail")
    public LiveData<List<BookingIdModel>> getAllBookingsDao();


    @Query("SELECT * FROM booking_detail where booking_id LIKE  :id")
    public boolean isBookingIdExistsDao(String id);


    @Query("SELECT customer_lastname FROM booking_detail ORDER BY  uid DESC  LIMIT 1")
     String getLastNameDao();


    @Query("SELECT uid FROM booking_detail where booking_id LIKE  :id")
    public int rowPosition(String id);
//    @Query("SELECT * FROM person where mobile = :mobileIn")
//    public LiveData<BookingIdModel> getPersonByMobile(String mobileIn);
//    @Query("SELECT * FROM person where city In (:cityIn)")
//    public List<BookingIdModel> getPersonByCities(List<String> cityIn);
}
