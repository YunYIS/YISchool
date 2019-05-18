package com.example.yischool.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.yischool.Bean.ServerDatabaseBean.Category;
import com.example.yischool.Bean.ServerDatabaseBean.Collection;
import com.example.yischool.Bean.ServerDatabaseBean.Commodity;
import com.example.yischool.Bean.ServerDatabaseBean.User;
import com.example.yischool.Bean.ServerDatabaseBean.browsingHistory;
import com.example.yischool.Bean.jsonBean.CommodityCardBean;
import com.example.yischool.SearchResultActivity;
import com.example.yischool.bmobNet.QueryHelper;
import com.example.yischool.publish.ChoosePositionActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import cn.bmob.v3.BmobQuery;

import static com.example.yischool.SearchActivity.SEARCH_CONTENT;

/**
 * @author 张云天
 * date on 2019/5/17
 * describe: 商品搜索策略 + 排序方式
 * 搜索策略在毕业设计记录策中
 */
public class CommodityCardSearchService extends Service {

    public static final String LOG_TAG = "CommoditySearchService";

    /*
     * 查询动作请求码，区分handler返回的消息
     */
    //查询大类别
    public static final int REQUEST_PRIMARY_CATEGORY = 1;
    //查询小类别
    public static final int REQUEST_SECONDARY_CATEGORY = 2;
    //查询具体类别
    public static final int REQUEST_SPEC_CATEGORY = 3;
    //查询商品
    public static final int REQUEST_TITLE_COMMODITY = 4;
    //商品浏览量统计查询
    public static final int REQUEST_BROWSE_COMMODITY = 5;
    //商品收藏量统计查询
    public static final int REQUEST_COLLECTION_COMMODITY = 6;

    /**
     * 通知上个活动，handler what
     */
    //数据加载完成
    public static final int RESULT_COMPLETED = 11;




    //搜索内容
    private String searchContentStr;
    //搜索内容匹配的商品分类
    private HashSet<Category> categoryHashSet;
    //大类别保存结果
    private HashSet<Category> primaryCategorySet;
    private boolean isPriSuccess = false;//大类别查询成功
    //小类别保存结果
    private HashSet<Category> secondaryCategorySet;
    private boolean isSecSuccess = false;//小类别查询成功
    //具体类别保存结果
    private HashSet<Category> specCategorySet;
    private boolean isSpecSuccess = false;//具体类别查询成功
    //收藏量保存
    private List<Integer> countList;


    //商品搜索结果
    private HashSet<Commodity> commodityHashSet;
    //搜索结果的商品卡片
    private HashSet<CommodityCardBean> commodityCardHashSet;
    //服务与子线程通信，处理子线程返回的消息
    private static Handler serviceHandler;
    //活动与服务通信，发送消息给活动
    private Handler activityHandler;
    //一个是处理，一个是发送

    private HashSet<Character> contentChars;

    private MyBinder myBinder;


    @Override
    public void onCreate() {
        super.onCreate();

        //初始化实例域
        serviceHandler = new MyHandler(this);
        categoryHashSet = new HashSet<>();
        commodityHashSet = new HashSet<>();
        commodityCardHashSet = new HashSet<>();
        countList = new ArrayList<>();
        myBinder = new MyBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {

        activityHandler = SearchResultActivity.getHandler();
        return myBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //每次搜索/请求数据
        searchContentStr = intent.getStringExtra(SEARCH_CONTENT);
        splitContentStr();
        queryInCategory();

        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 查询符合搜索条件的商品分类
     */
    private synchronized void queryInCategory(){

        //每次查询先置false
        isPriSuccess = false;
        isSecSuccess = false;
        isSpecSuccess = false;

        /*
         * 查询大类别，结果在handler中接收
         */
        primaryCategorySet = new HashSet<>();
        QueryHelper<Category> queryHelper1 = QueryHelper.getInstance();
        //添加查询条件
        for(Character c : contentChars){
            queryHelper1.addOrContainsKey("primaryCategory", c.toString());
        }
        BmobQuery<Category> bmobQuery = queryHelper1.combineQueryList()
                .resultColumnKeys("objectId")
                .getBmobQuery();
        QueryHelper.test.queryFormServer(bmobQuery, serviceHandler, REQUEST_PRIMARY_CATEGORY);

        /*
         * 查询小类别
         */
        secondaryCategorySet = new HashSet<>();
        QueryHelper<Category> queryHelper2 = QueryHelper.getInstance();
        //添加查询条件
        for(Character c : contentChars){
            queryHelper2.addOrContainsKey("secondaryCategory", c.toString());
        }
//        queryHelper2.combineQueryList()
//                .resultColumnKeys("objectId")
//                .queryFormServer(serviceHandler, REQUEST_SECONDARY_CATEGORY);

        /*
         * 查询具体类别
         */
        specCategorySet = new HashSet<>();
        QueryHelper<Category> queryHelper3 = QueryHelper.getInstance();
        //添加查询条件
        for(Character c : contentChars){
            queryHelper3.addOrContainsKey("specificCategory", c.toString());
        }
//        queryHelper3.combineQueryList()
//                .resultColumnKeys("objectId")
//                .queryFormServer(serviceHandler, REQUEST_SPEC_CATEGORY);

    }

    /**
     * 拆分搜索内容字符串，（只取文字或英文结果）
     * @return
     */
    private void splitContentStr(){

        contentChars = new HashSet<>();

        char[] charArray = searchContentStr.toCharArray();
        for(int i = 0; i < charArray.length; i++){

            Character c = charArray[i];
            if(Character.isLetter(c)){
                //字母
                contentChars.add(c);
            }else if(String.valueOf(c).matches("[\u4e00-\u9fa5]")){
                //汉字
                contentChars.add(c);
            }
        }
    }

    /**
     * 在标题中查询商品（综合排序,按浏览量）
     */
    private void queryTitleInCommodity(){

        QueryHelper<Commodity> queryHelper = QueryHelper.getInstance();
        //在种类集查询后的条件下
        if(categoryHashSet.size() > 0){
            queryHelper.addAndContainedInKey("category", new ArrayList<>(categoryHashSet));
        }
        //添加查询条件
        for(Character c : contentChars){
            queryHelper.addOrContainsKey("title", c.toString());
        }
        queryHelper.combineQueryList();
        queryHelper.resultColumnKeys("title,price,pictureAndVideo,publishUser,objectId,browseCount");
        //以浏览量排序
        queryHelper.setOrder("browseCount");
        //内联查询用户
        queryHelper.setInclude("publishUser[username|headPortrait]");
//        queryHelper.queryFormServer(serviceHandler, REQUEST_TITLE_COMMODITY);
    }

    /**
     * 收藏量统计查询
     */
    private void queryCollections(){

        if(commodityHashSet.size() > 0){
            QueryHelper<Collection> queryHelper = QueryHelper.getInstance();
            queryHelper.addAndContainedInKey("collectCommodity", new ArrayList<>(commodityHashSet));
            queryHelper.combineQueryList();
            queryHelper.setGroupby(new String[]{"collectCommodity"});
            queryHelper.queryStatisticsFormServer(Collection.class, serviceHandler, REQUEST_COLLECTION_COMMODITY);
        }
    }

    /**
     * 所有数据查询完成后，开始合成商品卡片数据
     */
    private void compoundCardData(){
        Log.d(LOG_TAG, "commodity size: " + commodityHashSet.size());
        Log.d(LOG_TAG, "countList size: " + countList.size());

        if(commodityHashSet.size() == countList.size()){

            int i = 0;
            for(Commodity commodity : commodityHashSet){

                User user = commodity.getPublishUser();
                CommodityCardBean card = new CommodityCardBean(commodity.getPictureAndVideo().get(0).getUrl(),
                        commodity.getTitle(), commodity.getPrice(), countList.get(i),
                        user.getHeadPortrait().getUrl(), user.getUsername(), commodity.getObjectId());
                commodityCardHashSet.add(card);
                ++i;
            }

            //组装完成后，保存数据在Binder中,与活动通信
            myBinder.setResultCards(commodityCardHashSet);

            //通知活动数据加载完成，可以取出
            Message message = new Message();
            message.what = RESULT_COMPLETED;
            activityHandler.sendMessage(message);
        }
    }

    static class MyHandler extends Handler {

        WeakReference<CommodityCardSearchService> weakReference;

        public MyHandler(CommodityCardSearchService service){
            weakReference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CommodityCardSearchService service = weakReference.get();
            if(service != null){
                if(msg.what == QueryHelper.QUERY_HELPER_SUCCESS){

                    //查询成功，处理返回结果
                    switch (msg.arg1){
                        case REQUEST_PRIMARY_CATEGORY:
                            service.isPriSuccess = true;
                            service.primaryCategorySet.addAll((List<Category>)msg.obj);
                            break;
                        case REQUEST_SECONDARY_CATEGORY:
                            service.isSecSuccess = true;
                            service.secondaryCategorySet.addAll((List<Category>)msg.obj);
                            break;
                        case REQUEST_SPEC_CATEGORY:
                            service.isSpecSuccess = true;
                            service.specCategorySet.addAll((List<Category>)msg.obj);
                            break;
                        case REQUEST_TITLE_COMMODITY:
                            //查询商品成功
                            service.commodityHashSet.addAll((List<Commodity>)msg.obj);
                            //通过商品Id查询，收藏记录表获取收藏人数
                            service.queryCollections();
                            break;
                        case REQUEST_COLLECTION_COMMODITY:
                            Log.d(LOG_TAG, "商品收藏查询成功");
                            service.countList = (List<Integer>)msg.obj;
                            //开始组装商品卡片数据
                            service.compoundCardData();
                            break;

                        default:break;
                    }
                    //如果三个被别均查询成功
                    if(service.isPriSuccess && service.isSecSuccess && service.isSpecSuccess){

                        //取并集
                        service.categoryHashSet.addAll(service.primaryCategorySet);
                        service.categoryHashSet.retainAll(service.secondaryCategorySet);
                        service.categoryHashSet.retainAll(service.specCategorySet);

                        //在商品标题中查询
                        service.queryTitleInCommodity();

                    }
                }else if(msg.what == QueryHelper.QUERY_HELPER_FAILED){

                    //查询失败，处理返回结果
                    switch (msg.arg1){
                        case REQUEST_PRIMARY_CATEGORY:
                            service.isPriSuccess = false;
                            Log.d(LOG_TAG, "大类别查询失败");
                            break;
                        case REQUEST_SECONDARY_CATEGORY:
                            service.isSecSuccess = false;
                            Log.d(LOG_TAG, "小类别查询失败");
                            break;
                        case REQUEST_SPEC_CATEGORY:
                            service.isSpecSuccess = false;
                            Log.d(LOG_TAG, "具体类别查询失败");
                            break;
                        case REQUEST_TITLE_COMMODITY:
                            Log.d(LOG_TAG, "商品标题查询失败");
                            break;
                        case REQUEST_COLLECTION_COMMODITY:
                            Log.d(LOG_TAG, "商品收藏查询失败");
                            break;
                    }
                }

            }
        }
    }

    public class MyBinder extends Binder {

        private HashSet<CommodityCardBean> resultCards = null;//保存查询到的类别数据

        public HashSet<CommodityCardBean> getResultCards() {
            return resultCards;
        }

        public void setResultCards(HashSet<CommodityCardBean> resultCards) {
            this.resultCards = resultCards;
        }
    }
}
