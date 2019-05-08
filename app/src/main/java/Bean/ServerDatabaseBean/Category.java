package Bean.ServerDatabaseBean;

import cn.bmob.v3.BmobObject;

/**
 * @author 张云天
 * date on 2019/5/5
 * describe: 商品类别表实体类（大类别+二级类别+具体类型）（如：手机数码：数码配件：鼠标）
 * 保存每个大类别中的小类别和具体类别的名称和图片URL
 */
public class Category extends BmobObject {

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
}
