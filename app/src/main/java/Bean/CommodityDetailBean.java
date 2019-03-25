package Bean;

import java.util.Date;
import java.util.List;

/**
 * @author 张云天
 * date on 2019/3/25
 * describe: 商品详细信息展示页实体类（JSON数据解析）
 */
public class CommodityDetailBean {

    private List<String> commodityImgUrl;//该商品的所有图片
    private String description;//商品详情描述
    private String publishDate;//发布日期
    private String location;//发布位置（卖家位置）
    private String category;//分类（由于详情页之后的推荐列表）
    private Date accountRegisterDate;//用户注册日期（用于计算用户描述信息）
    private String college;//学院
    private String major;//专业

}
