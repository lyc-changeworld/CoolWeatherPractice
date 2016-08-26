package com.example.achuan.coolweatherpractice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.achuan.coolweatherpractice.service.AutoUpdateService;

/**
 * Created by achuan on 16-8-26.
 * 功能：接收发送的广播,实现定时服务的启动
 */
public class AutoUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1=new Intent(context, AutoUpdateService.class);
        context.startService(intent);//重新启动服务
    }
}
