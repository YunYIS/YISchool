package com.example.yischool.Bean.ServerDatabaseBean;

import org.litepal.annotation.Column;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * @author 张云天
 * date on 2019/3/25
 * describe: 服务器商品详细信息表实体类
 */
public class Commodity extends BmobObject {

    //交易方式
    public static final transient int MEANS_EXCHANGE_TAKE = 1;//自提
    public static final transient int MEANS_EXCHANGE_FACE = 2;//当面交易
    public static final transient int MEANS_EXCHANGE_MAIL = 4;//邮寄

    //objectId(唯一标识)，注册时间等属性已由BmobObject继承
    private String title;//商品标题
    private User publishUser;//发布商品的用户
    private AddressDetail publishPosition;//发布位置（卖家位置）
    private Category category;//商品类别（由于详情页之后的推荐列表）
    private String detail;//商品详情描述
    private Double price;//价格
    private boolean isSoldOut;//是否已售完（默认否）
    private boolean isCancelSale;//是否下架（即取消商品销售，用户自己取消，也可由管理员下架）
    private List<BmobFile> pictureAndVideo;//关于商品的所有图片和视频信息（保存为File，因为不需要重复利用图片）
    private String brand;//品牌
    private int meansOfExchange;//交易方式
    private int browseCount;//浏览数量

    public int getBrowseCount() {
        return browseCount;
    }

    public void setBrowseCount(int browseCount) {
        this.browseCount = browseCount;
    }

    public String getTitle() {
        return title;
    }

    public User getPublishUser() {
        return publishUser;
    }

    public AddressDetail getPublishPosition() {
        return publishPosition;
    }

    public Category getCategory() {
        return category;
    }

    public String getDetail() {
        return detail;
    }

    public Double getPrice() {
        return price;
    }

    public boolean isSoldOut() {
        return isSoldOut;
    }

    public boolean isCancelSale() {
        return isCancelSale;
    }

    public List<BmobFile> getPictureAndVideo() {
        return pictureAndVideo;
    }

    public String getBrand() {
        return brand;
    }

    public int getMeansOfExchange() {
        return meansOfExchange;
    }

    public Commodity setBrand(String brand) {
        this.brand = brand;
        return this;
    }

    public Commodity setMeansOfExchange(int meansOfExchange) {
        this.meansOfExchange = meansOfExchange;
        return this;
    }

    public Commodity setTitle(String title) {
        this.title = title;
        return this;
    }

    public Commodity setPublishUser(User publishUser) {
        this.publishUser = publishUser;
        return this;
    }

    public Commodity setPublishPosition(AddressDetail publishPosition) {
        this.publishPosition = publishPosition;
        return this;
    }

    public Commodity setCategory(Category category) {
        this.category = category;
        return this;
    }

    public Commodity setDetail(String detail) {
        this.detail = detail;
        return this;
    }

    public Commodity setPrice(Double price) {
        this.price = price;
        return this;
    }

    public Commodity setSoldOut(boolean soldOut) {
        isSoldOut = soldOut;
        return this;
    }

    public Commodity setCancelSale(boolean cancelSale) {
        isCancelSale = cancelSale;
        return this;
    }

    public Commodity setPictureAndVideo(List<BmobFile> pictureAndVideo) {
        this.pictureAndVideo = pictureAndVideo;
        return this;
    }
}
