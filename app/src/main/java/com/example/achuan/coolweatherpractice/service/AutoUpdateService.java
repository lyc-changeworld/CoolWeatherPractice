package com.example.achuan.coolweatherpractice.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.example.achuan.coolweatherpractice.receiver.AutoUpdateReceiver;
import com.example.achuan.coolweatherpractice.util.HttpCallbackListener;
import com.example.achuan.coolweatherpractice.util.HttpUtil;
import com.example.achuan.coolweatherpractice.util.Utility;

/**
 * Created by achuan on 16-8-26.
 */
public class AutoUpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    /**
     * 更新天气信息的方法
     * */
    private void updateWeather()
    {
        //活动存储文件操作实例
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        //获取存储文件中记录的天气代号
        String weatherCode=preferences.getString("weather_code","");
        //合成天气信息的访问链接
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        HttpUtil.sendRequestWithHttpURLConnection(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                //对服务器返回的数据进行解析并存储到本地文件
                Utility.handleWeatherResponse(AutoUpdateService.this,response);
            }
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * //每次服务启动的时候调用,主要的逻辑编写的地方
     * **/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();//更新天气信息
            }
        }).start();
        //先获得系统定时服务
        AlarmManager manager= (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour=60*60*1000;//这是一小时的毫秒数
        //设置定时后的那个时间点
        long triggerAtTime= SystemClock.elapsedRealtime()//系统开机至今的时间毫秒数
                +anHour*8;//设定的定时毫秒数(8小时更新一次)
        //设置一个意图,接收者为AutoUpdateReceiver类
        Intent intent1=new Intent(this,AutoUpdateReceiver.class);
        //创建一个延缓的意图
        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,0,intent1,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,//系统开机的时间
                triggerAtTime,//开机后计算的时间毫秒数
                pendingIntent);//到了（系统开机时间+开机后计算的时间毫秒数）时,启动这个意图,发出一条广播
        return super.onStartCommand(intent, flags, startId);
    }
}
