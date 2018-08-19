package com.zenas.weatherapptest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class LocationDTO {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("woeid")
    @Expose
    private Integer woeid;

    public String getTitle() {
        return title;
    }

    public Integer getWoeid() {
        return woeid;
    }

}
