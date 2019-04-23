package Bean.citySchools;

import java.util.List;

/**
 * @author 张云天
 * date on 2019/4/11
 * describe: 城市—学校json实体类
 */
public class CityBean {
    private int id;
    private List<SchoolBean> school;
    private String name;
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setSchool(List<SchoolBean> school) {
        this.school = school;
    }
    public List<SchoolBean> getSchool() {
        return school;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
