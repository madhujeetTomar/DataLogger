package com.embitel.datalogger.remote;


import android.util.Log;

import com.embitel.datalogger.bleutils.SharedPreferencesManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClass {


    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private static Retrofit retrofit;


    private static Retrofit getInstance() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


        final OkHttpClient okHttpClient = new OkHttpClient.Builder()

                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(interceptor)
                .addInterceptor(chain -> {
                    Request request = chain.request();

                    // try the request
                    Response response = chain.proceed(request);

                    int tryCount = 0;
                    while (!response.isSuccessful() && tryCount < SharedPreferencesManager.getAPICALLRetryValue()) {

                        Log.d("intercept", "Request is not successful - " + tryCount);

                        tryCount++;

                        // retry the request
                        response = chain.proceed(request);
                    }
                    Log.d("Request", "getInstance: "+tryCount);
                 //   Log.d("Response", "getInstance: "+response.body());

                    // otherwise just pass the original response on
                    return response;

                })
                .build();
        Gson gson = new GsonBuilder().setLenient().create();
        return new Retrofit.Builder()
                .baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();


    }

    public static ApiServices getApiServices() {

        if (retrofit == null) {
            return getInstance().create(ApiServices.class);

        } else {
            return retrofit.create(ApiServices.class);

        }

    }
}