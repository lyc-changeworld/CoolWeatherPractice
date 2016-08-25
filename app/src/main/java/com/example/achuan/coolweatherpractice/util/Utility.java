package com.example.achuan.coolweatherpractice.util;

import android.text.TextUtils;

import com.example.achuan.coolweatherpractice.db.CoolWeatherDB;
import com.example.achuan.coolweatherpractice.model.City;
import com.example.achuan.coolweatherpractice.model.County;
import com.example.achuan.coolweatherpractice.model.Province;

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

}
