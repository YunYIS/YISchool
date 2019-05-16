package com.example.yischool.Bean.ServerDatabaseBean;

import cn.bmob.v3.BmobObject;

public class browsingHistory extends BmobObject {

    private User browseUser;
    private Commodity browseCommodity;

    public User getBrowseUser() {
        return browseUser;
    }

    public void setBrowseUser(User browseUser) {
        this.browseUser = browseUser;
    }

    public Commodity getBrowseCommodity() {
        return browseCommodity;
    }

    public void setBrowseCommodity(Commodity browseCommodity) {
        this.browseCommodity = browseCommodity;
    }
}
