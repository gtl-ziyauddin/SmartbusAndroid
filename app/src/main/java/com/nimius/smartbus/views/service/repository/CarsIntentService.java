package com.nimius.smartbus.views.service.repository;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.nimius.smartbus.views.service.model.carDetailModel.CarDetailModel;
import com.nimius.smartbus.views.service.retrofit.SmartBusRestClient;
import com.nimius.smartbus.views.utility.Constants;
import com.nimius.smartbus.views.utility.Utils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarsIntentService extends IntentService {
    private static final String TAG = "CarsIntentService";

    public static String EXTRA_CODE = "responseCode";
    public static final String EXTRA_CARS_API = "carApi";

    public static int CODE_SUCCESS = 1;
    public static int CODE_ERROR = 2;
    public static int CODE_FAILURE = 3;
    public static int CODE_INTERNET = 4;



    public CarsIntentService() {
        super("Cashback IntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String authenticateApi = intent.getStringExtra(EXTRA_CARS_API);
        carsDetailRequest(authenticateApi, this);

    }


    private void sendCarDetailToClient(CarDetailModel model, int code) {
        Intent intent = new Intent();
        intent.setAction(Constants.CAR_DETAILS_BROADCAST_FILTER);
        intent.putExtra(EXTRA_CODE, code);
        intent.putExtra(EXTRA_CARS_API, model);
        sendBroadcast(intent);
    }


    public void carsDetailRequest(String url, Context mContext) {
        if (Utils.isNetworkAvailable(mContext)) {
            Call<CarDetailModel> call = SmartBusRestClient.getInstance().getApiInterface().getCarsDetailApi(url);
            call.enqueue(new Callback<CarDetailModel>() {
                @Override
                public void onResponse(@NonNull Call<CarDetailModel> call, @NonNull Response<CarDetailModel> response) {
                    Log.e(TAG, "onResponse: ");

                    if (response.isSuccessful()) {
                        CarDetailModel model = response.body();
                        if (model != null)
                            sendCarDetailToClient(model, CODE_SUCCESS);

                    } else {
                        try {
                            if (!TextUtils.isEmpty(response.errorBody().string())) {
                                Log.e(TAG, "onResponse: error=" + response.message());
//                                showSnackBar(mContext, response.message());
                                CarDetailModel carsModel = new CarDetailModel();
                                carsModel.errorMessage = response.message();
                                sendCarDetailToClient(carsModel, CODE_ERROR);

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                }

                @Override
                public void onFailure(@NonNull Call<CarDetailModel> call, @NonNull Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());
//                    showErrorMessage(getString(R.string.msg_server_error_try_again));
                    sendCarDetailToClient(null, CODE_FAILURE);

                }
            });
        } else {
            Log.e(TAG, "no_internet");
//            showSnackBar(mContext, getString(R.string.msg_no_internet));
            sendCarDetailToClient(null, CODE_INTERNET);

        }
    }
}