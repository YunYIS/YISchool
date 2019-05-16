package com.example.yischool.Bean.ServerDatabaseBean;

import cn.bmob.v3.BmobObject;

/**
 * @author 张云天
 * date on 2019/3/27
 * describe: 用户-评价-用户 关系表
 */
public class Evaluate extends BmobObject {

    private String content;//评价内容
    private User publishUser;//发表评价的用户
    private User evaluatedUser;//被评价的用户
    private int evaluateOfStar;//该评价的星级，最高五星（整型）

    public String getContent() {
        return content;
    }

    public User getPublishUser() {
        return publishUser;
    }

    public User getEvaluatedUser() {
        return evaluatedUser;
    }

    public int getEvaluateOfStar() {
        return evaluateOfStar;
    }

    public Evaluate setContent(String content) {
        this.content = content;
        return this;
    }

    public Evaluate setPublishUser(User publishUser) {
        this.publishUser = publishUser;
        return this;
    }

    public Evaluate setEvaluatedUser(User evaluatedUser) {
        this.evaluatedUser = evaluatedUser;
        return this;
    }

    public Evaluate setEvaluateOfStar(int evaluateOfStar) {
        this.evaluateOfStar = evaluateOfStar;
        return this;
    }
}
