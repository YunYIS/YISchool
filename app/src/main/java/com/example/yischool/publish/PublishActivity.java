package com.example.yischool.publish;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.yischool.BigPictureBrowserActivity;
import com.example.yischool.CustomCameraActivity;
import com.example.yischool.InitApplication;
import com.example.yischool.R;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import org.raphets.roundimageview.RoundImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.example.yischool.Bean.ServerDatabaseBean.AddressDetail;
import com.example.yischool.Bean.ServerDatabaseBean.Category;
import com.example.yischool.Bean.ServerDatabaseBean.Commodity;
import com.example.yischool.Utils.FileUtils;
import com.example.yischool.Utils.Glide4Engine;
import com.example.yischool.Utils.ToastUtils;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

import static com.example.yischool.Bean.ServerDatabaseBean.Commodity.MEANS_EXCHANGE_FACE;
import static com.example.yischool.Bean.ServerDatabaseBean.Commodity.MEANS_EXCHANGE_MAIL;
import static com.example.yischool.Bean.ServerDatabaseBean.Commodity.MEANS_EXCHANGE_TAKE;

/**
 * @author 张云天
 * date on 2019/5/9
 * describe: 发布商品活动
 */
public class PublishActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String LOG_TAG = "PublishActivity";

    //提示对话框的类型
    public static final int ALERT_TYPE_TWO = 2;//两个按钮类型
    public static final int ALERT_TYPE_FOUR = 4;//四个按钮

    //requestCode(启动相册，拍照，视频，摄像)
    public static final int REQUEST_CODE_ALBUM = 1;
    public static final int REQUEST_CODE_CAMERA = 2;
    public static final int REQUEST_CODE_VIDEO = 3;
    public static final int REQUEST_CODE_VIDICON = 4;
    //requestCode(大图片浏览活动，编辑器)
    public static final int REQUEST_CODE_BIG_PICTURE = 5;
    //requestCode 分类选择器
    public static final int REQUEST_CODE_CHOOSE_CATEGORY = 6;
    //requestCode 定位
    public static final int REQUEST_CODE_CHOOSE_POSITION = 7;



    private ImageView backButton;//取消按钮
    private EditText titleEditText;//标题
    private EditText contentEditText;//商品详情
    private TableLayout pictureTableLayout;//图片视频布局(网格布局)
    private EditText priceEditText;//价格（只允许输入数字）
    private EditText categoryEditText;//分类（多级选择按钮）
    private EditText brandEditText;//品牌
    private ViewGroup locationLayout;//定位布局
    private TextView locationTextView;//定位按钮（多级选择或自动定位）
    // 交易方式
    private CheckBox takeCheck;
    private CheckBox faceCheck;
    private CheckBox mailCheck;
    private Button completePublishButton;//确认发布
    private RoundImageView addPhotoVideoButton;//添加图片或视频按钮（代码动态加入）
    private ProgressDialog progressDialog;

    private List<File> photoFiles = new ArrayList<>();//用户上传(或拍摄)的图片或视频缩略图展示（最多11张）
    private File videoFile;//视频文件只能有一个
    private Commodity commodity = new Commodity();//待上传的商品
    private Category category;//商品选择器活动返回的商品分类
    private AddressDetail addressDetail;//选择定位器返回位置信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        backButton = findViewById(R.id.back_button);
        titleEditText = findViewById(R.id.title_edit_text);
        contentEditText = findViewById(R.id.content_edit_text);
        pictureTableLayout = findViewById(R.id.picture_grid_view);
        priceEditText = findViewById(R.id.price_edit_text);
        categoryEditText = findViewById(R.id.category_edit_text);
        brandEditText = findViewById(R.id.brand_edit_text);
        locationTextView =  findViewById(R.id.location_text_view);
        takeCheck = findViewById(R.id.take_check);
        faceCheck = findViewById(R.id.face_check);
        mailCheck = findViewById(R.id.mail_check);
        completePublishButton = findViewById(R.id.complete_publish_button);
        locationLayout = findViewById(R.id.location_layout);

        //由父布局宽度计算tableLayout布局每个子项宽度，所以在代码中添加addPhotoVideoButton
        addPhotoVideoButton = new RoundImageView(this);
        //设置addPhotoVideoButton一些布局参数，由于addButton始终存在，所以不在inflateTab方法中重复设置
        addPhotoVideoButton.setType(RoundImageView.TYPE_ROUND);
        addPhotoVideoButton.setCornerRadius(10);
        addPhotoVideoButton.setImageResource(R.drawable.add_photo_video);
        //添加addPhotoVideoButton点击事件
        addPhotoVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果存在视频文件，那么就不能再选择视频或拍摄视频
                if(videoFile != null){
                    showAlertDialog(ALERT_TYPE_TWO);
                }else{
                    showAlertDialog(ALERT_TYPE_FOUR);
                }
            }
        });
        //添加addPhotoVideoButton，以及选择后的图片布局
        inflateTableLayout();

        completePublishButton.setOnClickListener(this);
        categoryEditText.setOnClickListener(this);
        locationLayout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.complete_publish_button://完成上传点击事件
                    //组装商品数据
                    boolean ret = setCommodityData();
                    if(ret){
                        //视频和图片在上传时处理
                        //开始上传商品数据
                        showProgressDialog();
                        uploadCommodity();
                    }
                break;
            case R.id.category_edit_text:
                Intent intent = new Intent(PublishActivity.this, ChooseCategoryActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CHOOSE_CATEGORY);
                break;
            case R.id.location_layout:
                Intent intent1 = new Intent(PublishActivity.this, ChoosePositionActivity.class);
                startActivityForResult(intent1, REQUEST_CODE_CHOOSE_POSITION);
                break;
            case R.id.back_button:
                //@TODO 保存草稿
                finish();
                break;
            default:break;
        }
    }

    /**
     * 设置商品数据
     */
    private boolean setCommodityData() {

        //是否填写完成
        boolean isTure = false;
        //获取填写数据
        String titleStr = getTitleStr();
        String contentStr = getContentStr();
        Double price = getPrice();
        String brand = getBrandStr();
        int meansExchange = getMeansOfExchange();

        if(titleStr == null){
            ToastUtils.toastMessage(PublishActivity.this,"请填写标题");
        }else if(contentStr == null){
            ToastUtils.toastMessage(PublishActivity.this,"请填写商品描述内容");
        }else if(price == null){
            ToastUtils.toastMessage(PublishActivity.this,"请填写价格");
        }else if(meansExchange == 0){
            ToastUtils.toastMessage(PublishActivity.this,"请选择交易方式");
        }else if(category == null){
            ToastUtils.toastMessage(PublishActivity.this,"请选择商品分类");
        }else if(addressDetail == null){
            ToastUtils.toastMessage(PublishActivity.this,"请选择发布位置");
        }else if(videoFile == null && photoFiles.size() == 0) {
            ToastUtils.toastMessage(PublishActivity.this,"至少上传一个视频或图片");
        }else {
            //至此，以完整填写所有必填项
            //开始设置发布商品数据
            commodity.setTitle(titleStr)
                    .setDetail(contentStr)
                    .setPublishUser(InitApplication.getCurrentUser())
                    .setPublishPosition(addressDetail)
                    .setCategory(category)
                    .setPrice(price)
                    .setBrand(brand)
                    .setMeansOfExchange(meansExchange)
                    .setSoldOut(false);

            isTure = true;
        }

        return isTure;
    }
    /**
     * 获取交易方式
     * @return
     */
    private int getMeansOfExchange(){
        int retMeans = 0;
        if(takeCheck.isChecked()){
            retMeans += MEANS_EXCHANGE_TAKE;
        }
        if(faceCheck.isChecked()){
            retMeans += MEANS_EXCHANGE_FACE;
        }
        if(mailCheck.isChecked()){
            retMeans += MEANS_EXCHANGE_MAIL;
        }
        return retMeans;
    }

    /**
     * 获取品牌
     * @return
     */
    private String getBrandStr(){
        Editable editable = brandEditText.getText();
        String brandStr = null;
        if(editable != null){
            brandStr = editable.toString();
        }
        return brandStr;
    }

    /**
     * 获取价格
     * @return
     */
    private Double getPrice(){
        Editable editable = priceEditText.getText();
        Double price = null;
        if(editable != null){
            String priceStr = editable.toString();
            if(priceStr.length() > 0){
                price = Double.valueOf(priceStr);
            }
        }
        return price;
    }
    /**
     * 获取商品标题
     * @return
     */
    private String getTitleStr(){
        Editable editable = titleEditText.getText();
        String titleStr = null;
        if(editable != null){
            titleStr = editable.toString();
        }
        return titleStr;
    }

    /**
     * 获取商品详细信息内容
     * @return
     */
    private String getContentStr(){
        Editable editable = contentEditText.getText();
        String contentStr = null;
        if(editable != null){
            contentStr = editable.toString();
        }
        return contentStr;
    }

    /**
     * 获取屏幕宽度
     * @return
     */
    private int getScreenWidth(){
        
        //获取屏幕宽度
        WindowManager windowManager = this.getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }
    /**
     * 根据屏幕宽度计算展示图片的宽==高
     * @return
     */
    private int getImageWH(){
        
        //获取屏幕宽度
        int screenWidth = getScreenWidth();
        //展示图片宽度
        int imageWH = screenWidth / 5;
        return imageWH;
    }

    /**
     * 根据屏幕宽度计算展示图片的间距(左间距==右间距)
     * @return
     */
    private int getImageMargin(){
        
        //获取屏幕宽度
        int screenWidth = getScreenWidth();
        //展示图片宽度
        int imageWH = getImageWH();
        //父布局与屏幕左间距==右
        int parentMargin = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20,getResources().getDisplayMetrics());
        //计算展示图片的间距
        int margin = (screenWidth - 4 * imageWH - 2 * parentMargin)/8;
        return margin;
    }

    /**
     * 加载图片展示布局
     */
    private void inflateTableLayout(){

        pictureTableLayout.removeAllViews();//刷新TableLayout

        //tableLayout中每个子项所占宽度，每行四列
        int itemWH = getImageWH();
        //每个子项左==右间距
        int itemMargin = getImageMargin();
        //设置每个子项宽高参数
        TableRow.LayoutParams tabItemLayoutParams = new TableRow.LayoutParams(itemWH,itemWH);
        //设置每个子项四面间距
        tabItemLayoutParams.setMargins(itemMargin, 30, itemMargin, 30);


        //开始添加商品展示view
        TableRow tableRow = null;
        int count = 0;//计数

        //添加商品的视频缩略图
        if(videoFile != null){
            //Glide也支持加载视频缩略图
            RoundImageView roundImageView = new RoundImageView(PublishActivity.this);
            Glide.with(PublishActivity.this).load(videoFile)
                    .error(R.drawable.image_error).thumbnail(0.15f).into(new MySimpleTarget(roundImageView));
            roundImageView.setType(RoundImageView.TYPE_ROUND);
            roundImageView.setCornerRadius(10);
            roundImageView.setLayoutParams(tabItemLayoutParams);

            //在视频缩略图上加上video icon
            Resources resources = getResources();
            Drawable videoIcon = resources.getDrawable(R.drawable.ic_video_gray);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                roundImageView.setForeground(videoIcon);
            }

            //添加至页面布局
            tableRow = new TableRow(PublishActivity.this);
            tableRow.addView(roundImageView);
            pictureTableLayout.addView(tableRow);

            //添加视频view点击事件
            setItemClick(count, roundImageView);
            count++;
        }

        //添加商品所有图片
        for(int i = 0; i < photoFiles.size(); i++){
            if(count % 4 == 0){//四个item一行
                tableRow = new TableRow(PublishActivity.this);//换行
                pictureTableLayout.addView(tableRow);//添加一行子项进页面
            }
            //由选择的图片返回Uri列表，创建每个子项View
            RoundImageView roundImageView = new RoundImageView(PublishActivity.this);
            Glide.with(PublishActivity.this).load(photoFiles.get(i))
                    .error(R.drawable.image_error).thumbnail(0.15f).into(new MySimpleTarget(roundImageView));
            roundImageView.setType(RoundImageView.TYPE_ROUND);
            roundImageView.setCornerRadius(10);
            roundImageView.setLayoutParams(tabItemLayoutParams);
            //添加至页面布局
            tableRow.addView(roundImageView);

            //设置每个子项的点击事件
            setItemClick(count, roundImageView);
            count++;
        }

        //添加addaddPhotoVideoButton 至最后
        if(count % 4 == 0){//四个item一行
            tableRow = new TableRow(PublishActivity.this);
            pictureTableLayout.addView(tableRow);
        }
        addPhotoVideoButton.setLayoutParams(tabItemLayoutParams);
        //再次刷新时，addPhotoVideoButton已有ParentView
        if(addPhotoVideoButton.getParent() != null){
            ((ViewGroup)addPhotoVideoButton.getParent()).removeView(addPhotoVideoButton);
        }
        tableRow.addView(addPhotoVideoButton);
    }

    /**
     * 设置每个子项(展示的图片或视频)的点击事件
     * @param view
     */
    private void setItemClick(int count, View view){
        //设置一些启动参数
        final Intent intent = new Intent(PublishActivity.this, BigPictureBrowserActivity.class);
        intent.putExtra("index", count);//设置图片指示器，ViewPager从哪一页开始显示
        if(videoFile != null){
            List<File> files = new ArrayList<>();
            files.add(videoFile);
            intent.putExtra("videoFiles", files.toArray());
        }
        if(photoFiles != null && photoFiles.size() > 0){
            //由于Object[] 与 Parcelable[]之间不能转换, List<Parcelable> 与List<Uri>不能转换，虽然Uri实现了Parcelable接口
            // ，但是List<Uri>又是一个新数据类型.(List没有实现Serializable，但是数组类型实现了Serializable)
            intent.putExtra("photoFiles", photoFiles.toArray());//设置所有要显示的图片
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动图片浏览器活动
                startActivityForResult(intent, REQUEST_CODE_BIG_PICTURE);
            }
        });
    }
    /**
     * 创建并展示提示对话框（两种类型：一种两个按钮，相册和拍照； 一种四个按钮，相册、拍照和视频、摄像）
     * @param alertType 对话框类型
     * @return AlertDialog
     */
    private void showAlertDialog(int alertType){

        //自定义AlertDialog布局
        View alertDialogView = LayoutInflater.from(PublishActivity.this).inflate(R.layout.alert_dialog_layout,null);

        //设置标题和内容
        TextView titleTextView = alertDialogView.findViewById(R.id.title_text_view);
        titleTextView.setText("上传格式");
        TextView contentTextView = alertDialogView.findViewById(R.id.content_text_view);
        contentTextView.setText("请选择使用哪种方式来上传描述商品的图片或视频");

        //创建AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(PublishActivity.this);
        final AlertDialog alertDialog = builder.create();
        //设置自定义布局
        alertDialog.setView(alertDialogView, 75, 0, 75, 0);
        //显示AlertDialog
        alertDialog.show();

        //设置按钮及其点击事件(必须在show()之后设置监听事件)
        //相册按钮
        Button albumButton = alertDialogView.findViewById(R.id.button_1);
        albumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动相册选择器
                Matisse.from(PublishActivity.this)
                        .choose(MimeType.ofImage())// 选择 mime 的类型（图片类型）
                        .theme(R.style.CustomMatissesStyle)
                        .showSingleMediaType(true)
                        .countable(true)
                        .originalEnable(true)
                        .maxSelectable(11 - photoFiles.size()) // 每次图片选择的最多数量（总数量11）
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.8f) // 缩略图的比例
                        .imageEngine(new Glide4Engine()) // 使用的图片加载引擎
                        .forResult(REQUEST_CODE_ALBUM); // 设置作为标记的请求码
                //点击按钮后取消AlertDialog
                alertDialog.dismiss();
            }
        });

        //拍照按钮
        Button takePhotoButton = alertDialogView.findViewById(R.id.button_2);
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动自定义相机
                Intent intent = new Intent(PublishActivity.this, CustomCameraActivity.class);
                intent.putExtra("camera_type", CustomCameraActivity.CAMERA_TYPE_PICTURE);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
                //点击按钮后取消AlertDialog
                alertDialog.dismiss();
            }
        });

        if(alertType == ALERT_TYPE_TWO){//两个按钮
            //隐藏其余两个按钮
            alertDialogView.findViewById(R.id.button_3).setVisibility(View.GONE);
            alertDialogView.findViewById(R.id.button_4).setVisibility(View.GONE);

        }else if(alertType == ALERT_TYPE_FOUR){//四个按钮
            //视频按钮
            Button videoButton = alertDialogView.findViewById(R.id.button_3);
            videoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //启动视频选择器
                    Matisse.from(PublishActivity.this)
                            .choose(MimeType.ofVideo())// 选择 mime 的类型（视频类型）
                            .showSingleMediaType(true)
                            .theme(R.style.CustomMatissesStyle)
                            .countable(true)
                            .originalEnable(true)
                            .maxSelectable(1) // 每次图片选择的最多数量（总数量1个）
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                            .thumbnailScale(1f) // 缩略图的比例
                            .imageEngine(new Glide4Engine()) // 使用的图片加载引擎
                            .forResult(REQUEST_CODE_VIDEO); // 设置作为标记的请求码

                    //点击按钮后取消AlertDialog
                    alertDialog.dismiss();
                }
            });

            //摄像按钮,调用自定义相机
            Button shootButton = alertDialogView.findViewById(R.id.button_4);
            shootButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PublishActivity.this, CustomCameraActivity.class);
                    intent.putExtra("camera_type", CustomCameraActivity.CAMERA_TYPE_VIDEO);
                    startActivityForResult(intent, REQUEST_CODE_VIDICON);
                    //点击按钮后取消AlertDialog
                    alertDialog.dismiss();
                }
            });
        }
    }

    /**
     * 获取 照片 或 视频 活动返回的数据
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_CODE_ALBUM :

                    //照片返回 选择照片的Uris
                    Log.d(LOG_TAG, "Image Uris size: " + Matisse.obtainResult(data).size());
                    Log.d(LOG_TAG, "Image Uri 0: " + Matisse.obtainResult(data).get(0).toString());
                    //如果photoUris是适配器数据源，则不能直接指向Matisse返回的List<Uri> 对象，因为会导致notifyDataSetChanged不可用
                    List<String> picPaths = Matisse.obtainPathResult(data);
                    //将path转换成file,添加进photoFiles
                    for(String filePath : picPaths){
                        photoFiles.add(new File(filePath));
                    }
                    //通知布局刷新
                    inflateTableLayout();
                    break;
                case REQUEST_CODE_VIDEO:

                    //视频返回，返回的视频封面Uri，而不是视频
                    Log.d(LOG_TAG, "video Uris size: " + Matisse.obtainResult(data).size());
                    Log.d(LOG_TAG, "video Uri 0: " + Matisse.obtainResult(data).get(0).toString());
                    List<String> videoPaths = Matisse.obtainPathResult(data);
                    //将path转换成file,添加进photoFiles
                    videoFile = FileUtils.getFile(videoPaths.get(0));//视频文件只能有一个
                    inflateTableLayout();
                    break;
                case REQUEST_CODE_CAMERA:
                    //拍照返回
                    Object[] objects = (Object[])data.getSerializableExtra("customCameraPhotos");
                    for(int i = 0; i < objects.length; i++){
                        photoFiles.add((File)objects[i]);
                    }
                    inflateTableLayout();
                    break;
                case REQUEST_CODE_VIDICON:
                    //获取返回拍摄数据
                    videoFile = (File)data.getSerializableExtra("customCameraVideo");
                    inflateTableLayout();
                    break;
                case REQUEST_CODE_BIG_PICTURE:
                    //图片，视频浏览器可以删除，编辑图片
                    if(data != null){
                        if(data.hasExtra("videoFiles")){
                            Object[] objects1 = (Object[])data.getSerializableExtra("videoFiles");
                            if(objects1.length > 0) {
                                videoFile = (File) objects1[0];
                            }else{
                                videoFile = null;
                            }
                        }
                        if(data.hasExtra("photoFiles")){
                            Object[] objects2 = (Object[])data.getSerializableExtra("photoFiles");
                            //清除原有图片，接收修改结果
                            photoFiles.clear();
                            for(int i = 0; i < objects2.length; i++){
                                photoFiles.add((File)objects2[i]);
                            }
                        }
                    }
                    break;
                case REQUEST_CODE_CHOOSE_CATEGORY:
                    //获取返回类别数据
                    if(data != null && data.hasExtra("choose_category")){
                        category = (Category)data.getSerializableExtra("choose_category");
                        //设置EditText显示
                        categoryEditText.setText(category.getPrimaryCategory()+" - "+category.getSecondaryCategory());
                    }
                    break;
                case REQUEST_CODE_CHOOSE_POSITION:
                    //获取返回位置信息数据
                    addressDetail = (AddressDetail) data.getSerializableExtra("publish_position");
                    locationTextView.setText(addressDetail.getProvince()+","+addressDetail.getCity()+","+addressDetail.getDistrict());
                    break;
                default:break;
            }

        }
    }

    private void uploadCommodity(){

        //文件路径数组长度
        int length = photoFiles.size();
        if(videoFile != null){
            length += 1;
        }
        //文件路径数组
        final String[] filePaths = new String[length];
        int i = 0;
        if(videoFile != null){
            filePaths[i] = videoFile.getAbsolutePath();
            ++i;
        }
        if(photoFiles.size() > 0){
            for(File f : photoFiles){
                filePaths[i] = f.getAbsolutePath();
                ++i;
            }
        }
        Log.d(LOG_TAG, "filePath: " + filePaths[0]);
        //批量上传文件, 上传商品
        BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> list, List<String> list1) {
                //文件全部上传成功
                if(list.size()==filePaths.length){
                    Log.d(LOG_TAG, "文件全部上传成功");
                    //设置发布商品图片
                    commodity.addAll("pictureAndVideo", list);
                    //文件上传成功后, 才上传商品
                    commodity.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e == null){
                                Log.d(LOG_TAG, "添加商品成功");
                                closeProgressDialog();
                                ToastUtils.toastMessage(PublishActivity.this, "发布商品成功");
                                finish();
                            }else{
                                Log.d(LOG_TAG, "添加商品失败, " + e.getMessage());
                            }
                        }
                    });
                }
            }

            @Override
            public void onProgress(int i, int i1, int i2, int i3) { }

            @Override
            public void onError(int i, String s) {
                Log.d(LOG_TAG, "文件上传失败，错误描述："+s);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //@TODO 保存草稿

        finish();
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if(progressDialog == null){
            progressDialog = new ProgressDialog(PublishActivity.this);
            progressDialog.setMessage("正在上传商品数据,请勿退出...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }
}

/**
 * Glide -> RoundImageView Target
 */
class MySimpleTarget extends SimpleTarget<Drawable>{

    private RoundImageView view;

    public MySimpleTarget(RoundImageView view){
        this.view = view;
    }

    @Override
    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
        view.setImageDrawable(resource);


    }
    @Override
    public void onLoadFailed(@Nullable Drawable errorDrawable) {
        super.onLoadFailed(errorDrawable);
        Log.d("PublishActivity", "onLoadFailed");
    }
}
