package ViewHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.yischool.R;

import java.util.ArrayList;
import java.util.List;

import adapter.SlideViewPagerAdapter;

/**
 * @author 张云天
 * date on 2019/3/24
 * describe: 处理首页（HomePageFragment）中的轮播图的工具类，封装所有关于轮播图的方法
 * （并不是一个自定义View，只是由于写在Activity或Fragment中代码太杂乱）
 */
public class SlideViewPagerHelper {

    public static final int SLIDE_TIME = 3000;//;轮播延迟时间（毫秒）
    public final int DOT_SIZE;//圆点大小，由于做的并不是通用轮播图，只是用于本程序使用，所以没有为用户暴露接口，而是构造器中直接指定

    private ViewPager viewPager;
    private SlideViewPagerAdapter adapter;//ViewPager适配器
    private Handler handler;//发送轮播延时消息
    private List<String> imageUrls;//ViewPager图片数据源的Url（从网络加载图片，现在只是模拟）
    private List<ImageView> imageViews;//适配器初始化数据源
    private int currPosition = 0;//记录当前轮播图片位置
    private Context context;
    private LinearLayout indicatorDotLayoout;//指示器圆点线性布局

    /**
     * 构造器初始化ViewPager和适配器（没有无参构造器，因为没有ViewPager和适配器这个工具类将毫无意义）
     * @param viewPager ViewPager实例
     */
    public SlideViewPagerHelper(Context context, ViewPager viewPager, LinearLayout indicatorDotLayoout){
        this.viewPager = viewPager;
        this.context = context;
        this.indicatorDotLayoout = indicatorDotLayoout;
        DOT_SIZE = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                5,context.getResources().getDisplayMetrics());//圆点默认大小
    }

    /**
     * 初始化数据源（从网络请求数据，到解析返回的数据，再从网络加载图片到imageView）
     */
    private void initData(){
        //@TODO 开启子线程或服务加载图片数据，现在由于还没有做后台，只是模拟
//        ImageView imageView = null;
//        imageViews = new ArrayList<>();
//        //加载图片思路（前端+后台），前端网络查询收藏量前五的商品，后端返回商品id，和商品第一张图片的url（JSON格式），解析JSON数据，初始化imageUrl
//        //在通过Glide对每张图片的imageUrl进行加载（从网络访问开始 就在服务（子线程）中进行，知道imageViewList初始化结束）
//        ！！！异步处理，在图片没有加载完成之前，要初始化适配器，这时需要imageViews已初始化，但为空或为上次缓存图片
//        imageUrls;//初始化
//        for(int i = 0; i < imageUrls.size(); i++){
//            imageView = new ImageView(context);
//            Glide.with(context).load(imageUrls.get(i)).centerCrop().into(imageView);
//            imageViews.add(imageView);
//        }
        imageUrls = new ArrayList<>();
        imageUrls.add(""+R.drawable.slide_picture_1);
        imageUrls.add(""+R.drawable.slide_picture_2);
        imageUrls.add(""+R.drawable.slide_picture_3);
        imageUrls.add(""+R.drawable.slide_picture_4);
        ImageView imageView = null;
        imageViews = new ArrayList<>();
        for(int i = 0; i < imageUrls.size(); i++){
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(Integer.valueOf(imageUrls.get(i)));
            imageViews.add(imageView);
        }
    }

    /**
     * 初始化小圆点指示器
     */
    @SuppressLint("NewApi")
    private void initIndicatorDot(){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(DOT_SIZE, DOT_SIZE);
        lp.setMarginEnd((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                5,context.getResources().getDisplayMetrics()));//每个指示点（圆点）默认间距大小
        View view = null;
        for(int i = 0; i < imageUrls.size(); i++){
            view = new View(context);
            view.setLayoutParams(lp);
            view.setBackgroundResource(R.drawable.indicator_dot);
            indicatorDotLayoout.addView(view);
        }
        indicatorDotLayoout.getChildAt(currPosition).setSelected(true);//初始点选中
    }
    /**
     * 初始化适配器，set适配器，并重写监听滑动事件 (实现定时轮播)(选择轮播小点)
     * 轮播实现思路：第一条消息自动发送，用于启动轮播（实际上启动了监听事件调用onPageScrollStateChanged），
     * 在滑动停止时（即SCROLL_STATE_IDLE状态）发送一条延时消息，继续轮播，以此类推，实现无限轮播。
     * 轮播期间，如果用户对轮播图，进行了操作，即SCROLL_STATE_DRAGGING状态，则清空消息队列，停止轮播，
     * 在用户停止操作时，又会进入SCROLL_STATE_IDLE状态，则继续发送延时消息，以此类推，继续轮播。
     */
    private void initListener(){
        handler = new Handler();//轮播消息handler
        adapter = new SlideViewPagerAdapter(context, imageViews);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(final int position) {
                currPosition = position;//实际当前位置很可能大于数据源size
                selectedDot();
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state){
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        handler.removeCallbacksAndMessages(null);
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                viewPager.setCurrentItem(currPosition+1, true);
                            }
                        }, SLIDE_TIME);
                        break;
                    default:break;
                }
            }
        });
        //发送第一条消息，用于启动轮播（如果用户此时操作，那么这条消息会被清出消息队列）
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(1, true);
            }
        }, SLIDE_TIME);
    }
    /**
     * 设置选中小点，并取消前一个小点的选中
     */
    public void selectedDot(){
        int size = indicatorDotLayoout.getChildCount();
        int dotPosition = currPosition % size;//小圆点选中位置（因为使用MAX_COUNT实现的循环）
        int previousDotPosition = (currPosition-1)%size;//前一个小圆点位置(向右滑动)
        int afterDotPosition = (currPosition+1)%size;//后一个小点位置（向左滑动）
        if(previousDotPosition < 0){
            previousDotPosition = imageViews.size() + previousDotPosition;
        }
        indicatorDotLayoout.getChildAt(afterDotPosition).setSelected(false);
        indicatorDotLayoout.getChildAt(previousDotPosition).setSelected(false);
        indicatorDotLayoout.getChildAt(dotPosition).setSelected(true);
    }
    /**
     * 启动轮播图
     */
    public void startSlideViewPager(){
        //注：方法顺序不能改变（不然NullPointer）
        initData();
        initIndicatorDot();
        initListener();
    }
    /**
     * 记得在onDestroy中停止轮播，以避免发生OOM
     */
    public void stopSlideViewPager(){
        handler.removeCallbacksAndMessages(null);//清空消息队列，避免OOM
    }
}
