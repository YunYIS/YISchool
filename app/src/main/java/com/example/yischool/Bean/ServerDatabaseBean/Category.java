package com.example.yischool.Bean.ServerDatabaseBean;

import java.io.Serializable;
import java.util.Objects;

import cn.bmob.v3.BmobObject;

/**
 * @author 张云天
 * date on 2019/5/5
 * describe: 商品类别表实体类（大类别+二级类别+具体类型）（如：手机数码：数码配件：鼠标）
 * 保存每个大类别中的小类别和具体类别的名称和图片URL
 */
public class Category extends BmobObject implements Serializable {

    private String primaryCategory;//大类别
    private String secondaryCategory;//小类别
    private String specificCategory;//具体类别
    private String commodityIconUrl;//大小和位置与specificCategory数组对应，保存icon图片URL(保存Url可以重复利用素材库中相同图片)

    public Category(){}

    public Category(String primaryCategory, String secondaryCategory, String specificCategory, String commodityIconUrl) {
        this.primaryCategory = primaryCategory;
        this.secondaryCategory = secondaryCategory;
        this.specificCategory = specificCategory;
        this.commodityIconUrl = commodityIconUrl;
    }

    public String getCommodityIconUrl() {
        return commodityIconUrl;
    }

    public void setCommodityIconUrl(String commodityIconUrl) {
        this.commodityIconUrl = commodityIconUrl;
    }

    public String getPrimaryCategory() {
        return primaryCategory;
    }

    public void setPrimaryCategory(String primaryCategory) {
        this.primaryCategory = primaryCategory;
    }

    public String getSecondaryCategory() {
        return secondaryCategory;
    }

    public void setSecondaryCategory(String secondaryCategory) {
        this.secondaryCategory = secondaryCategory;
    }

    public String getSpecificCategory() {
        return specificCategory;
    }

    public void setSpecificCategory(String specificCategory) {
        this.specificCategory = specificCategory;
    }

    /**
     * 由于Category可能是由数据库查询得到，所以，在有Id时，使用主键Id（唯一）作为hash code
     * 主要也是用于查询时存储
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        if(getObjectId() != null && category.getObjectId() != null){
            return getObjectId().equals(category.getObjectId());
        }else {
            return Objects.equals(primaryCategory, category.primaryCategory) &&
                    Objects.equals(secondaryCategory, category.secondaryCategory) &&
                    Objects.equals(specificCategory, category.specificCategory) &&
                    Objects.equals(commodityIconUrl, category.commodityIconUrl);
        }
    }

    @Override
    public int hashCode() {
        if(getObjectId() != null){
            return getObjectId().hashCode();
        }else {
            return Objects.hash(primaryCategory, secondaryCategory, specificCategory, commodityIconUrl);
        }
    }
}
