package com.nimius.smartbus.views.database;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BookingViewModel extends AndroidViewModel {

    private BookingRepository personRepository = new BookingRepository(this.getApplication());
    private final Executor executor = Executors.newFixedThreadPool(2);

    private final MediatorLiveData<List<BookingIdModel>> personsByCity = new MediatorLiveData<>();

    private final MediatorLiveData<String> mobileNo = new MediatorLiveData<>();

    //transformation applied so that observer to this LiveData can be added only once
//    private final LiveData<BookingIdModel> personsByMobile = Transformations.switchMap(mobileNo, (mobile) -> {
//        return personRepository.getPersonByMobile(mobile);
//    });

    public LiveData<List<BookingIdModel>> getPersonsByCityLive() {
        return personsByCity;
    }

    public BookingViewModel(@NonNull Application application) {
        super(application);
    }

    //Room DAO call needs to be run on background thread
    //This example uses Executor
    public void addBooking(BookingIdModel p) {
        executor.execute(() -> personRepository.addBooking(p));
    }

    public void updatePerson(BookingIdModel p) {
        executor.execute(() -> personRepository.updatePerson(p));
    }

    public void deleteBooking(BookingIdModel p) {
        executor.execute(() -> personRepository.deleteBooking(p));
    }

    public boolean isBookingIdExistsVM(String bookingId) {
        return personRepository.isBookingIdExistsRepo(bookingId);
    }

    public String getLastNameVM() {
        return personRepository.getLastNameRepo();
    }


    public int rowPosition(String bookingId) {
        return personRepository.rowPosition(bookingId);
    }

    //Since room DAO returns LiveData, it runs on background thread.
    public LiveData<List<BookingIdModel>> getAllBookingVM() {
        return personRepository.getAllBookingsRepo();
    }

    //Room DAO call needs to be run on background thread
    //This example uses AsyncTask
//    public void getPersonsByCity(List<String> cities){
//        new AsyncTask<Void, Void, List<BookingIdModel>>() {
//            @Override
//            protected List<BookingIdModel> doInBackground(Void... params) {
//                return personRepository.getPersonsByCity(cities);
//            }
//            @Override
//            protected void onPostExecute(List<BookingIdModel> personLst) {
//                Log.d("", "persons by city "+personLst.size());
//                personsByCity.setValue(personLst);
//            }
//        }.execute();
//
//    }
    //sets the mobile number to LiveData object,
    //transformation using switchMap intern calls room DAO method which return LiveData
    public void setMobile(String mobile) {
        mobileNo.setValue(mobile);
    }
//    public LiveData<BookingIdModel> getPersonsByMobile(){
//        return personsByMobile;
//    }
}
