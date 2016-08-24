package com.example.achuan.coolweatherpractice.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by achuan on 16-8-23.
 * 功能：数据库的创建及更新
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper{
    /*
    * Province 创建表语句
    * */
    public static final String CREATE_PROVINCE =
            "create table Province("
                    +"id integer primary key autoincrement,"
                    +"province_name text,"
                    +"province_code text)";
    /*
    * City create table
    * */
    public static final String CREATE_CITY =
            "create table City("
                    +"id integer primary key autoincrement,"
                    +"city_name text,"
                    +"city_code text,"
                    +"province_id integer)";//
    /*
    * County create table
    * */
    public static final String CREATE_COUNTY =
            "create table County("
                    +"id integer primary key autoincrement,"
                    +"county_name text,"
                    +"county_code text,"
                    +"city_id integer)";//


    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_PROVINCE);//执行创建省表命令
        sqLiteDatabase.execSQL(CREATE_CITY);//创建市表
        sqLiteDatabase.execSQL(CREATE_COUNTY);//创建县表
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
