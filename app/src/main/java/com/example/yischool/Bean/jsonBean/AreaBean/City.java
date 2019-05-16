package com.example.yischool.Bean.jsonBean.AreaBean;

import java.util.List;

/**
 * @author 张云天
 * date on 2019/5/15
 * describe: 城市json实体类
 */
public class City {

    private String id;
    private String city;
    private List<District> district;
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setCity(String city) {
        this.city = city;
    }
    public String getCity() {
        return city;
    }

    public void setDistrict(List<District> district) {
        this.district = district;
    }
    public List<District> getDistrict() {
        return district;
    }
}
