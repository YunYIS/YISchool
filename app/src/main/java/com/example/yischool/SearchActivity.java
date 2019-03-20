package com.example.yischool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.HashMap;
import java.util.HashSet;

import adapter.SearchListAdapter;
import searcheditview.EditTextView;

/**
 * @author 张云天
 * @date on 2019/3/19
 * @describe 搜索页面
 * 功能： 用户输入搜索内容，查看搜索历史，点击搜索，跳转搜索结果界面
 */
public class  SearchActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String SEARCH_CONTENT = "search_content_key";//以键值对的形式向搜索结果页面传递信息（key值）
    public static final String SHARED_PREFERENCE_NAME = "searchHistory";//sharedPreference文件名

    private ImageView backImgView;//返回按钮
    private EditTextView editTextView;//输入框
    private Button searchButton;//搜索按钮
    private ListView listView;//搜索历史列表
    private HashSet<String> historyData;//搜索历史数据源，由SharedPreferences提供，也可以由搜索按钮添加;
    private SearchListAdapter adapter;//自定义ListView适配器，主要为适配HashSet数据源
    private SharedPreferences sharedPreferences;//本地存储搜索历史记录
    private Intent startSearchResultActivity = new Intent(this, SearchResultActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        init();
    }
    /**
     * 初始化操作
     */
    private void init(){
        //获取控件实例
        backImgView = findViewById(R.id.back_img_button);
        editTextView = findViewById(R.id.search_edit_text);
        searchButton = findViewById(R.id.search_button);
        listView = findViewById(R.id.search_history_list);
        //注册点击事件
        backImgView.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        //从本地获取搜索历史记录，并用HashSet存储
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        HashMap<String,String> historyMap = (HashMap<String, String>) sharedPreferences.getAll();
        historyData = new HashSet<>();
        if(historyMap != null && historyMap.size() > 0){
            for(String s : historyMap.values()){
                historyData.add(s);
            }
        }

        adapter = new SearchListAdapter(this, historyData);//设置ListView适配
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String historyContentStr = (String)adapter.getItem(position);//获取history内容
                editTextView.setText(historyContentStr);
                editTextView.setSelection(historyContentStr.length());
                startSearchResultActivity.putExtra(SEARCH_CONTENT, historyContentStr);
                startActivity(startSearchResultActivity);
            }
        });
    }
    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_img_button:
                finish();
                break;
            case R.id.search_button:
                //保存搜索历史
                String editContent = editTextView.getText().toString();//搜索内容字符串
                Log.d("onClick edit", editContent);
                if(editContent != null && editContent.length() > 0){//搜索框内容不为空
                    startSearchResultActivity.putExtra(SEARCH_CONTENT, editContent);
                    startActivity(startSearchResultActivity);//跳转搜索结果界面
                    historyData.add(editContent);//由于是HashSet类型，所以数据不会重复
                    adapter.notifyDataSetChanged();
                }
                break;
            default:break;
        }
    }
    /**
     * 搜索活动销毁时，将新增的搜索历史记录存入本地
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(sharedPreferences != null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();//清空后再添加，避免循环比较数据是否重复，避免重复添加
            editor.commit();
            int i = 1;
            for(String content : historyData){
                editor.putString(""+i++, content);
            }
            editor.apply();
        }
    }
}
