package com.example.yischool.publish;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yischool.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.example.yischool.Bean.ServerDatabaseBean.Category;
import com.example.yischool.Utils.ToastUtils;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

/**
 * @author 张云天
 * date on 2019/5/14
 * describe: 分类选择器
 */
public class ChooseCategoryActivity extends AppCompatActivity {

    public static final String LOG_TAG = "ChooseCategoryActivity";

    //查询指定列
    public static final String QUERY_COLUMN_PRIMARY = "primaryCategory";
    public static final String QUERY_COLUMN_SECONDARY = "secondaryCategory";


    private ImageView backButton;
    private ListView categoryListView;
    private ViewGroup includeLoadingLayout;//由include标签插入的正在加载布局（ProgressBar）
    private ViewStub viewStub;
    private RelativeLayout networkErrorLayout;//网络错误提示布局

    private List<Category> categoryList;//查询到的数据
    private List<String> listData;//ListView数据源
    private ArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);

        backButton = findViewById(R.id.back_button);
        categoryListView = findViewById(R.id.category_list_view);
        includeLoadingLayout = findViewById(R.id.loading_progressbar);
        viewStub = findViewById(R.id.network_error_view_stub);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //没有选择分类
                finish();
            }
        });
        //查询所有分类
        queryCategories();
    }

    /**
     * 查询所有二级分类
     */
    private void queryCategories(){

        BmobQuery<Category> categoryQuery = new BmobQuery<>();
        categoryQuery.groupby(new String[]{ QUERY_COLUMN_SECONDARY });
        categoryQuery.findStatistics(Category.class, new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray jsonArray, BmobException e) {
                if(e == null){
                    //查询成功
                    Log.d(LOG_TAG, "查询成功：jsonData:" + jsonArray.toString());
                    querySuccess(jsonArray);
                }else{
                    //查询失败
                    queryFailed();
                }
            }
        });
    }

    /**
     * 查询成功，界面操作及功能逻辑
     * @param jsonArray 查询返回数据
     */
    private void querySuccess(JSONArray jsonArray){

        JSONObject jsonObject = null;
        listData = new ArrayList<>();
        categoryList = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            try {
                jsonObject = jsonArray.getJSONObject(i);
                //组装categoryList，返回活动数据时用到
                String secondaryCategory = jsonObject.getString( QUERY_COLUMN_SECONDARY );
                Category category = new Category(null, secondaryCategory, null, null);
                categoryList.add(category);
                //组装ListView数据源，用于显示
                listData.add(secondaryCategory);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

        //数据查询成功，取消进度条
        includeLoadingLayout.setVisibility(View.GONE);//(已在主线程中)
        //设置ListView，及其点击事件
        adapter = new ArrayAdapter<String>(ChooseCategoryActivity.this, android.R.layout.simple_list_item_1,
                listData);
        categoryListView.setAdapter(adapter);
        //由于查询成功时，布局已经在完成，所以设置adapter后刷新ListView
        adapter.notifyDataSetChanged();

        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                queryResultFinish(position);
            }
        });

    }

    /**
     * 查询失败，界面操作
     */
    private void queryFailed(){

        //查询不成功，则提示网络错误信息
        includeLoadingLayout.setVisibility(View.GONE);

        try {//获取网络错误提示布局
            networkErrorLayout = (RelativeLayout) viewStub.inflate();//膨胀网络错误提示布局(此处错误提示布局可见)
        }catch (Exception e1){
            if(networkErrorLayout == null){
                networkErrorLayout = findViewById(R.id.network_error_inflateId);
            }
            networkErrorLayout.setVisibility(View.VISIBLE);//(以膨胀后的布局设置可见)
        }

        //获取网络错误提示布局中的控件，并添加点击事件(点击提示图片或文字)
        ImageView imageView = networkErrorLayout.findViewById(R.id.no_network_img);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewStub.setVisibility(View.GONE);//隐藏错误提示布局
                includeLoadingLayout.setVisibility(View.VISIBLE);//显示正在加载布局
                queryCategories();//重新查询
            }
        });
        TextView textView = networkErrorLayout.findViewById(R.id.no_network_text_button);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewStub.setVisibility(View.GONE);
                includeLoadingLayout.setVisibility(View.VISIBLE);
                queryCategories();
            }
        });
    }

    /**
     * 查询结果并返回
     * @param position
     */
    private void queryResultFinish(int position){

        //根据选择的小类别，查询所属的大类别
        BmobQuery<Category> categoryQuery = new BmobQuery<>();
        categoryQuery.addQueryKeys(QUERY_COLUMN_PRIMARY + "," + QUERY_COLUMN_SECONDARY);
        categoryQuery.addWhereEqualTo(QUERY_COLUMN_SECONDARY, categoryList.get(position).getSecondaryCategory());
        categoryQuery.findObjects(new FindListener<Category>() {
            @Override
            public void done(List<Category> list, BmobException e) {
                if(e==null){

                    finishAndResult(list.get(0));
                }else{
                    ToastUtils.toastMessage(ChooseCategoryActivity.this, "选择失败，请检查网络");
                }
            }
        });
    }

    /**
     * 向上个活动返回数据，并结束活动
     */
    private void finishAndResult(Category category){

        Intent intent = new Intent();
        intent.putExtra("choose_category", category);
        setResult(RESULT_OK,intent);
        finish();
    }
}
