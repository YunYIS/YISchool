package com.example.yischool.Bean.jsonBean.AreaBean;

/**
 * @author 张云天
 * date on 2019/5/15
 * describe: 区县json解析实体类
 */
public class District {

    private String id;
    private String district;
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
    public String getDistrict() {
        return district;
    }
}
