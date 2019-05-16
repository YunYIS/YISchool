package com.example.yischool.Bean.ServerDatabaseBean;

import java.io.Serializable;

import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * @author 张云天
 * date on 2019/3/27
 * describe: 位置详细信息（GeoPoint根据地球的经度和纬度坐标进行基于地理位置的信息查询 + 用户自己填写的详细阐述）
 * 默认中国
 */
public class AddressDetail implements Serializable {

    private BmobGeoPoint point;
    private String province;//省份名
    private String city;//城市
    private String district;//区县
    private String describe;//地址详细描述

    public AddressDetail(){}

    public AddressDetail(BmobGeoPoint point, String province, String city, String district, String describe) {
        this.point = point;
        this.province = province;
        this.city = city;
        this.district = district;
        this.describe = describe;
    }

    public BmobGeoPoint getPoint() {
        return point;
    }

    public void setPoint(BmobGeoPoint point) {
        this.point = point;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
