package com.example.achuan.coolweatherpractice.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.achuan.coolweatherpractice.db.CoolWeatherDB;
import com.example.achuan.coolweatherpractice.model.City;
import com.example.achuan.coolweatherpractice.model.County;
import com.example.achuan.coolweatherpractice.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by achuan on 16-8-24.
 */
public class Utility {
     /**
      * 解析和处理服务器返回的省级数据
      * */
     public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,String response)
     {
         if(!TextUtils.isEmpty(response))//如果服务器返回的数据不为空
         {
             String[] allProvinces=response.split(",");//省份信息按逗号分隔字符串,存储到数组中
             if(allProvinces!=null&&allProvinces.length>0)//如果数组中有数据
             {
                 for(String p:allProvinces)//遍历所有的数组元素
                 {
                    String[] array=p.split("\\|");//将代号和城市名分隔开来,“.”和“|”都是转义字符，必须得加"\\";
                     Province province=new Province();//新建省份实例
                     province.setProvinceCode(array[0]);//“|”号左边的是代号,对应第一个元素
                     province.setProvinceName(array[1]);//右边的是城市名,对应第二个元素
                     //将解析出来的数据存储到Province表
                     coolWeatherDB.saveProvince(province);
                 }
                 return true;
             }
         }
         return false;
     }
    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,
                                               String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    // 将解析出来的数据存储到City表
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,
                                                 String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    // 将解析出来的数据存储到County表
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }
    /**
    *将服务器返回的所有天气信息存储到Sharedferences文件中
    **/
    public static void saveWeatherInfo(Context context, String cityName,
                                       String weatherCode, String temp1, String temp2, String weatherDesp,
                                       String publishTime) {
        //设置当前时间显示的格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context)
                .edit();//通过edit()方法获取SharedPreferences.Editor对象
        //添加数据
        editor.putBoolean("city_selected", true);//标志位,记录当前是否已经选择过城市了
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", sdf.format(new Date()));//获得系统的时间
        editor.commit();/*将添加的数据提交*/
    }
    /**
     *解析服务器返回的JSON数据,并存储到本地
     **/
    public static void handleWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            //通过信息名称获取数据集对象
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            //将数据集中的数据一一提取出来
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2,
                    weatherDesp, publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
