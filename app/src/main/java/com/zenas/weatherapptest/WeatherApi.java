package com.zenas.weatherapptest;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface WeatherApi {

    //String BASE_URL = "https://www.metaweather.com/";


    @GET()
    Call<List<WeatherDTO>> getWeather(@Url String url);
    @GET()
    Call<List<LocationDTO>> getCityWoeid(@Url String url);


}
