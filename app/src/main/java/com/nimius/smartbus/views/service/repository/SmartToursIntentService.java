package com.nimius.smartbus.views.service.repository;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.nimius.smartbus.views.service.model.bookingTourModel.BookingTourModel;
import com.nimius.smartbus.views.service.retrofit.SmartBusRestClient;
import com.nimius.smartbus.views.utility.Constants;
import com.nimius.smartbus.views.utility.Utils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SmartToursIntentService extends IntentService {
    private static final String TAG = "SmartToursIntentService";

    public static String EXTRA_CODE = "responseCode";
    public static final String EXTRA_SMART_TOURS_API = "smartTourApi";

    public static int CODE_SUCCESS = 1;
    public static int CODE_ERROR = 2;
    public static int CODE_FAILURE = 3;
    public static int CODE_INTERNET = 4;


    public SmartToursIntentService() {
        super("Cashback IntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String authenticateApi = intent.getStringExtra(EXTRA_SMART_TOURS_API);
        smartTourDetailRequest(authenticateApi, this);

    }


    private void sendSmartToursDetailToClient(BookingTourModel model, int code) {
        Intent intent = new Intent();
        intent.setAction(Constants.SMART_TOURS_DETAILS_BROADCAST_FILTER);
        intent.putExtra(EXTRA_CODE, code);
        intent.putExtra(EXTRA_SMART_TOURS_API, model);
        sendBroadcast(intent);
    }


    public void smartTourDetailRequest(String url, Context mContext) {
        if (Utils.isNetworkAvailable(mContext)) {
            Call<BookingTourModel> call = SmartBusRestClient.getInstance().getApiInterface().getTourApi(url);
            call.enqueue(new Callback<BookingTourModel>() {
                @Override
                public void onResponse(@NonNull Call<BookingTourModel> call, @NonNull Response<BookingTourModel> response) {
                    Log.e(TAG, "onResponse: ");

                    if (response.isSuccessful()) {
                        BookingTourModel model = response.body();
                        if (model != null)
                            sendSmartToursDetailToClient(model, CODE_SUCCESS);

                    } else {
                        try {
                            if (!TextUtils.isEmpty(response.errorBody().string())) {
                                Log.e(TAG, "onResponse: error=" + response.message());
//                                showSnackBar(mContext, response.message());
                                BookingTourModel carsModel = new BookingTourModel();
                                carsModel.errorMessage = response.message();
                                sendSmartToursDetailToClient(carsModel, CODE_ERROR);

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                }

                @Override
                public void onFailure(@NonNull Call<BookingTourModel> call, @NonNull Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());
//                    showErrorMessage(getString(R.string.msg_server_error_try_again));
                    sendSmartToursDetailToClient(null, CODE_FAILURE);

                }
            });
        } else {
            Log.e(TAG, "no_internet");
//            showSnackBar(mContext, getString(R.string.msg_no_internet));
            sendSmartToursDetailToClient(null, CODE_INTERNET);

        }
    }
}