package com.app.air.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.app.air.coolweather.db.CoolWeatherDB;
import com.app.air.coolweather.model.City;
import com.app.air.coolweather.model.County;
import com.app.air.coolweather.model.Province;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * parsing and handle the data from server
 * Created by air on 14-10-10.
 */
public class Utility {
     public static final String TAG = Utility.class.getSimpleName();
    //parsing and handle the Provinces data from server
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB
        coolWeatherDB, String response){
        if(!TextUtils.isEmpty(response)){
            String[] allProvinces = response.split(",");
            if(allProvinces.length > 0){
                for(String p : allProvinces){
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.provinceCode = array[0];
                    province.provinceName = array[1];
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    //parsing and handle the Cities data from server
    public synchronized static boolean handleCitiesResponse(CoolWeatherDB
        coolWeatherDB, String response, int provinceId){
        if(!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if(allCities.length > 0){
                for(String c : allCities){
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.cityCode = array[0];
                    city.cityName = array[1];
                    city.provinceId = provinceId;
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    //parsing and handle the Counties data from server
    public synchronized static boolean handleCountiesResponse(CoolWeatherDB
        coolWeatherDB, String response, int cityId){
        if(!TextUtils.isEmpty(response)){
            String[] allCounties = response.split(",");
            if(allCounties.length > 0){
                for(String c : allCounties){
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.countyCode = array[0];
                    county.countyName = array[1];
                    county.cityId = cityId;
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }


    public static void handleWeatherResponse(Context context, String response){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Log.d(TAG, "response " + response);
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode weatherInfo = jsonNode.findValue("weatherinfo");
            String cityName = weatherInfo.get("city").textValue();
            String weatherCode = weatherInfo.get("cityid").textValue();
            String temp1 = weatherInfo.get("temp1").textValue();
            String temp2 = weatherInfo.get("temp2").textValue();
            String weatherDesp = weatherInfo.get("weather").textValue();
            String publicTime = weatherInfo.get("ptime").textValue();
            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2,
                    weatherDesp, publicTime);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //store weather message in SharedPreferences from server
    public static void saveWeatherInfo(Context context, String cityName,
                                       String weatherCode, String temp1, String temp2,
                                       String weatherDesp, String publishTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.
                getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", sdf.format(new Date()));
        editor.apply();
    }

}

