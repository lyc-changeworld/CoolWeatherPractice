package com.example.achuan.coolweatherpractice.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.achuan.coolweatherpractice.model.City;
import com.example.achuan.coolweatherpractice.model.County;
import com.example.achuan.coolweatherpractice.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by achuan on 16-8-23.
 * 功能：将常用的数据库操作进行封装
 */
public class CoolWeatherDB {
    /*数据库名*/
    public static final String DB_NAME="cool_weather";
    /*数据库版本*/
    public static final int VERSION=1;
    private static CoolWeatherDB sCoolWeatherDB;//声明操作类实例
    private SQLiteDatabase db;//进行数据库操作的实例

    /*将构造方法私有化*/
    public CoolWeatherDB(Context context) {
        CoolWeatherOpenHelper dbHelper=new CoolWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db=dbHelper.getWritableDatabase();//通过Helper实例拿到具体的操作对象
    }
    /**获取CoolWeatherDB的实例
    * 对线程进行加锁,防止并发访问,保证初始化时只产生一个对象 synchronized  getInstance()
    * */
    public synchronized static CoolWeatherDB getInstance(Context context)
    {
        if(sCoolWeatherDB==null)
        {
            sCoolWeatherDB=new CoolWeatherDB(context);
        }
        return sCoolWeatherDB;
    }
    /**
    存储省份实例到数据库中
    **/
    public void saveProvince(Province province)
    {
        if(province!=null)
        {
            ContentValues values=new ContentValues();//基本类型数据存储对象
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            db.insert("Province",null,values);//将数据存储到省份表中
        }
    }
    /**
     * 从数据库中读取全国所有的省份信息
     * **/
    public List<Province> loadProvinces(){
        List<Province> list=new ArrayList<Province>();
        //创建游标实例,通过移动指针,将表格中的数据全部读取出来
        Cursor cursor=db.query("Province",null,null,null,null,null,null);
        if(cursor.moveToFirst())
        {
            do{
                Province province=new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));//表格中的序号
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);//将数据库中的数据存储到集合中
            }while (cursor.moveToNext());
        }
        return list;
    }
    /**
     * 将City实例存储到数据库。
     */
    public void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("city_code", city.getCityCode());
            values.put("province_id", city.getProvinceId());
            db.insert("City", null, values);
        }
    }

    /**
     * 从数据库读取某省下所有的城市信息。
     */
    public List<City> loadCities(int provinceId) {
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("City", null, "province_id = ?",
                new String[] { String.valueOf(provinceId) }, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor
                        .getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor
                        .getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                list.add(city);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 将County实例存储到数据库。
     */
    public void saveCounty(County county) {
        if (county != null) {
            ContentValues values = new ContentValues();
            values.put("county_name", county.getCountyName());
            values.put("county_code", county.getCountyCode());
            values.put("city_id", county.getCityId());
            db.insert("County", null, values);
        }
    }
    /**
     * 从数据库读取某城市下所有的县信息。
     */
    public List<County> loadCounties(int cityId) {
        List<County> list = new ArrayList<County>();
        Cursor cursor = db.query("County", null, "city_id = ?",
                new String[] { String.valueOf(cityId) }, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor
                        .getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor
                        .getColumnIndex("county_code")));
                county.setCityId(cityId);
                list.add(county);
            } while (cursor.moveToNext());
        }
        return list;
    }
}
