package Bean.jsonBean;

import org.litepal.annotation.Column;

import java.math.BigDecimal;

/**
 * @author 张云天
 * date on 2019/3/24
 * describe: 商品浏览时,商品结果展示时，商品卡片的实体类（数据库操作+JSON数据解析）
 */
public class CommodityCardBean {

    private String commodityImgUrl;//商品图片（取第一张图，即主图）
    private String title;//商品标题
    private double price;//商品价格
    private int collectNumber;//收藏人数
    private String accountPhotoUrl;//卖家用户头像
    private String accountName;//卖家账户名
    @Column(unique = true, nullable = false, defaultValue = "0")
    private String commodityID;//商品服务器端数据库唯一标识ID，用于请求服务器商品数据(不能重复，不能为空)
    //（与客户端数据库中的id不同,本类为了和GSON实体类共用，没有写id字段）

    private CommodityCardBean(){}

    public String getCommodityImgUrl() {
        return commodityImgUrl;
    }

    public void setCommodityImgUrl(String commodityImgUrl) {
        this.commodityImgUrl = commodityImgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCollectNumber() {
        return collectNumber;
    }

    public void setCollectNumber(int collectNumber) {
        this.collectNumber = collectNumber;
    }

    public String getAccountPhotoUrl() {
        return accountPhotoUrl;
    }

    public void setAccountPhotoUrl(String accountPhotoUrl) {
        this.accountPhotoUrl = accountPhotoUrl;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getCommodityID() {
        return commodityID;
    }

    public void setCommodityID(String commodityID) {
        this.commodityID = commodityID;
    }

    /**
     * 由于商品卡片对象用到了HashSet，所以重写hashCode和equals方法
     * @return
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (commodityImgUrl != null ? commodityImgUrl.hashCode():0);
        result = 31 * result + (title != null ? title.hashCode():0);
        long doubleTemp = Double.doubleToLongBits(price);
        result = 31 * result + (int)(doubleTemp ^ (doubleTemp >>> 32));
        result = 31 * result + collectNumber;
        result = 31 * result + (accountPhotoUrl != null ? accountPhotoUrl.hashCode():0);
        result = 31 * result + (accountName != null ? accountName.hashCode():0);
        result = 31 * result + (commodityID != null ? commodityID.hashCode():0);

        return result;
    }
    /**
     * 重写equals方法
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if(obj == null){
            return false;
        }
        if(!(obj instanceof CommodityCardBean)){
            return false;
        }
        CommodityCardBean com = (CommodityCardBean)obj;
        int ret = BigDecimal.valueOf(price).compareTo(BigDecimal.valueOf(com.price));//double类型精度比较

        //逐个比较对象成员
        return (commodityImgUrl == null ? com.commodityImgUrl == null : commodityImgUrl.equals(com.commodityImgUrl))//字符串先判空，再比较
                && (title == null ? com.title == null : title.equals(com.title))
                && ret == 0
                && collectNumber == com.collectNumber
                && (accountPhotoUrl == null ? com.accountPhotoUrl == null : accountPhotoUrl.equals(com.accountPhotoUrl))
                && (accountName == null ? com.accountName == null : accountName.equals(com.accountName))
                && (commodityID == null ? com.commodityID == null : commodityID.equals(com.commodityID));
    }

    public static class Builder{

        private CommodityCardBean cardBean = new CommodityCardBean();
        public Builder() {}

        public Builder setCommodityImgUrl(String commodityImgUrl){
            cardBean.commodityImgUrl = commodityImgUrl;
            return this;
        }
        public Builder setTitle(String title){
            cardBean.title = title;
            return this;
        }
        public Builder setPrice(double price){
            cardBean.price = price;
            return this;
        }
        public Builder setCollectNumber(int collectNumber){
            cardBean.collectNumber = collectNumber;
            return this;
        }
        public Builder setAccountPhotoUrl(String accountPhotoUrl){
            cardBean.accountPhotoUrl = accountPhotoUrl;
            return this;
        }
        public Builder setAccountName(String accountName){
            cardBean.accountName = accountName;
            return this;
        }
        public Builder setCommodityID(String commodityID){
            cardBean.commodityID = commodityID;
            return this;
        }
        public CommodityCardBean build(){
            return cardBean;
        }
    }
}
