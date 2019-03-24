package Bean;

import android.util.Log;

import java.math.BigDecimal;

public class CommodityCardBean {

    private String commodityImgUrl;
    private String title;
    private double price;
    private int collectNumber;
    private String accountPhotoUrl;
    private String accountName;

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
                && (accountName == null ? com.accountName == null : accountName.equals(com.accountName));
    }
}
