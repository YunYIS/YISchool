package com.example.yischool;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.*;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.yischool.Utils.FileUtils;

/**
 * @author 张云天
 * date on 2019/5/9
 * describe: 自定义相机(拍照和摄像)
 */
public class CustomCameraActivity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback {

    public static final String LOG_TAG = "CustomCameraActivity";
    //录制文件后缀名
    public static final String VIDEO_SUFFIX = ".mp4";
    //图片文件后缀名
    public static final String IMG_SUFFIX = ".jpeg";
    //照相机类型，拍照或摄像
    public static final int CAMERA_TYPE_PICTURE = 1;//拍照
    public static final int CAMERA_TYPE_VIDEO = 2;//录制
    //当前视频录制状态
    public static final int RECORD_STATE_PAUSE = 21;//暂停
    public static final int RECORD_STATE_BEING = 22;//正在录制
    public static final int RECORD_STATE_STOP = 23;//停止(即使开始过，现在录制也结束了)
    //REQUEST CODE
    public static final int REQUEST_CODE_CUSTOM_CAMERA = 10;
    //最大录制时间(ms)
    public static final int MAX_RECORD_DURING = 40 * 1000;

    private ImageView backButton;
    private ImageView flashLightButton;
    private ImageView changeCameraButton;
    private SurfaceView surfaceView;
    private ImageView displayPhotosButton;
    private ImageView captureButton;
    private ImageView completeButton;
    private TextView restartRecordingButton;
    private ImageView recordingButton;
    private Chronometer timer;//视频计时器

    private Camera mCamera;
    private SurfaceHolder surfaceHolder;
    private Camera.PictureCallback pictureCallback;
    private List<File> photoFiles;//保存所有拍摄的图片文件
    private int frontCameraIndex;//前置摄像头索引
    private int backCameraIndex;//后置索引
    private int currentCameraIndex;//当前摄像头索引
    private int cameraType;

    //录制相关
    private int currRecordState; //标记当前录制状态
    private long recordCurrentTime = 0; //录制时间
    private File videoFile;//保存视频文件
    private MediaRecorder mediaRecorder;//视频录制关键类
    private MediaRecorder.OnErrorListener onErrorListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_camera);

        backButton = findViewById(R.id.back_button);
        flashLightButton = findViewById(R.id.flash_light_button);
        changeCameraButton = findViewById(R.id.change_camera_button);
        surfaceView = findViewById(R.id.surface_view);
        displayPhotosButton = findViewById(R.id.display_photos_button);
        captureButton = findViewById(R.id.capture_button);
        completeButton = findViewById(R.id.complete_button);
        recordingButton = findViewById(R.id.recording_button);
        restartRecordingButton = findViewById(R.id.restart_recording_button);
        timer = findViewById(R.id.record_time);

        //注册监听事件
        backButton.setOnClickListener(this);
        flashLightButton.setOnClickListener(this);
        changeCameraButton.setOnClickListener(this);
        completeButton.setOnClickListener(this);

        //获取照相机类型
        Intent intent = getIntent();
        cameraType = intent.getIntExtra("camera_type", CAMERA_TYPE_PICTURE);

        //不同类型，界面布局及其点击事件不同
        if(cameraType == CAMERA_TYPE_PICTURE){
            //采用拍照布局
            restartRecordingButton.setVisibility(View.GONE);
            recordingButton.setVisibility(View.GONE);
            timer.setVisibility(View.GONE);
            //拍照相关按钮点击事件
            displayPhotosButton.setOnClickListener(this);
            captureButton.setOnClickListener(this);
            //工具类相关
            pictureCallback = new MyPictureCallback();
            photoFiles = new ArrayList<>();

        }else if(cameraType == CAMERA_TYPE_VIDEO){ //录制
            //布局相关
            displayPhotosButton.setVisibility(View.GONE);
            captureButton.setVisibility(View.GONE);
            restartRecordingButton.setOnClickListener(this);
            recordingButton.setOnClickListener(this);
            //计时器监听
            timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    //最大录制时间MAX_RECORD_DURING秒
                    if(SystemClock.elapsedRealtime() - chronometer.getBase() >= MAX_RECORD_DURING){
                        //自动停止录制
                        recordingButton.setImageResource(R.drawable.ic_start);
                        timer.stop();//停止计时
                    }
                }
            });
            //工具类相关
            //设置文件输出路径
            currRecordState = RECORD_STATE_STOP;
            videoFile = FileUtils.getFileInCache(CustomCameraActivity.this,
                    InitApplication.getCurrentUser().getUsername()+System.currentTimeMillis()+ VIDEO_SUFFIX,
                    FileUtils.GET_FILE_TYPE_NEW);

            onErrorListener = new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mediaRecorder, int what, int extra) {
                    try {
                        if (mediaRecorder != null) {
                            mediaRecorder.reset();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        }
        //初始化所需实例域
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// 设置Surface不需要维护自己的缓冲区
        surfaceHolder.addCallback(this);
        //初始化摄像头设备索引
        setCameraIndex();
    }

    /**
     * 各个控件点击功能逻辑
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_button:
                //如果拍摄得有照片，则提示照片会丢失，是否退出，否则，不提示，直接退出Activity
                if(photoFiles != null && photoFiles.size() > 0){

                    showAlertDialog();
                } else if(videoFile!=null){

                    showAlertDialog();
                }else {
                    finish();
                }
                break;
            case R.id.flash_light_button:
                //打开或关闭闪光灯
                turnLightOnOff(mCamera);
                break;
            case R.id.complete_button:
                //如果拍摄得有照片，则保存后退出，否则，直接退出
                if(photoFiles != null && photoFiles.size() > 0){
                    //保存图片并退出
                    Intent intent = new Intent();
                    intent.putExtra("customCameraPhotos", photoFiles.toArray());
                    setResult(RESULT_OK, intent);

                } else if(videoFile != null && videoFile.exists()){
                    //保存视频并退出
                    Intent intent = new Intent();
                    intent.putExtra("customCameraVideo", videoFile);
                    setResult(RESULT_OK, intent);
                }
                finish();
                break;
            case R.id.capture_button:
                //拍摄照片，并存储至app自带缓存目录，并在拍摄完成后重启预览
                captureButton.setClickable(false);//在拍照完成之前不能重复拍照
                completeButton.setClickable(false);//在拍照完成之前不能保存
                capture();
                break;
            case R.id.change_camera_button:
                //切换前后摄像头
                changeCamera();
                break;
            case R.id.display_photos_button:
                if(photoFiles.size() > 0){
                    Intent startBigPictureIntent = new Intent(CustomCameraActivity.this, BigPictureBrowserActivity.class);
                    startBigPictureIntent.putExtra("CustomCamera_photoFiles" ,photoFiles.toArray());
                    startActivityForResult(startBigPictureIntent, REQUEST_CODE_CUSTOM_CAMERA);
                }
                break;
            case R.id.surface_view:
                mCamera.autoFocus(null);
                break;
            case R.id.recording_button:
                //开始/继续/停止录制
                if(currRecordState == RECORD_STATE_STOP){
                    startRecord();
                    timer.setBase(SystemClock.elapsedRealtime());
                    timer.start();
                    recordingButton.setImageResource(R.drawable.ic_stop);
                }else if(currRecordState == RECORD_STATE_BEING){
                    stopRecord();
                    timer.stop();
                    recordingButton.setImageResource(R.drawable.ic_start);
                }
                break;
            case R.id.restart_recording_button:
                //重新开始录制

                break;
            default:break;
        }
    }

    @Override
    public void onBackPressed() {
        //如果拍摄得有照片，则提示照片会丢失，是否退出，否则，不提示，直接退出Activity
        if(photoFiles != null && photoFiles.size() > 0){
            showAlertDialog();
        }else if(videoFile != null){
            showAlertDialog();
        }else {
            finish();
        }
    }

    /**
     * 切换前后摄像头
     */
    private void changeCamera(){

        //先释放相机
        releaseCamera();
        //切换相机
        if(currentCameraIndex == backCameraIndex){
            mCamera = getCamera(frontCameraIndex);
            setStartPreview(mCamera, surfaceHolder);
            currentCameraIndex = frontCameraIndex;
        }else {
            mCamera = getCamera(backCameraIndex);
            setStartPreview(mCamera, surfaceHolder);
            currentCameraIndex = backCameraIndex;
        }

    }

    /**
     * 获取并设置frontCameraIndex和backCameraIndex
     */
    private void setCameraIndex(){

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraNumber = Camera.getNumberOfCameras();

        for (int i = 0; i < cameraNumber; i++) {
            Camera.getCameraInfo(i,cameraInfo);
            if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                frontCameraIndex = i;
            }
            else if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
                backCameraIndex = i;
            }
        }
    }

    /**
     * 拍照动作，并保存照片
     */
    private void capture(){
        //mCamera相关参数（预览和图片等）都在getCamera()中设置好了
        //拍摄照片时，先自动对焦，在执行拍摄操作
        mCamera.autoFocus(new AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                //自动聚焦成功
                if(success){
                    //拍照
                    mCamera.takePicture(null, null, pictureCallback);
                }
            }
        });
    }

    /**
     * 退出提示图片保存
     */
    private void showAlertDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(CustomCameraActivity.this);
        builder.setTitle("确定退出");
        builder.setMessage("您拍摄的图片或视频还未保存，是否退出拍摄？");
        builder.setPositiveButton("仍然退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //初始化时，打开后置相机
        mCamera = getCamera(backCameraIndex);
        //启动相机预览
        if(mCamera != null && surfaceHolder != null){
            setStartPreview(mCamera, surfaceHolder);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();
        releaseCamera();
    }

    /**
     * 开始预览相机内容，并在surfaceView中显示
     */
    private void setStartPreview(Camera camera, SurfaceHolder holder){
        try {
            camera.setPreviewDisplay(holder);//绑定相机与SurfaceView
            camera.setDisplayOrientation(90);//竖屏的Camera
            camera.startPreview();//开始预览

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始录制视频
     */
    public void startRecord() {

        if(mCamera != null && surfaceHolder != null){
            //配置MediaRecorder
            if(setMediaRecorder(mCamera,surfaceHolder)){
                //开始录制
                mediaRecorder.start();
                currRecordState = RECORD_STATE_BEING;
            }
        }
    }

    /**
     * 停止录制视频
     */
    public void stopRecord() {
        //停止录制
        mediaRecorder.stop();
        //释放资源
        releaseMediaRecorder();
        currRecordState = RECORD_STATE_STOP;
    }

    /**
     * 释放MediaRecorder
     */
    private void releaseMediaRecorder(){
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            mCamera.lock();
        }
    }
    /**
     * 初始化一个Camera对象
     * @param index 前/后摄像头
     * @return
     */
    private Camera getCamera(int index){
        if(mCamera != null){
            return mCamera;
        }else {
            //按前后摄像头类型打开照相机
            currentCameraIndex = index;
            Camera camera = Camera.open(index);
            //设置相机相关参数
            setCameraParams(camera, cameraType);
            return camera;
        }
    }

    /**
     * 释放资源
     */
    private void releaseCamera(){
        if(mCamera != null){
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

    }

    /**
     * 根据相机类型设置相机参数
     * @param cameraType
     */
    private void setCameraParams(Camera camera, int cameraType){
        if(cameraType == CAMERA_TYPE_PICTURE){
            setPicCameraParams(camera);
        }else if(cameraType == CAMERA_TYPE_VIDEO){
            setVidCameraParams(camera);
        }

    }

    /**
     * 设置 录制视频类型 时的相机参数
     * @param camera
     */
    private void setVidCameraParams(Camera camera){

        if(camera != null){
            Parameters parameters = camera.getParameters();
            //设置聚焦模式
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            //缩短Recording启动时间
            parameters.setRecordingHint(true);
            //是否支持影像稳定能力，支持则开启
            if (parameters.isVideoStabilizationSupported())
                parameters.setVideoStabilization(true);

            camera.setParameters(parameters);
        }
    }

    /**
     * 配置MediaRecorder（必须按顺序配置）
     */
    private boolean setMediaRecorder(Camera camera, SurfaceHolder surfaceHolder){

        //设备的支持VideoSize（必须在unlock之前调用getParameters()）
        List<Size> sizeList = camera.getParameters().getSupportedVideoSizes();

        mediaRecorder = new MediaRecorder();

        camera.unlock();
        mediaRecorder.reset();
        mediaRecorder.setCamera(camera);
        mediaRecorder.setOnErrorListener(onErrorListener);
        //设置采集声音
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置采集图像
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //设置视频，音频的输出格式 mp4
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //设置音频的编码格式
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //设置图像的编码格式
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //设置最大录像时间 单位：毫秒
        mediaRecorder.setMaxDuration(MAX_RECORD_DURING);
        //音频一秒钟包含多少数据位
        CamcorderProfile mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        mediaRecorder.setAudioEncodingBitRate(44100);
        if (mProfile.videoBitRate > 2 * 1024 * 1024)
            mediaRecorder.setVideoEncodingBitRate(2 * 1024 * 1024);
        else
            mediaRecorder.setVideoEncodingBitRate(1024 * 1024);
        mediaRecorder.setVideoFrameRate(mProfile.videoFrameRate);

        //设置选择角度，顺时针方向
        mediaRecorder.setOrientationHint(90);
        //设置录像的分辨率
        mediaRecorder.setVideoSize(sizeList.get(0).width, sizeList.get(0).height);
        //文件输出位置
        mediaRecorder.setOutputFile(videoFile.getAbsolutePath());
        //准备配置MediaRecorder
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(LOG_TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            currRecordState = RECORD_STATE_STOP;
            return false;
        } catch (IOException e) {
            Log.d(LOG_TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            currRecordState = RECORD_STATE_STOP;
            return false;
        }
        return true;
    }

    /**
     * 设置 拍照类型 时的相机参数
     * @param camera
     */
    private void setPicCameraParams(Camera camera){

        if(camera != null){
            Parameters parameters = camera.getParameters();
            //获取设备支持的预览大小
            List<Camera.Size> preSizeList = parameters.getSupportedPreviewSizes();
            //设置所支持的最大预览大小
            parameters.setPreviewSize(preSizeList.get(0).width, preSizeList.get(0).height);
            //图片格式
            parameters.setPictureFormat(ImageFormat.JPEG);
            //获取设备支持的图片大小
            List<Camera.Size> picSizeList = parameters.getSupportedPictureSizes();
            //设置设备所支持的最大图片大小
            parameters.setPictureSize(picSizeList.get(picSizeList.size()-1).width,
                    picSizeList.get(picSizeList.size()-1).height);
            //设置捕获图片的Jpeg质量
            parameters.setJpegQuality(100);
            camera.setParameters(parameters);
        }
    }
    /**
     * 通过设置Camera打开或关闭闪光灯，并更改图标
     * @param camera
     */
    public void turnLightOnOff(Camera camera) {
        if (camera == null) {
            return;
        }
        Parameters parameters = camera.getParameters();
        if (parameters == null) {
            return;
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        // 检查闪光灯存在
        if (flashModes == null) {
            return;
        }
        String flashMode = parameters.getFlashMode();
        if (!Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
            // 打开闪关灯
            if (flashModes.contains(Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameters);
                //设置按钮icon
                flashLightButton.setImageResource(R.drawable.ic_flash_close);
            }

        } else {
            // 关闭闪关灯
            if (flashModes.contains(Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
                //设置按钮icon
                flashLightButton.setImageResource(R.drawable.ic_flash_open);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_CODE_CUSTOM_CAMERA){
                Object[] objects = (Object[])data.getSerializableExtra("photoFiles");
                photoFiles = new ArrayList<>();
                for(int i = objects.length-1; i >= 0; i--){//在浏览器里图片被倒序放置，现在正序
                    photoFiles.add((File)objects[i]);
                }
                //有图片浏览器返回的数据，就说明有图片被删除
                if(photoFiles.size() == 0){//如果图片被删完了
                    displayPhotosButton.setImageResource(R.drawable.ic_photo);
                }else{
                    Glide.with(CustomCameraActivity.this).load(photoFiles.get(0)).into(displayPhotosButton);
                }
                Log.d(LOG_TAG,"object size: " +objects.length);
                Log.d(LOG_TAG,"photoFiles size: " +photoFiles.size());

            }

        }
    }

    /**
     * 实现SurfaceHolder.Callback，重写的三个方法
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(LOG_TAG,"surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.stopPreview();
        setStartPreview(mCamera, holder);
        Log.d(LOG_TAG,"surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    class MyPictureCallback implements PictureCallback{

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            //使用Bitmap进行图像的旋转
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Matrix matrix = new Matrix();
            matrix.setRotate(90);
            bitmap = Bitmap.createBitmap(bitmap,0, 0, bitmap.getWidth(), bitmap.getHeight(),matrix,true);
            //将旋转后的图像再转换成byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] matrixData = baos.toByteArray();

            //data保存了完整的图像效果的数据，而不是缩略图
            File file = FileUtils.createFileInCache(CustomCameraActivity.this,
                    InitApplication.getCurrentUser().getUsername()+System.currentTimeMillis()+IMG_SUFFIX, matrixData);
            if(file != null){
                photoFiles.add(file);
//                Glide.with(CustomCameraActivity.this).load(file).into(displayPhotosButton);
                displayPhotosButton.setImageBitmap(bitmap);
            }
            //takePicture()后会停止预览，只有Camera在预览状态才能拍照
            setStartPreview(mCamera,surfaceHolder);
            //可以再次拍照
            captureButton.setClickable(true);
            //保存可用
            completeButton.setClickable(true);
        }
    }
}
