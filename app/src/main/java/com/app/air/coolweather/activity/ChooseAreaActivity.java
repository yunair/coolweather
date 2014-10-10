package com.app.air.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.air.coolweather.R;
import com.app.air.coolweather.db.CoolWeatherDB;
import com.app.air.coolweather.model.City;
import com.app.air.coolweather.model.County;
import com.app.air.coolweather.model.Province;
import com.app.air.coolweather.util.HttpCallbackListener;
import com.app.air.coolweather.util.HttpUtil;
import com.app.air.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList = new ArrayList<String>();

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    //user selected
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;

    //current selected level
    private int currentLevel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose_area);


    }

    private void findViews(){
        titleText = (TextView) findViewById(R.id.choose_area_title);
        listView = (ListView) findViewById(R.id.choose_area_list_view);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });
        queryProvinces();

    }

    //query all counties in selected city,
    // first query from database
    //if database doesn't have
    //then query from server
    private void queryCounties() {
        countyList = coolWeatherDB.loadCounties(selectedCity.id);
        if(countyList.size() > 0){
            dataList.clear();
            for(County county : countyList){
                dataList.add(county.countyName);
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.cityName);
            currentLevel = LEVEL_COUNTY;
        }else {
            queryFromServer(selectedCity.cityCode, "county");
        }
    }
    //query all cities in selected province,
    // first query from database
    //if database doesn't have
    //then query from server
    private void queryCities() {
        cityList = coolWeatherDB.loadCities(selectedProvince.id);
        if(cityList.size() > 0){
            dataList.clear();
            for(City city : cityList){
                dataList.add(city.cityName);
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.provinceName);
            currentLevel = LEVEL_CITY;
        }else {
            queryFromServer(selectedProvince.provinceCode, "city");
        }
    }
    //query all provinces in China,
    // first query from database
    //if database doesn't have
    //then query from server
    private void queryProvinces() {
        provinceList = coolWeatherDB.loadProvinces();
        if(provinceList.size() > 0){
            dataList.clear();
            for(Province province : provinceList){
                dataList.add(province.provinceName);
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }else {
            queryFromServer(null, "province");
        }
    }

    //query data from server according to the code and type
    private void queryFromServer(String code, final String type) {
        String address;
        if(!TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city" + code
                    + ".xml";
        }else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgresDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if("province".equals(type)){
                    result = Utility.handleProvincesResponse(coolWeatherDB,
                            response);
                }else if("city".equals(type)){
                    result = Utility.handleCitiesResponse(coolWeatherDB,
                            response, selectedProvince.id);
                }else if("county".equals(type)){
                    result = Utility.handleCountiesResponse(coolWeatherDB,
                            response, selectedCity.id);
                }

                if(result){
                    //return to main Thread and handle logic
                    // by using function runOnUiThread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type))
                                queryProvinces();
                            else if("city".equals(type))
                                queryCities();
                            else if("county".equals(type))
                                queryCounties();
                        }


                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,
                                "load failured",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void closeProgressDialog() {
        if(progressDialog != null)
            progressDialog.dismiss();
    }

    private void showProgresDialog() {
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading …………");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    
    @Override
    public void onBackPressed() {
        if(currentLevel == LEVEL_COUNTY){
            queryCities();
        }else if(currentLevel == LEVEL_CITY)
            queryProvinces();
        else
            finish();
//        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.choose_area, menu);
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
}
