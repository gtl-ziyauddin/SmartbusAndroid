package com.nimius.smartbus.views.ui.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.nimius.smartbus.R;
import com.nimius.smartbus.views.service.model.bookingModel.BookingModel;
import com.nimius.smartbus.views.service.model.bookingTourModel.BookingTourModel;
import com.nimius.smartbus.views.service.repository.BookingIntentService;
import com.nimius.smartbus.views.service.repository.CarsIntentService;
import com.nimius.smartbus.views.service.repository.SmartToursIntentService;
import com.nimius.smartbus.views.service.repository.ToursIntentService;
import com.nimius.smartbus.views.service.retrofit.SmartBusRestClient;
import com.nimius.smartbus.views.utility.Utils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthenticateApiFragment extends CurrentLocationBaseFragment {
    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    public final static int WIDTH = 250;
    public final static int HEIGHT = 300;


    public void calledStartLocationUpdate() {
        startLocationUpdate();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    /**
     * INNIT VARIABLES
     */
    @Override
    public void initVariable() {
    }

    @Override
    public void loadData() {

    }


    public void onTourSuccess(BookingTourModel model) {

    }

    public void onSmartTourSuccess(BookingTourModel model) {

    }

    public void onAuthenticateSuccess(BookingModel model) {

    }

    public void onFailed(String message) {

    }

    /***
     * CAR INTENT SERVICE
     * @param url
     */
    public void callCarIntent(String url) {
        Log.e(TAG, "interval callCarIntent: ");
        Intent cbIntent = new Intent();
        cbIntent.setClass(mContext, CarsIntentService.class);
        cbIntent.putExtra(CarsIntentService.EXTRA_CARS_API, url);
        mContext.startService(cbIntent);
    }


    /**
     * SMART TOUR INTENT SERVICE
     *
     * @param url
     */
    public void callSmartToursIntent(String url) {
        Log.e(TAG, " interval callSmartToursIntent: ");
        Intent cbIntent = new Intent();
        cbIntent.setClass(mContext, SmartToursIntentService.class);
        cbIntent.putExtra(SmartToursIntentService.EXTRA_SMART_TOURS_API, url);
        mContext.startService(cbIntent);
    }

    /**
     * BOOKING INTENT SERVICE
     *
     * @param url
     */

    public void callBookingIntent(String url) {
        Log.e(TAG, " interval callBookingIntent: ");
        Intent cbIntent = new Intent();
        cbIntent.setClass(mContext, BookingIntentService.class);
        cbIntent.putExtra(BookingIntentService.EXTRA_BOOKING_API, url);
        mContext.startService(cbIntent);
    }


    /***
     * CALL TOUR INTENT SERVICE
     * @param url
     */
    public void callToursIntent(String url) {
        Log.e(TAG, " interval callToursIntent: ");
        Intent cbIntent = new Intent();
        cbIntent.setClass(mContext, ToursIntentService.class);
        cbIntent.putExtra(ToursIntentService.EXTRA_TOURS_API, url);
        mContext.startService(cbIntent);
    }


    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }

    public void authenticateRequest(String url) {
        if (Utils.isNetworkAvailable(mContext)) {
            Call<BookingModel> call = SmartBusRestClient.getInstance().getApiInterface()
                    .authenticateApi(url);
            call.enqueue(new Callback<BookingModel>() {
                @Override
                public void onResponse(@NonNull Call<BookingModel> call, @NonNull Response<BookingModel> response) {

                    if (!isAdded()) {
                        return;
                    }
                    Log.e(TAG, "onResponse: ");

                    if (response.isSuccessful()) {
                        BookingModel result = response.body();
                        onAuthenticateSuccess(result);
                    } else {
                        try {
                            if (!TextUtils.isEmpty(response.errorBody().string())) {
                                Log.e(TAG, "onResponse: error=" + response.message());
//                                showSnackBar(getActivity(), response.message());
                                onFailed(response.message());

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                }

                @Override
                public void onFailure(@NonNull Call<BookingModel> call, @NonNull Throwable t) {
                    if (!isAdded()) {
                        return;
                    }
                    Log.e(TAG, "onFailure: " + t.getMessage());
                    showErrorMessage(getString(R.string.msg_server_error_try_again));
                    onFailed(getString(R.string.msg_server_error_try_again));

                }
            });
        } else {
            Log.e(TAG, "no_internet");
            showSnackBar(mContext, getString(R.string.msg_no_internet));
            hideWaitProgressbar();

        }
    }


    /***
     * TOUR API
     * @param url
     */
    public void toursApiRequest(String url) {
        if (Utils.isNetworkAvailable(mContext)) {
            Call<BookingTourModel> call = SmartBusRestClient.getInstance().getApiInterface().getTourApi(url);
            call.enqueue(new Callback<BookingTourModel>() {
                @Override
                public void onResponse(@NonNull Call<BookingTourModel> call, @NonNull Response<BookingTourModel> response) {
                    Log.e(TAG, "onResponse: ");
                    if (!isAdded()) {
                        return;
                    }
                    hideWaitProgressbar();
                    if (response.isSuccessful()) {
                        BookingTourModel model = response.body();
                        if (model != null)
                            onTourSuccess(model);

                    } else {
                        try {
                            if (!TextUtils.isEmpty(response.errorBody().string())) {
                                Log.e(TAG, "onResponse: error=" + response.message());
                                showSnackBar(mContext, response.message());

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                }

                @Override
                public void onFailure(@NonNull Call<BookingTourModel> call, @NonNull Throwable t) {
                    if (!isAdded()) {
                        return;
                    }
                    Log.e(TAG, "onFailure: " + t.getMessage());
                    showErrorMessage(getString(R.string.msg_server_error_try_again));
                    hideWaitProgressbar();

                }
            });
        } else {
            Log.e(TAG, "no_internet");
            showSnackBar(mContext, getString(R.string.msg_no_internet));
            hideWaitProgressbar();

        }
    }


    public void smartToursApiRequest(String url) {
        if (Utils.isNetworkAvailable(mContext)) {
            Call<BookingTourModel> call = SmartBusRestClient.getInstance().getApiInterface().getSmartTourApi(url);
            call.enqueue(new Callback<BookingTourModel>() {
                @Override
                public void onResponse(@NonNull Call<BookingTourModel> call, @NonNull Response<BookingTourModel> response) {
                    Log.e(TAG, "onResponse: ");
                    if (!isAdded()) {
                        return;
                    }
                    hideWaitProgressbar();
                    if (response.isSuccessful()) {
                        BookingTourModel model = response.body();
                        if (model != null)
                            onSmartTourSuccess(model);

                    } else {
                        try {
                            if (!TextUtils.isEmpty(response.errorBody().string())) {
                                Log.e(TAG, "onResponse: error=" + response.message());
                                showSnackBar(mContext, response.message());

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                }

                @Override
                public void onFailure(@NonNull Call<BookingTourModel> call, @NonNull Throwable t) {
                    if (!isAdded()) {
                        return;
                    }
                    Log.e(TAG, "onFailure: " + t.getMessage());
                    showErrorMessage(getString(R.string.msg_server_error_try_again));
                    hideWaitProgressbar();

                }
            });
        } else {
            Log.e(TAG, "no_internet");
            showSnackBar(mContext, getString(R.string.msg_no_internet));
            hideWaitProgressbar();

        }
    }



   /* public void carsDetailRequest(String url) {
        if (Utils.isNetworkAvailable(mContext)) {
            Call<CarDetailModel> call = SmartBusRestClient.getInstance().getApiInterface().getCarsDetailApi(url);
            call.enqueue(new Callback<CarDetailModel>() {
                @Override
                public void onResponse(@NonNull Call<CarDetailModel> call, @NonNull Response<CarDetailModel> response) {
                    Log.e(TAG, "onResponse: ");
                    if (!isAdded()) {
                        return;
                    }
                    hideWaitProgressbar();

                    if (response.isSuccessful()) {
                        CarDetailModel model = response.body();
                        if (model != null)
                            onCarDetailSuccess(model);

                    } else {
                        try {
                            if (!TextUtils.isEmpty(response.errorBody().string())) {
                                Log.e(TAG, "onResponse: error=" + response.message());
                                showSnackBar(mContext, response.message());

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                }

                @Override
                public void onFailure(@NonNull Call<CarDetailModel> call, @NonNull Throwable t) {
                    if (!isAdded()) {
                        return;
                    }
                    Log.e(TAG, "onFailure: " + t.getMessage());
                    showErrorMessage(getString(R.string.msg_server_error_try_again));
                    hideWaitProgressbar();

                }
            });
        } else {
            Log.e(TAG, "no_internet");
            showSnackBar(mContext, getString(R.string.msg_no_internet));
            hideWaitProgressbar();

        }
    }


    public void carsDetailRequest(String url,Context mContext) {
        if (Utils.isNetworkAvailable(mContext)) {
            Call<CarDetailModel> call = SmartBusRestClient.getInstance().getApiInterface().getCarsDetailApi(url);
            call.enqueue(new Callback<CarDetailModel>() {
                @Override
                public void onResponse(@NonNull Call<CarDetailModel> call, @NonNull Response<CarDetailModel> response) {
                    Log.e(TAG, "onResponse: ");
                    if (!isAdded()) {
                        return;
                    }
                    hideWaitProgressbar();

                    if (response.isSuccessful()) {
                        CarDetailModel model = response.body();
                        if (model != null)
                            onCarDetailSuccess(model);

                    } else {
                        try {
                            if (!TextUtils.isEmpty(response.errorBody().string())) {
                                Log.e(TAG, "onResponse: error=" + response.message());
                                showSnackBar(mContext, response.message());

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                }

                @Override
                public void onFailure(@NonNull Call<CarDetailModel> call, @NonNull Throwable t) {
                    if (!isAdded()) {
                        return;
                    }
                    Log.e(TAG, "onFailure: " + t.getMessage());
                    showErrorMessage(getString(R.string.msg_server_error_try_again));
                    hideWaitProgressbar();

                }
            });
        } else {
            Log.e(TAG, "no_internet");
            showSnackBar(mContext, getString(R.string.msg_no_internet));
            hideWaitProgressbar();

        }
    }*/

//    public void onCarDetailSuccess(CarDetailModel model) {
//
//    }
//

}
