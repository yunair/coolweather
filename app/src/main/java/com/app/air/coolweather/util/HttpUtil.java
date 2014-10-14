package com.app.air.coolweather.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by air on 14-10-10.
 */
public class HttpUtil {
    public static final String TAG = Utility.class.getSimpleName();
    public static void sendHttpRequest(final String address,
                                       final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    InputStream is = connection.getInputStream();
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(is));
                    StringBuilder reponse = new StringBuilder();
                    String line = "";
                    while((line = br.readLine()) != null){
                        reponse.append(line);
                    }
                    if(listener != null){
                        listener.onFinish(reponse.toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if(listener != null)
                        listener.onError(e);
                }finally {
                    if(connection != null)
                        connection.disconnect();
                }
            }
        }).start();
    }
}
