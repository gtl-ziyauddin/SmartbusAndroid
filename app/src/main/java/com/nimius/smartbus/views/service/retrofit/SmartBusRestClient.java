package com.nimius.smartbus.views.service.retrofit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.nimius.smartbus.views.service.repository.ApiInterface;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.support.constraint.Constraints.TAG;


public class SmartBusRestClient {

    private String username = "einar@airportdirect.is";
    private String password = "MonsuPabbi1";
    private String authorization_value = "Basic ZWluYXJAYWlycG9ydGRpcmVjdC5pczpNb25zdVBhYmJpMQ==";

    public static int DEFAULT_TIMEOUT = 0; // 60 seconds
    private static Context mContext;

    private ApiInterface apiInterface;
    private static SmartBusRestClient clientInstance;
    private static OkHttpClient.Builder httpClient = null;

    public static SmartBusRestClient getInstance(Context context) {
        mContext = context;
        if (clientInstance == null) {
            clientInstance = new SmartBusRestClient();
        }
        return clientInstance;
    }


    public static SmartBusRestClient getInstance() {
        if (clientInstance == null) {
            clientInstance = new SmartBusRestClient();
        }
        return clientInstance;
    }


    /***
     * THIS IS FOR PLACE APIs
     */
    private SmartBusRestClient() {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(ApiInterface.BASE_URL_BOOKING_APP);
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
        httpClient.addInterceptor(new BasicAuthInterceptor(username, password) {
            private String credentials;

            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                this.credentials = Credentials.basic(username, password);
                Request request = chain.request();
                Request authenticatedRequest = request.newBuilder()
                        .header("Authorization", credentials).build();
                return chain.proceed(authenticatedRequest);
            }
        });
//        httpClient.addNetworkInterceptor(new Interceptor() {
//
//            @Override
//            public Response intercept(@NonNull Chain chain) throws IOException {
//                Request original = chain.request();
//                Request.Builder request = original.newBuilder();
//
////                request.addHeader(ApiInterface.authorization, authorization_value);
//
//                request.method(original.method(), original.body()).build();
//                Response response = chain.proceed(request.build());
//                Log.e(TAG, "intercept: proceed=" + response.code());
//
//
//                return response;
//            }
//        });
        return httpClient.build();
    }


    /***
     * FOR AUTHENTICATION.
     */
    public class BasicAuthInterceptor implements Interceptor {

        private String credentials;

        public BasicAuthInterceptor(String user, String password) {
            this.credentials = Credentials.basic(user, password);
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request authenticatedRequest = request.newBuilder()
                    .header("Authorization", credentials).build();
            return chain.proceed(authenticatedRequest);
        }

    }

}