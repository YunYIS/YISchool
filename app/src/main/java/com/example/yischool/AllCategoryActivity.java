package com.example.yischool;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.lang.ref.WeakReference;
import java.util.List;

import com.example.yischool.Bean.ServerDatabaseBean.Category;
import com.example.yischool.Utils.NetworkUtils;
import com.example.yischool.customview.ImgTextButton;
import com.example.yischool.service.AllCategoryService;

/**
 * @author 张云天
 * date on 2019/4/30
 * describe: 所有分类展示页
 * bug：在网络不好时，上一个大类别，未加载好，又点击了，另一个大类别，该大类别加载好了之后，上一个大类别加载成功，
 * 此时myBinder的categories就会更新，导致加载内容不符
 */
public class AllCategoryActivity extends AppCompatActivity {

    public static final String LOG_TAG = "AllCategoryActivity";
    private final String[] listViewData = new String[]{"热门推荐", "手机数码", "女装", "家用电器", "生活百货", "男装",
            "鞋类", "服饰配件", "图书", "二手车", "体育运动", "游戏", "家居", "玩具手办", "箱包", "技能服务"};
    public static MyHandler serviceHandler;//后台加载Service成功后发送Message提示刷新页面

    private ImageView backButton;
    private ListView listView;//左侧分类列表
    private LinearLayout particularCategoryLayout;//右侧详细分类展示
    private SelectedAdapter adapter;//分类列表ListView适配器
    private ViewGroup includeLoadingLayout;//由include标签插入的正在加载布局（ProgressBar）
    private ViewStub viewStub;
    private RelativeLayout networkErrorLayout;//网络错误提示布局

    private AllCategoryService.MyBinder myBinder;
    private ServiceConnection connection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_category);

        backButton = findViewById(R.id.back_button);
        listView = findViewById(R.id.category_name_recycler_view);
        particularCategoryLayout = findViewById(R.id.particular_category_layout);
        includeLoadingLayout = findViewById(R.id.loading_progressbar);
        viewStub = findViewById(R.id.network_error_view_stub);
        particularCategoryLayout = findViewById(R.id.particular_category_layout);
        //返回按钮
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //左侧ListView大类别表
        adapter = new SelectedAdapter(this, listViewData);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, "whether execute OnItemClick() at firstly start Activity");
                adapter.changeSelectedState(position);
                inflateView(listViewData[position]);
            }
        });
        //绑定服务
        connection = new ServiceConnection(){
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

                myBinder = (AllCategoryService.MyBinder)service;
            }
            @Override
            public void onServiceDisconnected(ComponentName name) { }
        };
        Intent bindIntent = new Intent(AllCategoryActivity.this, AllCategoryService.class);
        bindService(bindIntent,connection,BIND_AUTO_CREATE);
        serviceHandler = new MyHandler(AllCategoryActivity.this);

        inflateView(listViewData[0]);
    }

    /**
     * 判断网络连接状态，加载不同的布局(每次点击ListView item选择不同大类别，也调用该方法)
     * @param primaryCategory 根据不同的大类别加载不同的内容页面
     */
    private void inflateView(final String primaryCategory){
        if(!NetworkUtils.isNetworkConnected(this)){//如果没有网络连接，显示提示布局
            includeLoadingLayout.setVisibility(View.GONE);//隐藏ProgressBar
            particularCategoryLayout.removeAllViews();
            try {//获取网络错误提示布局
                networkErrorLayout = (RelativeLayout) viewStub.inflate();//膨胀网络错误提示布局(此处错误提示布局可见)
            }catch (Exception e){
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
                    inflateView(primaryCategory);//重新加载
                }
            });
            TextView textView = networkErrorLayout.findViewById(R.id.no_network_text_button);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewStub.setVisibility(View.GONE);
                    includeLoadingLayout.setVisibility(View.VISIBLE);
                    inflateView(primaryCategory);
                }
            });


        }else {//网络连接正常，加载数据和内容界面（由primaryCategory判断加载那些数据）
            particularCategoryLayout.removeAllViews();
            if(includeLoadingLayout.getVisibility() == View.GONE){
                includeLoadingLayout.setVisibility(View.VISIBLE);
            }
            if(networkErrorLayout != null){
                if(networkErrorLayout.getVisibility() == View.VISIBLE){
                    networkErrorLayout.setVisibility(View.GONE);
                }
            }
            Intent intent = new Intent(AllCategoryActivity.this, AllCategoryService.class);
            intent.putExtra("primaryCategory", primaryCategory);
            startService(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解绑服务
        if(connection != null){
            unbindService(connection);
        }
    }

    /**
     * 处理Service发送的加载状态Message
     */
    public static class MyHandler extends Handler{

        WeakReference<AllCategoryActivity> weakReference;
        MyHandler(AllCategoryActivity activity){
            weakReference = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AllCategoryActivity activity = weakReference.get();
            if(activity != null){
                switch (msg.what){
                    case AllCategoryService.MyBinder.QUERY_STATUS_SUCCESS://加载成功
                        if(activity.includeLoadingLayout.getVisibility() == View.VISIBLE){
                            activity.includeLoadingLayout.setVisibility(View.GONE);
                        }

                        inflateContentView(activity);
                        break;
                    case AllCategoryService.MyBinder.QUERY_STATUS_FAILURE://加载失败
                        if(activity.includeLoadingLayout.getVisibility() == View.VISIBLE){
                            activity.includeLoadingLayout.setVisibility(View.GONE);
                        }
                        activity.networkErrorLayout.setVisibility(View.VISIBLE);
                        break;
                    default:break;
                }
            }

        }
        /**
         * 加载内容页面
         * @param activity
         */
        private static void inflateContentView(final AllCategoryActivity activity){

            activity.particularCategoryLayout.removeAllViews();
            int contentWidth = activity.particularCategoryLayout.getMeasuredWidth();//布局宽度（三列）
            int iconWH = contentWidth / 5;//每列图片宽度和高度
            List<Category> categories = activity.myBinder.getCategories();

            if(categories.size() > 0){

                String secondaryCategory = categories.get(0).getSecondaryCategory();//小类别标题

                //添加小类别标题 横排显示文字控件
                TextView titleTextView = new TextView(activity);
                titleTextView.setText(secondaryCategory);//设置小类别标题内容
                titleTextView.setTextColor(Color.BLACK);
                titleTextView.setBackgroundResource(R.drawable.underline_background);//设置标题背景（View下划线）
                LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textLayoutParams.setMargins(50, 50, 50,50);
                titleTextView.setLayoutParams(textLayoutParams);//垂直排列，占据一行
                activity.particularCategoryLayout.addView(titleTextView);

                //小标题下的tableLayout，用于显示每个具体类别
                TableLayout tableLayout = new TableLayout(activity);
                LinearLayout.LayoutParams tabLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                tabLayoutParams.setMargins(contentWidth/20, 0, contentWidth/20, 35);
                tableLayout.setLayoutParams(tabLayoutParams);
                TableRow tableRow = null;//tableLayout行对象
                int column = 0;//记录ImgTextButton数（保持每行小于等于三个）

                for(int i = 0; i < categories.size(); i++){

                    Category category = categories.get(i);

                    //更换小类别标题，更换TableLayout，添加横排标题和表格布局
                    if(!secondaryCategory.equals(category.getSecondaryCategory())){

                        activity.particularCategoryLayout.addView(tableLayout);//添加tableLayout
                        tableLayout = new TableLayout(activity);
                        tableLayout.setLayoutParams(tabLayoutParams);
                        column = 0;//重新计数（换行，即新的TableRow对象）
                        secondaryCategory = category.getSecondaryCategory();

                        //添加小类别标题 横排显示文字控件
                        titleTextView = new TextView(activity);
                        titleTextView.setText(secondaryCategory);//设置小类别标题内容
                        titleTextView.setTextColor(Color.BLACK);
                        titleTextView.setBackgroundResource(R.drawable.underline_background);//设置标题背景（View下划线）
                        titleTextView.setLayoutParams(textLayoutParams);
                        activity.particularCategoryLayout.addView(titleTextView);
                    }

                    //添加表格布局的子项
                    if(column % 3 == 0){//tableLayout 换行（三个View为一行）
                        tableRow = new TableRow(activity);//每个行对象有三个子View
                        tableLayout.addView(tableRow);
                    }
                    ImgTextButton imgTextButton = new ImgTextButton(activity);//表格中每个子View就是一个ImgTextButton
                    imgTextButton.setText(category.getSpecificCategory());
                    imgTextButton.setImageScaleType(ImageView.ScaleType.FIT_CENTER);//设置图片显示缩放方式
                    imgTextButton.setImageWH(iconWH);
                    imgTextButton.setImageMargins(contentWidth/20, 30, contentWidth/20,30);
                    loadImgIntoCustomView(activity, category.getCommodityIconUrl(),imgTextButton);//从网络加载图片到ImgTextButton
                    tableRow.addView(imgTextButton);
                    Log.d(LOG_TAG, "secondaryCategory:" +secondaryCategory);
                    column++;//ImgTextButton记录个数
                }
                activity.particularCategoryLayout.addView(tableLayout);//收尾
            }
        }

        /**
         * 网络加载图片，再显示到自定义View
         * @param context
         * @param url
         * @param imgTextButton 显示View
         */
        private static void loadImgIntoCustomView(Context context, String url, final ImgTextButton imgTextButton){

            //自定义Target，用于将网络加载好的图片设置进自定义View
            Target<Drawable> target = new CustomTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    imgTextButton.setImage(resource);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }

                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    super.onLoadFailed(errorDrawable);
                    Log.d(LOG_TAG, "CustomViewTarget onLoadFailed() ");
                }
            };
            //加载网络图片
            Glide.with(context).load(url).into(target);
        }
    }
}
/**
 * ListView Adapter,选中的item的背景和文字颜色不同
 */
class SelectedAdapter extends ArrayAdapter{

    private int selectedPosition = 0; //选中项
    private String[] listViewData;

    public SelectedAdapter(@NonNull Context context, @NonNull Object[] objects) {
        super(context,R.layout.simple_list_item, objects);
        listViewData = (String[]) objects;
    }

    public void changeSelectedState(int position){
        if(selectedPosition != position){
            selectedPosition = position;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return listViewData.length;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        @SuppressLint("ViewHolder")
        View v = LayoutInflater.from(getContext()).inflate(R.layout.simple_list_item, null,false);
        TextView textView = v.findViewById(R.id.text1);
        textView.setText(listViewData[position]);
        if(position == selectedPosition){
            textView.setBackgroundResource(R.color.colorPrimary);
            textView.setTextColor(Color.WHITE);
        }else{
            textView.setBackgroundResource(R.color.white);
            textView.setTextColor(Color.BLACK);
        }
        return v;
    }
}
