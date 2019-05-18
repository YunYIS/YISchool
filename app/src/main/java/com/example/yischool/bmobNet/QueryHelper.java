package com.example.yischool.bmobNet;

import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

/**
 * @author 张云天
 * date on 2019/3/27
 * describe: 后台数据库查询帮助类
 * 调用必须按一定顺序 （如果有下列操作的话）
 * 1. 获取实例
 * 2. 添加 或 并 查询条件
 * 3. 合并条件，得到新的BmobQuery实例
 * 4. 添加查询结果限定条件
 * 5. 开始查询
 */
public class QueryHelper<T> {

    public static final String LOG_TAG = "QueryHelper";
    public static final int QUERY_HELPER_SUCCESS = 10;
    public static final int QUERY_HELPER_FAILED = 11;

    //复合查询，以or方式组合列表中的条件
    private List<BmobQuery<T>> orQueries;
    //复合查询，以and方式组合列表中的条件
    private List<BmobQuery<T>> andQueries;
    //组合后的最终查询实例
    private BmobQuery<T> bmobQuery;


    public static <T> QueryHelper<T> getInstance(){

        return new QueryHelper<>();
    }

    private QueryHelper(){
        bmobQuery = new BmobQuery<>();
        orQueries = new ArrayList<>();
        andQueries = new ArrayList<>();
    }

    public BmobQuery getBmobQuery(){
        return bmobQuery;
    }
    /**
     * 添加相等匹配查询 ，或 复合，keys的length必须和values的length相等
     * @param keys 列名s
     * @param values 匹配值s
     * @return
     */
    public QueryHelper addOrEqualKeys(String[] keys, String[] values){

        if(keys.length == values.length){
            for(int i = 0; i < keys.length; i++){
                addOrEqualKey(keys[i], values[i]);
            }
        }
        return this;
    }

    /**
     * 添加相等匹配查询，或 复合
     * @param key
     * @param value
     */
    public void addOrEqualKey(String key, String value){

        BmobQuery<T> query = new BmobQuery<>();
        query.addWhereEqualTo(key, value);
        orQueries.add(query);

    }

    /**
     * 添加相等匹配查询 ，并 复合，keys的length必须和values的length相等
     * @param keys 列名s
     * @param values 匹配值s
     * @return
     */
    public QueryHelper addAndEqualKeys(String[] keys, String[] values){

        if(keys.length == values.length){
            for(int i = 0; i < keys.length; i++){
                addAndEqualKey(keys[i], values[i]);
            }
        }
        return this;
    }

    /**
     * 添加相等匹配查询 ，并 复合
     * @param key
     * @param value
     */
    public void addAndEqualKey(String key, String value){

        BmobQuery<T> query = new BmobQuery<>();
        query.addWhereEqualTo(key, value);
        andQueries.add(query);
    }

    /**
     * 添加加字符串模糊包含匹配查询 ，或 复合，keys的length必须和values的length相等
     * @param keys 列名s
     * @param values 匹配值s
     * @return
     */
    public QueryHelper addOrContainsKeys(String[] keys, String[] values){

        if(keys.length == values.length){
            for(int i = 0; i < keys.length; i++){
                addOrContainsKey(keys[i], values[i]);
            }
        }
        return this;
    }

    /**
     * 添加加字符串模糊包含匹配查询 ，或 复合
     * @param key
     * @param value
     */
    public void addOrContainsKey(String key, String value){

        BmobQuery<T> query = new BmobQuery<>();
        query.addWhereContains(key, value);
        orQueries.add(query);
    }

    /**
     * 添加字符串模糊包含匹配查询 ，并 复合，keys的length必须和values的length相等
     * @param keys 列名s
     * @param values 匹配值s
     * @return
     */
    public QueryHelper addAndContainsKeys(String[] keys, String[] values){

        if(keys.length == values.length){
            for(int i = 0; i < keys.length; i++){
                addAndContainsKey(keys[i], values[i]);
            }
        }
        return this;
    }

    /**
     * 加字符串模糊包含匹配查询 ，并 复合
     * @param key
     * @param value
     */
    public void addAndContainsKey(String key, String value){

        BmobQuery<T> query = new BmobQuery<>();
        query.addWhereContains(key, value);
        andQueries.add(query);
    }

    /**
     * 子查询，查询一列匹配几个不同值的数据
     * @param key
     * @param valueList
     */
    public void addAndContainedInKey(String key, List valueList){

        BmobQuery<T> query = new BmobQuery<>();
        query.addWhereContainedIn(key, valueList);
        andQueries.add(query);
    }

    /**
     * 结合 or 条件List 和 and 条件List
     * @return
     */
    public QueryHelper combineQueryList(){

        if(andQueries.size() > 0){

            if(orQueries.size() > 0){
                BmobQuery<T> orQuery = new BmobQuery<T>().or(orQueries);
                andQueries.add(orQuery);
            }
            //最终组合实例
            bmobQuery = new BmobQuery<T>().and(andQueries);
        }else{
            bmobQuery = new BmobQuery<T>().or(orQueries);
        }
        return this;
    }

    /**
     * 开始查询数据
     * @param handler
     * @param requestCode //多线程区分返回的那个数据
     */
    public static void queryFormServer(BmobQuery bmobQuery, final Handler handler, final int requestCode){

        bmobQuery.findObjects(new FindListener() {
            @Override
            public void done(List list, BmobException e) {

                Message message = new Message();
                if(e==null){

                    Log.d(LOG_TAG,"查询成功："+list.size());
                    message.what = QUERY_HELPER_SUCCESS;
                    message.arg1 = requestCode;
                    message.obj = list;
                    handler.sendMessage(message);
                }else{

                    Log.d(LOG_TAG,"查询失败："+e.getMessage()+","+e.getErrorCode());
                    message.what = QUERY_HELPER_FAILED;
                    message.arg1 = requestCode;
                    handler.sendMessage(message);
                }
            }
        });
    }
    /**
     * 开始统计查询
     * @param handler
     * @param requestCode //多线程区分返回的那个数据
     */
    public void queryStatisticsFormServer(Class c, final Handler handler, final int requestCode){

        bmobQuery.findStatistics(c, new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray jsonArray, BmobException e) {

                Message message = new Message();
                if (e == null) {
                    Log.d(LOG_TAG, "查询成功：" + jsonArray.length());
                    Log.d(LOG_TAG, "jsonData:：" + jsonArray.toString());
                    List<Integer> countList = new ArrayList<>();
                    try {
                        for(int i = 0; i < jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            int count = jsonObject.getInt("_count");
                            countList.add(count);
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    //统计结束，发送消息，返回结果
                    message.what = QUERY_HELPER_SUCCESS;
                    message.arg1 = requestCode;
                    message.obj = countList;
                    handler.sendMessage(message);

                } else {

                    Log.d(LOG_TAG, "查询失败" + e.toString());
                    message.what = QUERY_HELPER_FAILED;
                    message.arg1 = requestCode;
                    handler.sendMessage(message);
                }
            }
        });
    }


    /**
     * 指定查询时返回的列
     * @param keys 指定多列时用,号分隔每列，如：resultColumnKeys("objectId,name,age");
     * @return
     */
    public QueryHelper<T> resultColumnKeys(String keys){

        bmobQuery.addQueryKeys(keys);
        return this;
    }

    /**
     * 分页加载，限制条数
     * @param number
     * @return
     */
    public QueryHelper setLimitNum(int number){
        bmobQuery.setLimit(number);
        return this;
    }

    /**
     * 排序方式
     * @param order  "score" score字段升序显示数据; "-score" score字段降序显示数据
     *               多个排序字段可以用（，）号分隔
     * @return
     */
    public QueryHelper setOrder(String order){

        bmobQuery.order(order);
        return this;
    }

    /**
     * 内联查询
     * @param columnName Pointer所关联的列名
     * @return
     */
    public QueryHelper setInclude(String columnName){

        bmobQuery.include(columnName);
        return this;
    }

    /**
     * 分组统计
     * @param columnName 以columnName[] 列名分组
     * @return
     */
    public QueryHelper setGroupby(String[] columnName){

        bmobQuery.groupby(columnName);
        //返回统计结果计数 "_count"
        bmobQuery.setHasGroupCount(true);
        return this;
    }

    public static class test{
        /**
         * 开始查询数据
         * @param handler
         * @param requestCode //多线程区分返回的那个数据
         */
        public static void queryFormServer(BmobQuery bmobQuery, final Handler handler, final int requestCode){

            bmobQuery.findObjects(new FindListener() {
                @Override
                public void done(List list, BmobException e) {

                    Message message = new Message();
                    if(e==null){

                        Log.d(LOG_TAG,"查询成功："+list.size());
                        message.what = QUERY_HELPER_SUCCESS;
                        message.arg1 = requestCode;
                        message.obj = list;
                        handler.sendMessage(message);
                    }else{

                        Log.d(LOG_TAG,"查询失败："+e.getMessage()+","+e.getErrorCode());
                        message.what = QUERY_HELPER_FAILED;
                        message.arg1 = requestCode;
                        handler.sendMessage(message);
                    }
                }
            });
        }
    }
}


