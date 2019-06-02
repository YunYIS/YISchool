package com.example.yischool.session;

import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.BmobIMMessageHandler;

/**
 * @author 张云天
 * date on 2019/5/23
 * describe: 自定义消息接收器处理在线消息和离线消息
 */
public class MyMessageHandler extends BmobIMMessageHandler {

    public MyMessageHandler() {
        super();
    }

    @Override
    public void onMessageReceive(MessageEvent messageEvent) {
        super.onMessageReceive(messageEvent);
        //在线消息

    }

    @Override
    public void onOfflineReceive(OfflineMessageEvent offlineMessageEvent) {
        super.onOfflineReceive(offlineMessageEvent);
        //离线消息，每次connect的时候会查询离线消息，如果有，此方法会被调用

    }
}
