package com.nimius.smartbus.views.utility;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.nimius.smartbus.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Created by Ziyauddin.Ansari on 10/17/2017.
 */

public class Utils {
    public static final String CURRENT_DATE_FORMAT_SECOND = "yyyy-MM-dd kk:mm:ss";

//    2018-05-22T10:15:00

    public static final String CURRENT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String BOOKING_INFO_OUTPUT_DATE_FORMAT = "EEEE";
    public static final String PICKUP_DETAIL_OUTPUT_DATE_12HOURS_FORMAT = "yyyy-MM-dd'T'hh:mm aa";
    public static final String PICKUP_DETAIL_OUTPUT_DATE_24HOURS_FORMAT = "yyyy-MM-dd'T'kk:mm";
    private static final String TAG = "-Utils-";

    private static final int PROFILE_BACKGROUND_IMAGE_SIZE = 4000; //kb
    private static final int PROFILE_IMAGE_SIZE = 1024; //kb


    /***
     * FUNCTION TO HIDE KEYBOARD
     * @param activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }


    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    public static void showKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    /***
     * CHANGE NOTIFICATION STATUS BAR COLOR
     * apply only to splash, login, set password and protection screen
     * @param activity
     */

    public static void statusBarBlue(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(activity, R.color.colorPrimary));
        }
    }

    public static void statusBarWhite(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
        }
    }


    /**
     * CHECK INTERNET CONNECTIVITY
     *
     * @param context
     * @return
     */

    public static boolean isNetworkAvailable(Context context) {
        int[] networkTypes = {ConnectivityManager.TYPE_MOBILE,
                ConnectivityManager.TYPE_WIFI};
        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            for (int networkType : networkTypes) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null &&
                        activeNetworkInfo.getType() == networkType)
                    return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * PARSE ONLY DATE
     *
     * @param existingDate
     * @return
     */
    public static String parsePickUpDateTime(String existingDate, Context mContext) {
        final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs
        if (TextUtils.isEmpty(existingDate)) {
            return "";
        }
        Log.e(TAG, "parseDateTime: date=" + existingDate);
        String parsedDate = "";

        try {
            SimpleDateFormat df = new SimpleDateFormat(CURRENT_DATE_FORMAT, Locale.getDefault());
            SimpleDateFormat outputFormat;
            if (is24HourTimeFormat(mContext)) {
                outputFormat = new SimpleDateFormat(PICKUP_DETAIL_OUTPUT_DATE_24HOURS_FORMAT, Locale.getDefault());
            } else {
                outputFormat = new SimpleDateFormat(PICKUP_DETAIL_OUTPUT_DATE_12HOURS_FORMAT, Locale.getDefault());
            }


            Date date = df.parse(existingDate);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            long t = cal.getTimeInMillis();
//            Date afterLesTenMins = new Date(t - (30 * ONE_MINUTE_IN_MILLIS));
            Date strDate = new Date(t);
            parsedDate = outputFormat.format(strDate);
            String[] arr = parsedDate.split("T");
            parsedDate = arr[1];

        } catch (Exception e) {
            e.printStackTrace();
        }


        return parsedDate;

    }


    public static Date GetUTCdatetimeAsDate() {

        return StringDateToDate(GetUTCdatetimeAsString());
    }


    public static String GetUTCdatetimeAsString() {
        final SimpleDateFormat sdf = new SimpleDateFormat(CURRENT_DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Log.e(TAG, "UTC datetimeAsString:   " + sdf.format(new Date()));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }

    public static Date StringDateToDate(String StrDate) {
        Date dateToReturn = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(CURRENT_DATE_FORMAT);
        try {
            dateToReturn = (Date) dateFormat.parse(StrDate);
            Log.e(TAG, "UTC datetimeAsString: dateToReturn  " + dateToReturn);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateToReturn;
    }


    /***
     * START TOUR TIMER BEFORE 30 MINUTES OF DEPARTURE TIME
     * @param existingDate
     * @return
     */

    public static boolean startTourTimerDateTime(String existingDate) {
        long fixedValue = 0;
        long value;
        final int MILLI_TO_MINUTES = 1000 * 60;

        if (TextUtils.isEmpty(existingDate)) {
            return false;
        }
        Log.e(TAG, "parseDateTime: date=" + existingDate);

        try {
            SimpleDateFormat df = new SimpleDateFormat(CURRENT_DATE_FORMAT);
//            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date dateExisting = df.parse(existingDate);
            Date dateCurrent = GetUTCdatetimeAsDate();
            value = (dateExisting.getTime() - dateCurrent.getTime()) / MILLI_TO_MINUTES;
            Log.e(TAG, "isBookingDepartedDateTime: " + value);

            if (value <= fixedValue) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }


  /*  public static boolean startTourTimerDateTime(String existingDate) {
        long fixedValue = 30;
        long value;
        final int MILLI_TO_MINUTES = 1000 * 60;

        if (TextUtils.isEmpty(existingDate)) {
            return false;
        }
        Log.e(TAG, "parseDateTime: date=" + existingDate);

        try {
            SimpleDateFormat df = new SimpleDateFormat(CURRENT_DATE_FORMAT);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = df.parse(existingDate);
            Calendar existingCal = Calendar.getInstance();
            existingCal.setTime(date);
            Calendar currentCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//            Calendar currentCal = Calendar.getInstance();
            Log.e(TAG, "isBookingDepartedDateTime: " + (existingCal.getTimeInMillis() - currentCal.getTimeInMillis()) / MILLI_TO_MINUTES);
            value = (existingCal.getTimeInMillis() - currentCal.getTimeInMillis()) / MILLI_TO_MINUTES;

            if (value <= fixedValue) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }*/


    /**
     * PARSE ONLY DATE
     *
     * @param existingDate
     * @return
     */
    public static String parseBookingDateTime(String existingDate, Context mContext) {
        if (TextUtils.isEmpty(existingDate)) {
            return "";
        }
        Log.e(TAG, "parseDateTime: date=" + existingDate);
        String parsedDate = "";

        try {
            SimpleDateFormat df = new SimpleDateFormat(CURRENT_DATE_FORMAT, Locale.getDefault());
            Date date = df.parse(existingDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            String dayNumberSuffix = getDayNumberSuffix(cal.get(Calendar.DAY_OF_MONTH));
            SimpleDateFormat outputFormat;
            if (is24HourTimeFormat(mContext)) {
                outputFormat = new SimpleDateFormat("MMMM d'" + dayNumberSuffix + " " + mContext.getString(R.string.str_at) + " ' kk:mm ", Locale.getDefault());
            } else {
                outputFormat = new SimpleDateFormat("MMMM d'" + dayNumberSuffix + " " + mContext.getString(R.string.str_at) + " ' hh:mm aa", Locale.getDefault());
            }


            parsedDate = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return parsedDate;

    }

    public static String getDayNumberSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }

    }


    public static boolean is24HourTimeFormat(Context mContext) {
        int time_format = 0;
        try {
            time_format = Settings.System.getInt(mContext.getContentResolver(), Settings.System.TIME_12_24);
            Log.i(TAG, "Time format: " + time_format);
            if (time_format == 24) {
                return true;
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * CALCULATE TIME DIFFERENCE BETWEEN BOOKING TIME AND CURRENT TIME
     * IF DIFFERENCE IS MORE THAN 4 HOURS THEN (return true) SHOW BUS AS DEPARTED.
     *
     * @param strDate
     * @return
     */

    public static boolean isBookingDepartedDateTime(String strDate) {
        long value;
        final long fixedValue = 4;
        final int MILLI_TO_HOUR = 1000 * 60 * 60;
        if (TextUtils.isEmpty(strDate)) {
            return false;
        }
        Log.e(TAG, "parseDateTime: date=" + strDate);

        try {
            SimpleDateFormat df = new SimpleDateFormat(CURRENT_DATE_FORMAT);
//            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date dateExisting = df.parse(strDate);
            Date dateCurrent = GetUTCdatetimeAsDate();
            //milliseconds
            value = (dateCurrent.getTime() - dateExisting.getTime()) / MILLI_TO_HOUR;
            Log.e(TAG, "isBookingDepartedDateTime: " + value);
            if (value >= fixedValue) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

/*    public static boolean isBookingDepartedDateTime(String strDate) {
        long value;
        final long fixedValue = 4;
        final int MILLI_TO_HOUR = 1000 * 60 * 60;
        if (TextUtils.isEmpty(strDate)) {
            return false;
        }
        Log.e(TAG, "parseDateTime: date=" + strDate);

        try {
            SimpleDateFormat df = new SimpleDateFormat(CURRENT_DATE_FORMAT);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = df.parse(strDate);
            Calendar existingCal = Calendar.getInstance();
            existingCal.setTime(date);
//            Calendar currentCal = Calendar.getInstance();
            Calendar currentCal = Calendar.getInstance();
            currentCal.setTime(GetUTCdatetimeAsDate());

            Log.e(TAG, "isBookingDepartedDateTime: " + (currentCal.getTimeInMillis() - existingCal.getTimeInMillis()) / MILLI_TO_HOUR);
            value = (currentCal.getTimeInMillis() - existingCal.getTimeInMillis()) / MILLI_TO_HOUR;

            if (value >= fixedValue) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }*/

    public static void buildAlertMessageNoGps(Context mContext) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        mContext.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}