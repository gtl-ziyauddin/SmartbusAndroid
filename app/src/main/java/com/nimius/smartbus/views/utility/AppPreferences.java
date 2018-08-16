package com.nimius.smartbus.views.utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.nimius.smartbus.views.database.BookingIdModel;

import java.util.HashMap;

/**
 * Created by Ziyauddin.Ansari on 12/14/2017.
 */

public class AppPreferences {

    public final String PREF_BOOKING_LIST = "bookingList";
    private SharedPreferences sharedPreferences;
    public Context context;
    private SharedPreferences.Editor editor;


    public AppPreferences(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            editor = sharedPreferences.edit();
        }
    }


    /**
     * STORE REMEMBER ME
     */
    public void setCurrentPagePosition(int pager) {
        if (editor != null) {
            editor.putInt(Constants.PREF_PAGER_POSITION, pager);
            editor.commit();
        }
    }

    /**
     * GET REMEMBER  ME STATUS
     */

    public int getCurrentPagePosition() {
        if (sharedPreferences != null && sharedPreferences.contains(Constants.PREF_PAGER_POSITION)) {
            return sharedPreferences.getInt(Constants.PREF_PAGER_POSITION, -1);
        }
        return -1;
    }


    /***
     * STORE LOGIN RESPONSE INTO SHARE PREFERENCE
     * @param list
     */
    public void setBookingList(HashMap<String, BookingIdModel> list) {
        if (editor != null) {
            String json = new Gson().toJson(list);
            editor.putString(PREF_BOOKING_LIST, json);
            editor.commit();
        }
    }


    /***
     * STORE SESSION KEY WHICH WE GET FROM LOGIN RESPONSE FOR FUTURE USE
     * @param key
     */
    public void setDeviceKey(String key) {
        if (editor != null) {
            editor.putString(Constants.PREF_DEVICE_KEY, key);
            editor.commit();
        }
    }


    /**
     * GET SESSION KEY TO CALL OTHERS APIs.
     *
     * @return
     */
    public String getDeviceKey() {
        if (sharedPreferences != null && sharedPreferences.contains(Constants.PREF_DEVICE_KEY)) {
            return sharedPreferences.getString(Constants.PREF_DEVICE_KEY, "");
        }
        return null;
    }



    /**
     * FETCH LOGIN RESPONSE FROM SHARE PREFERENCE
     *
     * @return
     */
//    public HashMap<String,BookingIdModel> getBookingList() {
//        if (sharedPreferences != null && sharedPreferences.contains(PREF_BOOKING_LIST)) {
//            String json = sharedPreferences.getString(PREF_BOOKING_LIST, null);
//            return new Gson().fromJson(json, BookingIdModel.class);
//        }
//        return null;
//    }


}



