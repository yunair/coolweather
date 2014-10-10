package com.app.air.coolweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.app.air.coolweather.model.City;
import com.app.air.coolweather.model.County;
import com.app.air.coolweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by air on 14-10-10.
 */
public class CoolWeatherDB {
    //database name
    public static final String DB_NAME = "cool_weather";

    //database version
    public static final int VERSION = 1;
    //because can't get Context so it can't initialize by
    //coolWeatherDB = new CoolWeatherDB();
    private static CoolWeatherDB coolWeatherDB;

    private SQLiteDatabase db;

    private CoolWeatherDB(Context context) {
        CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(
                context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public synchronized static CoolWeatherDB getInstance(Context context){
        if(coolWeatherDB != null)
            coolWeatherDB = new CoolWeatherDB(context);
        return  coolWeatherDB;
    }

    //save Province data into Province database
    public void saveProvince(Province province){
        if(province != null){
            ContentValues values = new ContentValues();
            values.put("province_name", province.provinceName);
            values.put("province_code", province.provinceCode);
            db.insert("Province", null, values);
        }
    }

    //load all Provinces from db
    public List<Province> loadProvinces(){
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = db.query("Province",
                null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do {
                Province province = new Province();
                province.id = cursor.getInt(cursor.getColumnIndex("id"));
                province.provinceName = cursor.getString(
                        cursor.getColumnIndex("province_name"));
                province.provinceCode = cursor.getString(
                        cursor.getColumnIndex("province_code"));
                list.add(province);
            }while(cursor.moveToNext());
        }

        return list;
    }

    //save City data into City database
    public void saveCity(City city){
        if(city != null){
            ContentValues values = new ContentValues();
            values.put("city_name", city.cityName);
            values.put("city_code", city.cityCode);
            values.put("province_id", city.provinceId);
            db.insert("City", null, values);
        }
    }

    //load all Cities from db
    public List<City> loadCities(int provinceId){
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("City", null, "provinceId = ?",
                new String[]{String.valueOf(provinceId)}, null, null, null);
        if(cursor.moveToFirst()){
            do {
                City city = new City();
                city.id = cursor.getInt(cursor.getColumnIndex("id"));
                city.cityName = cursor.getString(
                        cursor.getColumnIndex("city_name"));
                city.cityCode = cursor.getString(
                        cursor.getColumnIndex("city_code"));
                city.provinceId = provinceId;
                list.add(city);
            }while(cursor.moveToNext());
        }

        return list;
    }


    //save County data into County database
    public void saveCity(County county){
        if(county != null){
            ContentValues values = new ContentValues();
            values.put("county_name", county.countyName);
            values.put("county_code", county.countyCode);
            values.put("city_id", county.cityId);
            db.insert("County", null, values);
        }
    }

    //load all Counties from db
    public List<County> loadCounties(int cityId){
        List<County> list = new ArrayList<County>();
        Cursor cursor = db.query("City", null, "cityId = ?",
                new String[]{String.valueOf(cityId)}, null, null, null);
        if(cursor.moveToFirst()){
            do {
                County county = new County();
                county.id = cursor.getInt(cursor.getColumnIndex("id"));
                county.countyName = cursor.getString(
                        cursor.getColumnIndex("county_name"));
                county.countyCode = cursor.getString(
                        cursor.getColumnIndex("county_code"));
                county.cityId = cityId;
                list.add(county);
            }while(cursor.moveToNext());
        }

        return list;
    }




}
