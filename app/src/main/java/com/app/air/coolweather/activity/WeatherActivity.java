package com.app.air.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.air.coolweather.R;
import com.app.air.coolweather.util.HttpCallbackListener;
import com.app.air.coolweather.util.HttpUtil;
import com.app.air.coolweather.util.Utility;

public class WeatherActivity extends Activity implements View.OnClickListener{
    public static final String TAG = Utility.class.getSimpleName();
    //show city name
    private TextView weatherCityName;
    //show publish time
    private TextView publishText;
    private LinearLayout weatherInfoLayout;
    //show current time
    private TextView currentDate;
    //show weather description
    private TextView weatherDesp;
    //show temperature 1
    private TextView temp2;
    //show temperature 2
    private TextView temp1;
    //switch city button
    private Button switchCity;
    //refresh weather button
    private Button refreshWeather;



    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2014-10-11 15:12:48 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        weatherCityName = (TextView)findViewById( R.id.weather_city_name );
        publishText = (TextView)findViewById( R.id.publish_text );
        weatherInfoLayout = (LinearLayout)findViewById( R.id.weather_info_layout );
        currentDate = (TextView)findViewById( R.id.current_date );
        weatherDesp = (TextView)findViewById( R.id.weather_desp );
        temp1 = (TextView)findViewById( R.id.temp1 );
        temp2 = (TextView)findViewById( R.id.temp2 );
        switchCity = (Button)findViewById( R.id.switch_city );
        refreshWeather = (Button)findViewById( R.id.refresh_weather );

        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        String countyCode = getIntent().getStringExtra("county_code");
        findViews();


        if(!TextUtils.isEmpty(countyCode)){
            //have county code then query weather
            publishText.setText("同步中……");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            weatherCityName.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        }else {
            //don't have county code then show the weather
            showWeather();
        }
    }

    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(this);
        weatherCityName.setText(prefs.getString("city_name", ""));
        Log.d(TAG, prefs.getString("city_name", ""));
        temp1.setText(prefs.getString("temp1", ""));
        Log.d(TAG, "temp 1 " + prefs.getString("temp1", ""));
        temp2.setText(prefs.getString("temp2", ""));
        weatherDesp.setText(prefs.getString("weather_desp", ""));
        publishText.setText("今天" + prefs.getString("public_time", "") + "发布");
        currentDate.setText(prefs.getString("current_date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        weatherCityName.setVisibility(View.VISIBLE);
    }

    //查询县级代号所对应的天气
    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" +
                countyCode + ".xml";
        queryFromServer(address, "countyCode");
    }

    //查询天气代号所对应的天气
    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" +
                weatherCode + ".html";
        queryFromServer(address, "weatherCode");
    }

    private void queryFromServer(String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {

                if("countyCode".equals(type)){
                    if(!TextUtils.isEmpty(response)) {
                        //
                        String[] array = response.split("\\|");
                        if(array.length == 2){
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                }else if("weatherCode".equals(type)){
                    Utility.handleWeatherResponse(WeatherActivity.this,
                            response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.switch_city :
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather :
                publishText.setText("同步中……");
                SharedPreferences prefs = PreferenceManager.
                        getDefaultSharedPreferences(this);
                String weatherCode = prefs.getString("weather_code", "");
                if(!TextUtils.isEmpty(weatherCode)){
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:
                break;
        }
    }
}
