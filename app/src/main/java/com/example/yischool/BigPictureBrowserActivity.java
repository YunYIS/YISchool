package com.example.yischool;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import Utils.FileUtils;

/**
 * @author 张云天
 * date on 2019/5/14
 * describe: 通用的图片预览器，预览图片或视频列表
 */
public class BigPictureBrowserActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String LOG_TAG = "BigPictureBrowser";

    public static final int REQUEST_CODE_PICTURE_BROWSER = 30;


    private ImageView backButton;
    private TextView indexTextView; //图片指示器
    private ImageView deleteButton;
    private ImageView editPictureButton; //编辑图片按钮
    private ViewPager viewPager;

    private boolean isChangePhotos = false;//记录是否改变了图片（删除或编辑）
    private int index;
    private List<File> photoFiles;
    private List<File> videoFiles;
    private List<View> viewList;//ViewPager数据源
    private MyViewPagerAdapter adapter;//ViewPager适配器


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_big_picture_browser);

        backButton = findViewById(R.id.back_button);
        indexTextView = findViewById(R.id.index_text_view);
        deleteButton = findViewById(R.id.delete_button);
        editPictureButton = findViewById(R.id.edit_picture_button);
        viewPager = findViewById(R.id.pictures_view_pager);
        //各个按钮点击事件
        backButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        editPictureButton.setOnClickListener(this);

        //初始化photoFiles
        photoFiles = new ArrayList<>();
        videoFiles = new ArrayList<>();
        //取出启动活动传递的数据
        Intent intent = getIntent();
        //图片浏览器是由自定义相机启动
        if(intent.hasExtra("CustomCamera_photoFiles")){

            index = 0;
            Object[] objects = (Object[])intent.getSerializableExtra("CustomCamera_photoFiles");
            //添加photoUris数据
            for(int i = objects.length-1; i >= 0; i--){//照相是最后一张展示在ImageView上，所以第一张从拍的最后一张开始浏览
                photoFiles.add((File)objects[i]);
            }
            //自定义相机图片只能预览，不能编辑图片，但是可以删除图片
            editPictureButton.setVisibility(View.GONE);

        } else {//说明图片浏览器是由PublishActivity启动的

            index = intent.getIntExtra("index", 0);
            if(intent.hasExtra("videoFiles")){
                Object[] objects = (Object[])intent.getSerializableExtra("videoFiles");
                for(int i = 0; i < objects.length; i++){
                    videoFiles.add((File)objects[i]);
                }
            }
            if(intent.hasExtra("photoFiles")){
                Object[] objects = (Object[])intent.getSerializableExtra("photoFiles");
                for(int i = 0; i < objects.length; i++){
                    photoFiles.add((File)objects[i]);
                }
            }
        }

        //初始化Viewpager数据源
        viewList = getPagerData();
        //设置ViewAdapter
        adapter = new MyViewPagerAdapter(viewList);
        viewPager.setAdapter(adapter);
        //进入页面时跳转至选择图片
        viewPager.setCurrentItem(index);
        //ViewPager滑动监听，更改指示器TextView
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                index = position;
                indexTextView.setText((index+1)+"/"+ viewList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });
        //页数指示器
        indexTextView.setText((index+1)+"/"+ viewList.size());
    }

    /**
     * 初始化ViewPager数据源(PhotoView列表)
     */
    private List<View> getPagerData(){

        List<View> list = new ArrayList<>();
        //View充满Pager
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        //有视频，加载视频View
        if(videoFiles != null && videoFiles.size() > 0){
            for (File file : videoFiles){
                View view = getVideoView(file);
                view.setLayoutParams(layoutParams);
                list.add(view);
            }
        }

        //有图片，加载图片View
        if(photoFiles != null && photoFiles.size() > 0){
            for(File file : photoFiles){
                PhotoView photoView = new PhotoView(BigPictureBrowserActivity.this);
                photoView.setLayoutParams(layoutParams);
                photoView.enable();//启动图片缩放功能
                //如果图片不存在，则显示预置的错误提示图
                Glide.with(BigPictureBrowserActivity.this).load(file)
                        .error(R.drawable.image_error).into(new MySimpleTarget(photoView));
                list.add(photoView);
            }
        }

        return list;
    }

    /**
     * 创建视频缩略图View（RelativeLayout：ImageView（缩略图,不能缩放） + ImageView（play icon））
     * @return
     */
    private View getVideoView(final File file){

        //子View布局参数
        RelativeLayout.LayoutParams relaLayoutParams1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        relaLayoutParams1.addRule(RelativeLayout.CENTER_IN_PARENT);//所有控件居中

        //布局容器
        RelativeLayout relativeLayout = new RelativeLayout(BigPictureBrowserActivity.this);
        //视频缩略图
        ImageView vidImageView = new ImageView(BigPictureBrowserActivity.this);
        vidImageView.setLayoutParams(relaLayoutParams1);
        Glide.with(BigPictureBrowserActivity.this).load(file).error(R.drawable.image_error).into(vidImageView);

        //icon
        ImageView iconImageView = new ImageView(BigPictureBrowserActivity.this);
        RelativeLayout.LayoutParams relaLayoutParams2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        relaLayoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);//所有控件居中
        //icon按钮的大小
        relaLayoutParams2.width = 150;
        relaLayoutParams2.height = 150;
        iconImageView.setLayoutParams(relaLayoutParams2);
        iconImageView.setImageResource(R.drawable.ic_video_gray);
        //icon点击事件
        iconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BigPictureBrowserActivity.this,VideoPlayerActivity.class);
                intent.putExtra("videoFile", file);
                startActivity(intent);
            }
        });

        relativeLayout.addView(vidImageView);
        relativeLayout.addView(iconImageView);
        return relativeLayout;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_button:
                //(setResult()方法要在finish之前调用，所以不能写在onDestroy中)
                result();
                finish();
                break;
            case R.id.delete_button:
                //删除文件
                isChangePhotos = true;

                //首先判断是视频文件还是图片文件
                if(viewList.get(index) instanceof RelativeLayout && videoFiles.size() > 0){
                    Log.d(LOG_TAG, "video delete");
                    //删除视频文件
                    if(viewList.size() > 1){
                        Log.d(LOG_TAG, "video index:" + index);
                        viewList.remove(index);//删除当前页面
                        videoFiles.remove(index);//删除数据
                        Log.d(LOG_TAG, "videoFiles size:" + videoFiles.size()+"viewList size:" + viewList.size());

                        //删除时，如果不是最后一页，则指向后一页
                        if(index == viewList.size()){
                            index = viewList.size()-1;
                        }
                        //修改指示器
                        indexTextView.setText((index+1)+"/"+ viewList.size());
                        //改变视图
                        adapter.notifyDataSetChanged();

                    }else{//图片浏览器中已无图片
                        videoFiles.remove(index);//删除最后一张图片数据
                        finish();//删除了所有图片，退出活动
                    }

                }else if(viewList.get(index) instanceof PhotoView && photoFiles.size() > 0){
                    //删除图片文件
                    if(viewList.size() > 1){
                        Log.d(LOG_TAG, "view index:" + index);
                        viewList.remove(index);//删除当前页面
                        photoFiles.remove(index-videoFiles.size());//删除数据
                        Log.d(LOG_TAG, "photo index:" + (index-videoFiles.size()));
                        Log.d(LOG_TAG, "photoFiles size:" + photoFiles.size()+"viewList size:" + viewList.size());

                        //删除时，如果不是最后一页，则指向后一页
                        if(index == viewList.size()){
                            index = viewList.size()-1;
                        }
                        //修改指示器
                        indexTextView.setText((index+1)+"/"+ viewList.size());
                        //改变视图
                        adapter.notifyDataSetChanged();
                    }else{//图片浏览器中已无图片
                        photoFiles.remove(index-videoFiles.size());//删除最后一张图片数据
                        finish();//删除了所有图片，退出活动
                    }
                }
                break;
            case R.id.edit_picture_button:
                //编辑按钮，如果是视频文件，则没有编辑功能
                if(videoFiles.size() > 0){
                    //查看当前文件是否为视频文件
                    if(viewList.get(index) instanceof RelativeLayout){
                        Toast.makeText(BigPictureBrowserActivity.this, "当前不支持视频编辑功能",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                //图片文件
                if(viewList.get(index) instanceof PhotoView){

                    //调用手机自带图片编辑器
                    Intent intent = new Intent("com.android.camera.action.CROP");

                    if(photoFiles.get(index-videoFiles.size()).exists()){
                        Log.d(LOG_TAG, "图片编辑index: "+(index-videoFiles.size()));
                        //Android7.0不能使用公开的file Uri提供给其他程序，要使用只能使用content Uri
                        Log.d(LOG_TAG, "图片编辑path: "+photoFiles.get(index-videoFiles.size()));
                        Uri photoUri = FileProvider.getUriForFile(BigPictureBrowserActivity.this,
                                "com.example.yischool.fileprovider", photoFiles.get(index-videoFiles.size()));
                        //设置图片类型，*表明所有类型的图片
                        intent.setDataAndType(photoUri, "image/*");
                        //设置显示的VIEW可裁剪
                        intent.putExtra("crop", "true");
                        //设置输出的格式
                        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                        //是否将数据保留在Bitmap中返回
                        intent.putExtra("return-data", true);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivityForResult(intent, REQUEST_CODE_PICTURE_BROWSER);
                    }
                }
                break;
            default:break;
        }
    }

    /**
     * 返回活动时返回photos改变的数据
     */
    private void result(){

        if(isChangePhotos){//图片/视频发生修改才返回数据
            Intent intent = new Intent();
            if(photoFiles != null && photoFiles.size() > 0){
                intent.putExtra("photoFiles", photoFiles.toArray());
            }
            if(videoFiles != null && videoFiles.size() > 0){
                intent.putExtra("videoFiles", videoFiles.toArray());
            }
            setResult(RESULT_OK, intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_CODE_PICTURE_BROWSER:
                    if(data != null){
                        Bitmap bitmap = data.getParcelableExtra("data");
                        //返回活动立即覆盖当前图片，采用修改以后的图片
                        final File file = photoFiles.get(index-videoFiles.size());
                        if(file.exists()){
                            Log.d(LOG_TAG, "已获取图片文件");
                            //将bitmap转换成byte[]，写入file
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            final byte[] bitmapData = baos.toByteArray();
                            FileUtils.createFile(file, bitmapData);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    while (true){
                                        if(file.length() == bitmapData.length){
                                            Log.d(LOG_TAG, "编辑图片文件写入完成,size: "+bitmapData.length/1024+"KB");
                                            Glide.with(BigPictureBrowserActivity.this)
                                                    .load(file)
                                                    .skipMemoryCache(true)//不使用缓存
                                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                                    .error(R.drawable.image_error)
                                                    .into(new MySimpleTarget((PhotoView)viewList.get(index)));
                                            //页面刷新
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    adapter.notifyDataSetChanged();
                                                }
                                            });
                                            break;
                                        }
                                    }
                                }
                            }).start();
                        }
                    }
                    break;
                default:break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        result();
        super.onBackPressed();

    }

    /**
     * Glide -> RoundImageView Target
     */
    class MySimpleTarget extends SimpleTarget<Drawable> {

        private PhotoView view;

        public MySimpleTarget(PhotoView view){
            this.view = view;
        }

        @Override
        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
            view.setImageDrawable(resource);
        }
        @Override
        public void onLoadFailed(@Nullable Drawable errorDrawable) {
            super.onLoadFailed(errorDrawable);
            Log.d("BigPictureBrowser", "onLoadFailed");
        }
    }

}

/**
 * ViewPager适配器
 */
class MyViewPagerAdapter extends PagerAdapter{

    public List<View> viewList;

    public MyViewPagerAdapter(List<View> viewList){
        this.viewList = viewList;
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}

