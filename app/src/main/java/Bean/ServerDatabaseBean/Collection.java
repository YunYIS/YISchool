package Bean.ServerDatabaseBean;

import cn.bmob.v3.BmobObject;

/**
 * @author 张云天
 * date on 2019/3/27
 * describe: 商品-用户 收藏关系表
 */
public class Collection extends BmobObject {

    private Commodity collectCommodity;//收藏的商品
    private User collectUser;//收藏该商品的用户

    public Commodity getCollectCommodity() {
        return collectCommodity;
    }

    public Collection setCollectCommodity(Commodity collectCommodity) {
        this.collectCommodity = collectCommodity;
        return this;
    }

    public User getCollectUser() {
        return collectUser;
    }

    public Collection setCollectUser(User collectUser) {
        this.collectUser = collectUser;
        return this;
    }
}
