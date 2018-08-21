package com.zenas.weatherapptest;

import org.junit.Before;
import org.junit.Test;

public class MainActivityTest {

    private MainActivity mainActivity;

    @Before
    public void setup(){
        mainActivity = new MainActivity();
    }
    @Test(expected = RuntimeException.class)
    public void getCity() {
        mainActivity.getCity();
    }

    @Test(expected = NullPointerException.class)
    public void getWoeid(){
        mainActivity.getWoeid();
    }
}