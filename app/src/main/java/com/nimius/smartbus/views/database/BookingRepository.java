package com.nimius.smartbus.views.database;


import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class BookingRepository {

    private final BookingDao personDAO;

    public BookingRepository(Context context) {
        personDAO = DatabaseCreator.getPersonDatabase(context).PersonDatabase();
    }

    public void addBooking(BookingIdModel p) {
        long rec = personDAO.insertBooking(p);
        Log.d("db insert ", "added " + rec);
    }

    public boolean isBookingIdExistsRepo(String id) {
        boolean isExist = personDAO.isBookingIdExistsDao(id);
        Log.d("db insert ", "added " + isExist);
        return  isExist;
    }


    public int rowPosition(String id) {
        int pos = personDAO.rowPosition(id);
        Log.d("db insert ", "added " + pos);
        return  pos;
    }


    public String getLastNameRepo() {
        String lastname = personDAO.getLastNameDao();
        Log.d("db insert ", "added " + lastname);
        return  lastname;
    }

    public void updatePerson(BookingIdModel p) {
        personDAO.updatePerson(p);
    }

    public void deleteBooking(BookingIdModel p) {
        personDAO.deleteBooking(p);
    }

    public LiveData<List< BookingIdModel>> getAllBookingsRepo() {
        return personDAO.getAllBookingsDao();
    }

//    public List<BookingIdModel> getPersonsByCity(List<String> cities) {
//        return personDAO.getPersonByCities(cities);
//    }
//
//    public LiveData<BookingIdModel> getPersonByMobile(String mobile) {
//        return personDAO.getPersonByMobile(mobile);
//    }
}
