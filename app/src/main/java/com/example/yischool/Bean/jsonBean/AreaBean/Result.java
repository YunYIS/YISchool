package com.example.yischool.Bean.jsonBean.AreaBean;

import java.util.List;

/**
 * @author 张云天
 * date on 2019/5/15
 * describe: province json实体类
 */
public class Result {

    private String id;
    private String province;
    private List<City> city;
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setProvince(String province) {
        this.province = province;
    }
    public String getProvince() {
        return province;
    }

    public void setCity(List<City> city) {
        this.city = city;
    }
    public List<City> getCity() {
        return city;
    }
}
