package Bean.ServerDatabaseBean;

import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * @author 张云天
 * date on 2019/3/27
 * describe: 位置详细信息（GeoPoint根据地球的经度和纬度坐标进行基于地理位置的信息查询 + 用户自己填写的详细阐述）
 */
public class AddressDetail{

    private BmobGeoPoint point;
    private String describe;

    public AddressDetail(BmobGeoPoint point, String describe){
        this.point = point;
        this.describe = describe;
    }

    public BmobGeoPoint getPoint() {
        return point;
    }

    public void setPoint(BmobGeoPoint point) {
        this.point = point;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
