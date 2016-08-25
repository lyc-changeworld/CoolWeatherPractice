package com.example.achuan.coolweatherpractice.util;

/**
 * Created by achuan on 16-8-24.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
