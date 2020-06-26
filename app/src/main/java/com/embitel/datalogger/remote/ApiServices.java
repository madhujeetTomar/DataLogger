
package com.embitel.datalogger.remote;

import com.embitel.datalogger.model.WeatherResponse;


import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiServices {


 @GET("weather")
 Single<WeatherResponse> getWeatherUpdate(@Query("APPID") String appId, @Query("q") String location, @Query("units") String unit);

}


