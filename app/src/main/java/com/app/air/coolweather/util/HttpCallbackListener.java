package com.app.air.coolweather.util;

/**
 * Created by air on 14-10-10.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
