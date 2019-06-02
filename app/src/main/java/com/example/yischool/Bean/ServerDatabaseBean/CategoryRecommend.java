package com.example.yischool.Bean.ServerDatabaseBean;

import cn.bmob.v3.BmobObject;

public class CategoryRecommend extends BmobObject {

    private String primaryCategory;//具体分类
    private String recommendContent;//推荐内容
    private String recommendUri;//推荐图片

    public CategoryRecommend(){}

    public CategoryRecommend(String primaryCategory, String recommendContent, String recommendUri) {
        this.primaryCategory = primaryCategory;
        this.recommendContent = recommendContent;
        this.recommendUri = recommendUri;
    }

    public String getPrimaryCategory() {
        return primaryCategory;
    }

    public void setPrimaryCategory(String primaryCategory) {
        this.primaryCategory = primaryCategory;
    }

    public String getRecommendContent() {
        return recommendContent;
    }

    public void setRecommendContent(String recommendContent) {
        this.recommendContent = recommendContent;
    }

    public String getRecommendUri() {
        return recommendUri;
    }

    public void setRecommendUri(String recommendUri) {
        this.recommendUri = recommendUri;
    }
}
