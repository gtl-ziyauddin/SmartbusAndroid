package com.nimius.smartbus.views.service.retrofit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.nimius.smartbus.views.service.repository.ApiInterface;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.support.constraint.Constraints.TAG;


public class PlaceApiClient {

    public static int DEFAULT_TIMEOUT = 0; // 60 seconds
    private static Context mContext;

    private ApiInterface apiInterface;
    private static PlaceApiClient clientInstance;
    private static OkHttpClient.Builder httpClient = null;
    private static boolean isPrismic;


    public static PlaceApiClient getInstance() {
        if (clientInstance == null) {
            clientInstance = new PlaceApiClient();
        }
        return clientInstance;
    }


    /***
     * THIS IS FOR PLACE APIs
     */
    private PlaceApiClient() {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(ApiInterface.BASE_URL_PLACE_API);
        builder.addConverterFactory(GsonConverterFactory.create());
        builder.client(getClient());
        Retrofit retrofit = builder.build();

        apiInterface = retrofit.create(ApiInterface.class);
    }




    public ApiInterface getApiInterface() {
        return apiInterface;
    }

    public OkHttpClient getClient() {

        // here I checked if okhttpclient is null, create new. otherwise, use existing.
        if (httpClient == null) {
            httpClient = new OkHttpClient.Builder();
        }
        httpClient.readTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        httpClient.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        httpClient.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        // add logging as last interceptor
        httpClient.interceptors().add(logging);  // <-- this is the important line!
        httpClient.addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder request = original.newBuilder();
                request.method(original.method(), original.body()).build();
                Response response = chain.proceed(request.build());
                Log.e(TAG, "intercept: proceed=" + response.code());


                return response;
            }
        });
        return httpClient.build();
    }
}