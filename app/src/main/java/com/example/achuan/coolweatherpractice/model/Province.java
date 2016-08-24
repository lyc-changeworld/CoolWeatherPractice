package com.example.achuan.coolweatherpractice.model;

/**
 * Created by achuan on 16-8-23.
 * 功能：省份信息的实体类
 */
public class Province {
    private int id;
    private String provinceName;
    private String provinceCode;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }
}
