package com.zenas.weatherapptest;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private int item_selected = 1;
    private String locationwoied;
    private TextView city;
    private double latitude;
    private double longitude;
    private LocationManager locationmanager;
    String cityName;
    private final static int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        city = findViewById(R.id.city);

        try {
            getCity();
            getWoeid();
            changeLocation();
        } catch(NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // This is Case 2 (Permission is now granted)
            getCity();
        } else {
            // This is Case 1 again as Permission is not granted by user
            //Now further we check if used denied permanently or not
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // case 4 User has denied permission but not permanently
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            } else {
                // case 5. Permission denied permanently.
                // You can open Permission setting's page from here now.
                Toast.makeText(this,"Please go to Setting and Turn on Location Service to get The Weather for Your Current Location",Toast.LENGTH_LONG).show();
            }

        }
    }
    @Override
    public void onLocationChanged(Location location) {
//       TextView city = findViewById(R.id.city);

//        city.setText(""+latitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void getCity(){
        if(ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            //Toast.makeText(this, "Permission to Location is Needed to Get the Weather for Your Current Location", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }else{
            locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationmanager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location != null){
                try{

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Geocoder geocoder = new Geocoder(this);
                    List<Address> addresses;

                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    cityName = addresses.get(0).getLocality().toString();
                    city.setText(cityName);


                }catch(NullPointerException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this.getApplicationContext(), "Current Coordinate not Found", Toast.LENGTH_SHORT);
            }
        }
    }
    public void onLocationButtonClick(View view) {
        registerForContextMenu(view);
        openContextMenu(view);
    }

    public String getWoeid(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.metaweather.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherApi api = retrofit.create(WeatherApi.class);
        Call<List<LocationDTO>> call = api.getCityWoeid("api/location/search/?query=" + cityName.toLowerCase().trim());
        call.enqueue(new Callback<List<LocationDTO>>() {
            @Override
            public void onResponse(Call<List<LocationDTO>> call, Response<List<LocationDTO>> response) {

                try {
                    List<LocationDTO> weather = response.body();
                    if (weather.isEmpty() != true) {

                        int[] cityWoeid = new int[weather.size()];
                        cityWoeid[0] = weather.get(0).getWoeid();
                        String currentWeather =  Arrays.toString(cityWoeid).replaceAll("\\[","").
                                replaceAll("\\]","").trim();
                        locationwoied = currentWeather;
                    } else {
                        Toast.makeText(getApplicationContext(), "Weather Information for this Location is Currently not Available", Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<LocationDTO>> call, Throwable t) {

            }
        });
        return locationwoied;
    }

    public void changeLocation() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);
        final String lastWeekDate = dtf.format(lastWeek);
        final String currentDate = dtf.format(now);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.metaweather.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApi api = retrofit.create(WeatherApi.class);
        Call<List<WeatherDTO>> call = api.getWeather("api/location/" + locationwoied + "/" + currentDate);
        Call<List<WeatherDTO>> caller = api.getWeather("api/location/" + locationwoied + "/" + lastWeekDate);
        call.enqueue(new Callback<List<WeatherDTO>>() {
            @Override
            public void onResponse(Call<List<WeatherDTO>> call, Response<List<WeatherDTO>> response) {
                try {
                    List<WeatherDTO> weather = response.body();
                    if (weather.isEmpty() != true) {

                        TextView weatherStatus = findViewById(R.id.weatherStatus);
                        TextView temperature = findViewById(R.id.temperature);
                        String[] specifiedWeather = new String[weather.size()];
                        Double[] specifiedTemp = new Double[weather.size()];
                        specifiedWeather[specifiedWeather.length -1] = weather.get(weather.size() - 1).getWeatherStateName();
                        specifiedTemp[specifiedTemp.length -1] = weather.get(weather.size() -1).getTheTemp();
                        String weatherName = String.join("", specifiedWeather)
                                .replaceAll("null", "");
                        String currentWeather = String.join("", Arrays.toString(specifiedTemp))
                                .replaceAll("null", "").replaceAll(",", "")
                                .replaceAll(" ", "").replaceAll("\\[", "")
                                .replaceAll("\\]", "");

                        String approxCurrentWeather = currentWeather.substring(0, Math.min(currentWeather.length(), 2));
                        temperature.setText(approxCurrentWeather + "°");
                        weatherStatus.setText(weatherName);
                    } else {
                        Toast.makeText(getApplicationContext(), "No Record Found", Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<WeatherDTO>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        caller.enqueue(new Callback<List<WeatherDTO>>() {
            @Override
            public void onResponse(Call<List<WeatherDTO>> call, Response<List<WeatherDTO>> response) {
                try {
                    List<WeatherDTO> weather = response.body();
                    TextView lastWeek = findViewById(R.id.lastWeek);
                    Double[] specifiedTemp = new Double[weather.size()];
                    specifiedTemp[0] = weather.get(0).getTheTemp();
                    String currentWeather = String.join("", Arrays.toString(specifiedTemp))
                            .replaceAll("null", "").replaceAll(",", "")
                            .replaceAll(" ", "").replaceAll("\\[", "")
                            .replaceAll("\\]", "");
                    String approxCurrentWeather = currentWeather.substring(0, Math.min(currentWeather.length(), 2));
                    lastWeek.setText("Last Week: " + approxCurrentWeather + "°");
                } catch (NullPointerException e) {
                    Toast.makeText(getApplicationContext(), "Weather Not Available for Your Current Location", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<WeatherDTO>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_method_menu, menu);
        MenuItem currentLocation = menu.findItem(R.id.currentLocation);
        MenuItem locationBerlin = menu.findItem(R.id.locationBerlin);
        MenuItem locationStuttgart = menu.findItem(R.id.locationStuttgart);
        MenuItem locationFrankfurt = menu.findItem(R.id.locationFrankfurt);
        if (item_selected == 1) {
            currentLocation.setChecked(true);
        } else if (item_selected == 2) {
            locationBerlin.setChecked(true);
        } else if (item_selected == 3) {
            locationStuttgart.setChecked(true);
        } else if (item_selected == 4) {
            locationFrankfurt.setChecked(true);
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        TextView city = findViewById(R.id.city);
        TextView lastWeek = findViewById(R.id.lastWeek);
        //locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //@SuppressLint("MissingPermission") Location location = locationmanager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        switch (item.getItemId()) {
            case R.id.currentLocation:
                item_selected = 1;
                //location = "651092";
                //onLocationChanged(location);
                try {
                    getCity();
                    getWoeid();
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch(NullPointerException e){
                    e.printStackTrace();
                }
                changeLocation();
                lastWeek.setVisibility(View.VISIBLE);
                return true;

            case R.id.locationBerlin:
                locationwoied = "638242";
                lastWeek.setVisibility(View.INVISIBLE);
                changeLocation();
                city.setText("Berlin");
                item_selected = 2;
                return true;

            case R.id.locationStuttgart:
                locationwoied = "698064";
                lastWeek.setVisibility(View.INVISIBLE);
                changeLocation();
                city.setText("Stuttgart");
                item_selected = 3;
                return true;

            case R.id.locationFrankfurt:
                locationwoied = "650272";
                lastWeek.setVisibility(View.INVISIBLE);
                changeLocation();
                city.setText("Frankfurt");
                item_selected = 4;
                return true;
        }
        return super.onContextItemSelected(item);
    }


}
