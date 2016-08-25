package com.example.achuan.coolweatherpractice.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.achuan.coolweatherpractice.R;
import com.example.achuan.coolweatherpractice.util.HttpCallbackListener;
import com.example.achuan.coolweatherpractice.util.HttpUtil;
import com.example.achuan.coolweatherpractice.util.Utility;

/**
 * Created by achuan on 16-8-24.
 */
public class WeatherActivity extends AppCompatActivity{
    private LinearLayout weatherInfoLayout;
    /**
     * 用于显示城市名
     */
    private TextView cityNameText;
    /**
     * 用于显示发布时间
     */
    private TextView publishText;
    /**
     * 用于显示天气描述信息
     */
    private TextView weatherDespText;
    /**
     * 用于显示最低气温
     */
    private TextView temp1Text;
    /**
     * 用于显示最高气温
     */
    private TextView temp2Text;
    /**
     * 用于显示当前日期
     */
    private TextView currentDateText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.weather_layout);
        //初始化加载控件
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);
        //获取上个activity传递过来的城市代号
        String countyCode=getIntent().getStringExtra("county_code");
        if(!TextUtils.isEmpty(countyCode))
        {
            //县级代号不为空就可以去服务器查询天气了
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);//让天气信息部分显示出来
            cityNameText.setVisibility(View.INVISIBLE);//显示县级名称
            //先查询县级的代号,再将代号添加到链接中进行网络访问获取数据,解析后存储到本地文件,然后从本地读取出来
            queryWeatherCode(countyCode);//通过县级代号获得天气代号,再获得天气信息
        }
        else {
            //直接显示上一次在本地文件中存储的天气信息
            showWeather();
        }
    }
    /**
     * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上。
     */
    private void showWeather() {
        //拿到存储文件的实例
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText( prefs.getString("city_name", ""));
        temp1Text.setText(prefs.getString("temp1", ""));
        temp2Text.setText(prefs.getString("temp2", ""));
        weatherDespText.setText(prefs.getString("weather_desp", ""));
        publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
        currentDateText.setText(prefs.getString("current_date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }
    /**
     * 根据传入的地址和类型去向服务器查询天气代号或者天气信息
     * */
    private void queryFromServer(final String address,final String type)
    {
        HttpUtil.sendRequestWithHttpURLConnection(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                if("countyCode".equals(type))
                {
                    //如果传入的时县级代号
                    if(!TextUtils.isEmpty(response))
                    {
                        //从服务器返回的数据中解析出天气代号
                        String[] array = response.split("\\|");
                        if(array!=null&&array.length==2)
                        {//满足：xxx|xxxxx　格式的数据才进行解析
                            String weatherCode=array[1];//单竖线右边的数才是天气代号
                            //查询天气代号对应的天气信息
                            queryWeatherInfo(weatherCode);
                        }
                    }else if("weatherCode".equals(type))
                    {
                        //对服务返回的天气信息进行解析,并存储到本地文件中
                        Utility.handleWeatherResponse(WeatherActivity.this,response);
                        //从子线程返回到主线程进行UI刷新显示
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showWeather();
                            }
                        });
                    }
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
    /**
     * 查询县级代号对应的天气代号
     * */
    private void queryWeatherCode(String countyCode)
    {
        //天气代号的访问链接
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(address, "countyCode");
    }
    /**
     * 查询天气代号所对应的天气。
     */
    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        queryFromServer(address, "weatherCode");
    }

}
