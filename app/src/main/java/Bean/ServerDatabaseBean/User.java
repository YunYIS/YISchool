package Bean.ServerDatabaseBean;

import java.io.Serializable;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;

/**
 * @author 张云天
 * date on 2019/3/27
 * describe: 服务器用户表实体类
 */
public class User extends BmobUser implements Serializable {

    //objectId(唯一标识)，用户名（会员名，唯一），密码，电话号码，邮箱，电话号码是否可用，邮箱是否可用，注册时间，修改时间等属性已由BmobUser类继承
    private BmobFile headPortrait;//用户头像
    private String sex;//性别
    private BmobDate birthday;//用户生日
    private String major;//主修专业
    private String college;//所属学院
    private String theSignature;//个性签名
    private List<String> hobbies;//用户爱好
    private List<AddressDetail> shoppingAddress;//收货地址
    private String idCardNO;//身份证号码(卖家需要)
    private BmobFile campusCardPhoto;//卖家验证身份信息图片（校园卡证件照）

    public BmobFile getHeadPortrait() {
        return headPortrait;
    }

    public User setHeadPortrait(BmobFile headPortrait) {
        this.headPortrait = headPortrait;
        return this;
    }

    public String getSex() {
        return sex;
    }

    public User setSex(String sex) {
        this.sex = sex;
        return this;
    }

    public BmobDate getBirthday() {
        return birthday;
    }

    public User setBirthday(BmobDate birthday) {
        this.birthday = birthday;
        return this;
    }

    public String getMajor() {
        return major;
    }

    public User setMajor(String major) {
        this.major = major;
        return this;
    }

    public String getCollege() {
        return college;
    }

    public User setCollege(String college) {
        this.college = college;
        return this;
    }

    public String getTheSignature() {
        return theSignature;
    }

    public User setTheSignature(String theSignature) {
        this.theSignature = theSignature;
        return this;
    }

    public List<String> getHobbies() {
        return hobbies;
    }

    public User setHobbies(List<String> hobbies) {
        this.hobbies = hobbies;
        return this;
    }

    public List<AddressDetail> getShoppingAddress() {
        return shoppingAddress;
    }

    public User setShoppingAddress(List<AddressDetail> shoppingAddress) {
        this.shoppingAddress = shoppingAddress;
        return this;
    }

    public String getIdCardNO() {
        return idCardNO;
    }

    public User setIdCardNO(String idCardNO) {
        this.idCardNO = idCardNO;
        return this;
    }

    public BmobFile getCampusCardPhoto() {
        return campusCardPhoto;
    }

    public User setCampusCardPhoto(BmobFile campusCardPhoto) {
        this.campusCardPhoto = campusCardPhoto;
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName=" + getUsername() +
                ", sex='" + sex + '\'' +
                "telephone=" + getMobilePhoneNumber() +
                "telephoneVerify=" + getMobilePhoneNumberVerified() +
                ", sex='" + sex + '\'' +
                "email=" + getEmail() +
                ", sex='" + sex + '\'' +
                ", sex='" + sex + '\'' +
                ", sex='" + sex + '\'' +
                "headPortrait=" + headPortrait +
                ", sex='" + sex + '\'' +
                ", birthday=" + birthday +
                ", major='" + major + '\'' +
                ", college='" + college + '\'' +
                ", theSignature='" + theSignature + '\'' +
                ", hobbies=" + hobbies +
                ", shoppingAddress=" + shoppingAddress +
                ", idCardNO='" + idCardNO + '\'' +
                ", campusCardPhoto=" + campusCardPhoto +
                '}';
    }
}
