package com.app.air.coolweather.util;

import android.text.TextUtils;

import com.app.air.coolweather.db.CoolWeatherDB;
import com.app.air.coolweather.model.City;
import com.app.air.coolweather.model.County;
import com.app.air.coolweather.model.Province;

/**
 * parsing and handle the data from server
 * Created by air on 14-10-10.
 */
public class Utility {
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

}
