package com.zenas.weatherapptest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherDTO {
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("weather_state_name")
    @Expose
    private String weatherStateName;
    @SerializedName("weather_state_abbr")
    @Expose
    private String weatherStateAbbr;
    @SerializedName("the_temp")
    @Expose
    private Double theTemp;
    @SerializedName("created")
    @Expose
    private String created;



    public Long getId() {
        return id;
    }
    public String getWeatherStateName() {
        return weatherStateName;
    }
    public String getWeatherStateAbbr() {
        return weatherStateAbbr;
    }

    public Double getTheTemp() {
        return theTemp;
    }
    public String getCreated() {
        return created;
    }

}
