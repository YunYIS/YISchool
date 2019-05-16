package com.example.yischool.publish;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.example.yischool.R;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.example.yischool.Bean.ServerDatabaseBean.AddressDetail;
import com.example.yischool.Bean.jsonBean.AreaBean.City;
import com.example.yischool.Bean.jsonBean.AreaBean.District;
import com.example.yischool.Bean.jsonBean.AreaBean.Result;
import com.example.yischool.Utils.HttpUtils;
import com.example.yischool.Utils.JSONUtils;
import com.example.yischool.Utils.LBSUtils;
import com.example.yischool.Utils.ToastUtils;
import cn.bmob.v3.datatype.BmobGeoPoint;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.yischool.Utils.LBSUtils.ADDRESS_TO_POINT_HANDLER_WHAT;
import static com.example.yischool.Utils.LBSUtils.addressToGeoPoint;
import static com.example.yischool.Utils.LBSUtils.startLocate;

public class ChoosePositionActivity extends AppCompatActivity {

    public static final String LOG_TAG = "ChoosePositionActivity";

    public static final String URL = "http://zhouxunwang.cn/data/?id=104&key=BujCq9JiH9v+h5qM+o4xR2bJOQTgsJeZ/pxz7fk&ske=1";

    //当前选择位置级别
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_DISTRICT = 2;

    private ImageView backButton;
    private ListView listView;
    private LinearLayout locationView;//ListView 的headerView
    private TextView locationText;//显示GPS定位后的地址
    private ViewGroup includeLoadingLayout;//由include标签插入的正在加载布局（ProgressBar）
    private ViewStub viewStub;
    private RelativeLayout networkErrorLayout;//网络错误提示布局

    private List<Result> areaList;//全国省市县数据
    private AddressDetail area;//用户选择的位置
    private ArrayAdapter adapter;//ListView适配器

    private static Handler handler;//接收百度定位消息

    //ListView 数据源
    private List<String> listData;
    //省列表
    private List<String> provinceList;
    //市列表
    private List<String> cityList;
    //县列表
    private List<String> districtList;
    //选中的省份
    private String selectedProvince;
    //选中的市
    private String selectedCity;
    //当前选中的级别
    private int currentLevel = LEVEL_PROVINCE;
    //是否定位成功
    private boolean isLocateSuccess = false;
    private LocationClient locationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_position);

        backButton = findViewById(R.id.back_button);
        listView = findViewById(R.id.location_list_view);
        includeLoadingLayout = findViewById(R.id.loading_progressbar);
        viewStub = findViewById(R.id.network_error_view_stub);
        //添加HeaderView
        locationView = (LinearLayout) LayoutInflater.from(ChoosePositionActivity.this).inflate(R.layout.location_text_layout, null);
        locationText = locationView.findViewById(R.id.location_text_view);
        locationText.setText("获取位置信息");
        listView.addHeaderView(locationView);

        listData = new ArrayList<>();
        adapter = new ArrayAdapter<String>(ChoosePositionActivity.this, android.R.layout.simple_list_item_1, listData);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLevel == LEVEL_PROVINCE){
                    finish();

                }else if(currentLevel == LEVEL_CITY){
                    currentLevel = LEVEL_PROVINCE;
                    refreshListView(getProvinces());

                }else if(currentLevel == LEVEL_DISTRICT){
                    currentLevel = LEVEL_CITY;
                    refreshListView(getCities(area.getProvince()));
                }
            }
        });

        handler = new MyHandler(ChoosePositionActivity.this);

        //查询 省市县 名称数据
        areaList = new ArrayList<>();
        area = new AddressDetail();
        queryData(URL);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0){
                    //点击的是HeaderView,即自动定位
                    if(isLocateSuccess){
                        if(locationClient != null){
                            locationClient.stop();
                        }
                        //定位成功
                        finishAndResult();
                    }else {
                        showProgress(false, null);
                        //启动定位
                        locationClient = startLocate(getApplicationContext(), handler);
                    }

                }else {
                    if(currentLevel == LEVEL_PROVINCE){
                        area.setProvince(listData.get(position-1));
                        refreshListView(getCities(area.getProvince()));
                        currentLevel = LEVEL_CITY;

                    }else if(currentLevel == LEVEL_CITY){
                        area.setCity(listData.get(position-1));
                        refreshListView(getDistrict(area.getProvince(),area.getCity()));
                        currentLevel = LEVEL_DISTRICT;

                    }else if(currentLevel == LEVEL_DISTRICT){
                        //最后区县选择之后，尝试获取坐标点，如果获取成功，则保存退出，没有则直接退出
                        showProgress(false, null);
                        area.setDistrict(listData.get(position-1));
                        //开始地址转换坐标
                        addressToGeoPoint(area.getDistrict(), null, handler);
                    }
                }
            }
        });
    }

    /**
     * 刷新ListView
     * @param list 数据源
     */
    private void refreshListView(List list){

        listData.clear();
        listData.addAll(list);

        adapter.notifyDataSetChanged();

    }

    /**
     * 查询省市区名称数据
     * @param address URL
     */
    private void queryData(String address) {

        HttpUtils.sendOkHttpRequest(address, new Callback(){
            @Override
            public void onResponse(Call call, Response response)throws IOException {

                String responseText = response.body().string();
                areaList = handleJsonData(responseText);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //数据查询成功，取消进度条
                        includeLoadingLayout.setVisibility(View.GONE);
                        refreshListView(getProvinces());
                    }
                });

            }
            @Override
            public void onFailure(Call call, IOException e){
                //通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadFailed();
                    }
                });
            }
        });
    }

    /**
     * 处理网络返回 省市县 的json数据
     * @param data json字符串
     * @return areaList
     */
    private List<Result> handleJsonData(String data){

        List<Result> areaList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            areaList = JSONUtils.resolveJsonArray(new TypeToken<List<Result>>(){}.getType(), jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return areaList;
    }

    /**
     * 获取全国所有的省
     */
    private List<String> getProvinces(){

        List<String> provinces = new ArrayList<>();
        for(Result result : areaList){
            provinces.add(result.getProvince());
        }
        return provinces;
    }

    /**
     * 获取选中 省内 所有的市
     */
    private List<String> getCities(String provinceName) {

        List<String> citys = new ArrayList<>();
        for(Result result:areaList){
            if(result.getProvince().equals(provinceName)){
                for(City city : result.getCity()){
                    citys.add(city.getCity());
                }
                break;
            }
        }
        return citys;
    }

    /**
     * 获取选中 省市内 所有的县
     */
    private List<String> getDistrict(String provinceName, String cityName) {

        List<String> districts = new ArrayList<>();
        for(Result result:areaList){

            if(result.getProvince().equals(provinceName)){
                for(City city : result.getCity()){

                    if(city.getCity().equals(cityName)){
                        for (District district : city.getDistrict()){
                            districts.add(district.getDistrict());
                        }
                        break;
                    }
                }
                break;
            }
        }
        return districts;
    }

    /**
     * 加载失败，界面操作
     */
    private void loadFailed(){

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
                queryData(URL);//重新查询
            }
        });
        TextView textView = networkErrorLayout.findViewById(R.id.no_network_text_button);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewStub.setVisibility(View.GONE);
                includeLoadingLayout.setVisibility(View.VISIBLE);
                queryData(URL);
            }
        });
    }

    /**
     * 修改返回按钮点击事件
     */
    @Override
    public void onBackPressed() {
        if(currentLevel == LEVEL_PROVINCE){
            finish();
        }else if(currentLevel == LEVEL_CITY){
            currentLevel = LEVEL_PROVINCE;
            refreshListView(getProvinces());
        }else if(currentLevel == LEVEL_DISTRICT){
            currentLevel = LEVEL_CITY;
            refreshListView(getCities(area.getProvince()));
        }
    }

    /**
     * 加载进度提示
     * @param message 提示信息
     * @param cancelable 是否可与界面交互
     */
    private void showProgress(boolean cancelable, String message){

        if(includeLoadingLayout.getVisibility() == View.GONE){
            includeLoadingLayout.setVisibility(View.VISIBLE);
        }
        //修改提示信息
        TextView view = includeLoadingLayout.findViewById(R.id.load_hint);
        if(message == null){
            if(view.getVisibility() == View.VISIBLE){
                view.setVisibility(View.GONE);
            }
        }else{
            if(view.getVisibility() == View.GONE){
                view.setVisibility(View.VISIBLE);
            }
            view.setText(message);
        }
        //是否不可取消
        if(!cancelable){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    /**
     * 取消加载提示
     */
    private void closeProgress(){

        if(includeLoadingLayout.getVisibility() == View.VISIBLE){
            includeLoadingLayout.setVisibility(View.GONE);
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    /**
     * 取消加载提示
     */
    private void closeProgress(ChoosePositionActivity activity){

        if(activity.includeLoadingLayout.getVisibility() == View.VISIBLE){
            activity.includeLoadingLayout.setVisibility(View.GONE);
        }
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    /**
     * 返回数据并退出活动
     */
    private void finishAndResult(){
        Intent intent = new Intent();
        intent.putExtra("publish_position", area);
        setResult(RESULT_OK, intent);
        finish();
    }

    static class MyHandler extends Handler {

        WeakReference<ChoosePositionActivity> weakReference;

        public MyHandler(ChoosePositionActivity activity){
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ChoosePositionActivity activity = weakReference.get();
            if(activity != null){
                switch (msg.what){
                    case LBSUtils.LOCATE_HANDLER_WHAT:
                        //先关闭进度条
                        activity.closeProgress(activity);

                        if(msg.obj != null){
                            //定位成功，返回一个BDLocation
                            setLBSAddress(activity, (BDLocation)msg.obj);
                            activity.locationText.setText(activity.area.getProvince()
                                    +","+activity.area.getCity()+","+activity.area.getDistrict());
                            activity.isLocateSuccess = true;
                        }else{
                            //定位失败
                            ToastUtils.toastMessage(activity, "定位失败，请检查GPS和网络连接");
                        }
                        break;
                    case ADDRESS_TO_POINT_HANDLER_WHAT:
                        //if obj == null 则转换失败，但是由于并不是一定需要坐标点，所以一样可以退出
                        if(msg.obj != null){
                            activity.area.setPoint((BmobGeoPoint) msg.obj);
                        }
                        activity.closeProgress(activity);
                        activity.finishAndResult();
                        break;
                    default:break;
                }
            }
        }

        /**
         * 设置Address类
         * @param location
         */
        private void setLBSAddress(ChoosePositionActivity activity, BDLocation location){

            //设置经纬度
            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息
            BmobGeoPoint geoPoint = new BmobGeoPoint(longitude, latitude);
            activity.area.setPoint(geoPoint);
            //设置省/市/区县
            activity.area.setProvince(location.getProvince());
            activity.area.setCity(location.getCity());
            activity.area.setDistrict(location.getDistrict());
            //设置详细地址信息
            Log.d(LOG_TAG, "LBS详细地址："+location.getAddrStr());
            activity.area.setDescribe(location.getAddrStr());
        }
    }
}

