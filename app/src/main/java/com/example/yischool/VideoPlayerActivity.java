package com.example.yischool;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;

/**
 * @author 张云天
 * date on 2019/5/14
 * describe: 自定义视频播放器（文件路径播放，单个视频文件播放）
 * MediaController+VideoView实现方式
 */
public class VideoPlayerActivity extends AppCompatActivity {

    public static final String LOG_TAG = "VideoPlayerActivity";

    //视频播放状态
    public static final int PLAY_STATE_STOP = 0;//停止
    public static final int PLAY_STATE_PLAYING = 1;//正在播放
    public static final int PLAY_STATE_PAUSE = 2;//暂停
    
    
    private VideoView videoView;
    private SeekBar videoProgressBar;
    private Chronometer timer;//视频计时器
    private TextView videoDuringView;//显示视频总时长
    private ImageView playButton;

    private MediaController mediaController;//媒体辅助控制器
    private int playState = PLAY_STATE_STOP;//当视频播放状态
    private int playCurrentTime = 0; //当前播放时间（ms）
    private File videoFile;
    private  int rangeTime = 0;//Timer暂停时间


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        //获取视频资源
        Intent intent = getIntent();
        if(intent.hasExtra("videoFile")){
            videoFile = (File)intent.getSerializableExtra("videoFile");
        }
        //判断视频资源是否可用
        if(videoFile == null || !videoFile.exists() || !videoFile.isFile()){
            showAlertDialog();
        }else{
            //视频资源可用，初始化控件
            videoView = findViewById(R.id.video_view);
            videoProgressBar = findViewById(R.id.seek_bar);
            timer = findViewById(R.id.video_play_time);
            playButton = findViewById(R.id.play_button);
            videoDuringView = findViewById(R.id.video_duration);

            //加载指定的视频文件
            videoView.setVideoURI(Uri.fromFile(videoFile));

            //计算视频总时长(不能使用getDuring()，会返回-1，因为是stream file)
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(videoFile.getAbsolutePath());
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);//ms数
            int videoDuring = Integer.valueOf(durationStr);
            Log.d(LOG_TAG, "video during: " + videoDuring);

            //设置显示视频总时长
            videoDuringView.setText(duringToTime(videoDuring));
            Log.d(LOG_TAG, "timer base: " + timer.getBase());

            //播放/暂停 按钮
            playButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if(playState == PLAY_STATE_STOP){

                        startPlay();
                    }else if(playState == PLAY_STATE_PLAYING){

                        pausePlay();
                    }else if(playState == PLAY_STATE_PAUSE){

                        continuePlay();
                    }
                }
            });
            //进入活动，视频加载完成后，就开始自动播放视频
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    startPlay();
                }
            });
            //视频播放完成时
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playState = PLAY_STATE_STOP;
                    playButton.setImageResource(R.drawable.ic_start_white);
                    //进度条重置
                    videoProgressBar.setProgress(0);
                    //计时器重置
                    setTimerBase(0, 0);
                    timer.stop();
                }
            });

            //视频播放计时
            timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    //进度条随播放计时变化
                    Log.d(LOG_TAG,"onCh():" + (int)(SystemClock.elapsedRealtime()- chronometer.getBase()));
                    videoProgressBar.setProgress((int)(SystemClock.elapsedRealtime()- chronometer.getBase())/1000);
                }
            });

            //可拖动进度条
            videoProgressBar.setMax(videoDuring/1000);
            videoProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                int seekStartTime = 0;//进度条开始拖动位置
                int seekStopTime = 0;//进度条结束拖动位置

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                    seekStartTime = seekBar.getProgress();
                    rangeTime = pauseTimer();

                    if(playState == PLAY_STATE_PLAYING){
                        videoView.pause();
                        rangeTime = pauseTimer();
                    }
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                    seekStopTime = seekBar.getProgress();
                    int seekTime = seekStopTime - seekStartTime;//拖动时间间隔

                    if(playState == PLAY_STATE_PLAYING){
                        Log.d(LOG_TAG,"seekBar stop touch: " + seekStopTime + "video position: " + videoView.getCurrentPosition());
                        videoView.seekTo(seekStopTime*1000);
                        videoView.start();
                        continueTimer(rangeTime, seekTime*1000);

                    }else if(playState == PLAY_STATE_PAUSE){
                        videoView.seekTo(seekStopTime*1000);
                        setTimerBase(rangeTime, seekTime*1000);

                    }else if(playState == PLAY_STATE_STOP){
                        videoView.seekTo(seekStopTime*1000);
                        setTimerBase(0, seekTime*1000);
                    }

                }
            });

            
        }
    }

    /**
     * 开始播放视频
     */
    private void startPlay(){

        playState = PLAY_STATE_PLAYING;
        videoView.start();
        //开始计时
        startTimer();
        playButton.setImageResource(R.drawable.ic_pause_white);
    }

    /**
     * 暂停播放
     */
    private void pausePlay(){

        playState = PLAY_STATE_PAUSE;
        videoView.pause();
        rangeTime = pauseTimer();
        playButton.setImageResource(R.drawable.ic_start_white);
    }

    /**
     * 继续播放
     */
    private void continuePlay(){

        playState = PLAY_STATE_PLAYING;
        videoView.start();
        continueTimer(rangeTime, 0);
        playButton.setImageResource(R.drawable.ic_pause_white);
    }
    /**
     * 开始计时（每次调用从0开始计时）
     */
    private void startTimer(){
        if(timer != null){
            timer.setBase(SystemClock.elapsedRealtime());
            timer.start();
        }
    }

    /**
     * 暂停计时
     * @return 暂停时间(ms数)
     */
    private int pauseTimer(){
        int rangeTime = 0;
        if(timer != null){
            rangeTime = (int)(SystemClock.elapsedRealtime() - timer.getBase());
            timer.stop();
        }
        return rangeTime;
    }
    /**
     * 继续计时
     * @param rangeTime 暂停时间
     * @param seekTime 拖动（进度条）时间（ms数）,如果seekTime为0，则没有拖动
     */
    private void continueTimer(int rangeTime, int seekTime){
        if(timer != null){
            setTimerBase(rangeTime,seekTime);
            timer.start();
        }
    }
    /**
     * 设置Timer(Chronometer)显示时间,但不开始计时
     * @param rangeTime 暂停时间
     * @param seekTime 拖动（进度条）时间（ms数）
     */
    private void setTimerBase(int rangeTime, int seekTime){

        timer.setBase(SystemClock.elapsedRealtime() - rangeTime - seekTime);
    }

    /**
     * 毫秒数转换成 MM:SS 格式字符串(用于视频总时间的显示)
     * @param during
     * @return
     */
    private String duringToTime(int during){

        int seconds = (during/1000) % 60;
        int minutes = (during/1000) / 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    /**
     * 提示图片文件出错
     */
    private void showAlertDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(VideoPlayerActivity.this);
        builder.setTitle("错误");
        builder.setMessage("视频文件不可用");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        //设置对话框不可取消
        builder.setCancelable(false);
        builder.create().show();
    }
}
