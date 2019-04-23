package Bean.ServerDatabaseBean;

import cn.bmob.v3.BmobObject;

/**
 * @author 张云天
 * date on 2019/3/27
 * describe: （（由商品表查询到的用户）用户-）商品-用户-留言 关系表
 */
public class LeaveWords extends BmobObject {

    private String content;//留言的内容
    private User publishUser;//发布留言的用户
    private Commodity commodity;//属于该商品的留言

    public String getContent() {
        return content;
    }

    public User getPublishUser() {
        return publishUser;
    }

    public Commodity getCommodity() {
        return commodity;
    }

    public LeaveWords setContent(String content) {
        this.content = content;
        return this;
    }

    public LeaveWords setPublishUser(User publishUser) {
        this.publishUser = publishUser;
        return this;
    }

    public LeaveWords setCommodity(Commodity commodity) {
        this.commodity = commodity;
        return this;
    }
}
