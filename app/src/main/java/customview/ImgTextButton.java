package customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.*;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yischool.R;

public class ImgTextButton extends LinearLayout {

    private boolean isActionDown = false;//是否从控件范围内按下（而不是从控件外移动而来）
    private boolean isTouch = true;//设置控件是否可以响应触摸事件（主要因为复用了TabLayout的tab键，它不需要点击功能），默认可以响应触摸

//    private int viewId;//判断那个view调用
    private Context context;
    private ImageView imageView;
    private TextView textView;

    //自定义属性
    private String text;//文字内容
    private float textSize;//文字大小
    private int textColor;//文字颜色
    private int imageDrawable;//imageView的src
    private float imageWidth;//imageView宽度
    private float imageHeight;//imageView高度
    private float imageMarginTop;//imageView与父布局的上边距
    private int imageScaleType;//scr图片的放缩类型、模式
    private int spacing;//上方图片与下方文字的间距
    private Drawable background;//背景图，支持透明（null）
    private LinearLayout.LayoutParams imageLayoutParams;//图片布局参数
    /**
     * (必须使用同一个LayoutParams对象，因为ImgTextButton有三处可以修改布局参数，如果不是同一个对象，
     *   而是去新建一个LayoutParams对象，再set进去，那么以前做的设置就被重置了)
     */
    private LinearLayout.LayoutParams textLayoutParams;//文字布局参数
    public static final ScaleType[] mScaleTypeArray = {//imageScaleType属性值
            ScaleType.FIT_START,
            ScaleType.FIT_CENTER,
            ScaleType.FIT_END,
            ScaleType.CENTER,
            ScaleType.CENTER_CROP,
            ScaleType.CENTER_INSIDE,
            ScaleType.FIT_XY
    };

    private OnClickedListener listener;//ImgTextButton点击事件接口对象

    public ImgTextButton(Context context) {
        this(context, null, 0);
    }

    public ImgTextButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImgTextButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    /**
     * 自定义组合控件初始化，获取自定义属性值
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr){
        this.context = context;
        //加载ImgTextButton布局
        LayoutInflater.from(context).inflate(R.layout.view_img_text_button, this, true);
        imageView = findViewById(R.id.icon_image_view);
        textView = findViewById(R.id.text_view);
        //获取属性值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImgTextButton);
        text = typedArray.getString(R.styleable.ImgTextButton_text);
        textSize = typedArray.getDimension(R.styleable.ImgTextButton_textSize,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 4,context.getResources().getDisplayMetrics()));
        Log.d("img", ""+textSize);
        //上面将sp值转换成px
        Log.d("imgText sp-px:",""+TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 4,context.getResources().getDisplayMetrics()));
        textColor = typedArray.getColor(R.styleable.ImgTextButton_textColor, Color.rgb(30, 30, 30));
        imageDrawable = typedArray.getResourceId(R.styleable.ImgTextButton_imageDrawable, R.drawable.default_head_portrait);
        imageWidth = typedArray.getDimension(R.styleable.ImgTextButton_imageWidth,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24,context.getResources().getDisplayMetrics()));
        imageHeight = typedArray.getDimension(R.styleable.ImgTextButton_imageHeight,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24,context.getResources().getDisplayMetrics()));
        imageMarginTop = typedArray.getDimension(R.styleable.ImgTextButton_imageMarginTop,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0,context.getResources().getDisplayMetrics()));
        imageScaleType = typedArray.getInt(R.styleable.ImgTextButton_imageScaleType, 4);//默认值4，表示Center_Crop
        spacing = typedArray.getDimensionPixelSize(R.styleable.ImgTextButton_spacing,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0,context.getResources().getDisplayMetrics()));
        //上面将dp值转换成px
        Log.d("imgText dp-px:",""+TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 0,context.getResources().getDisplayMetrics()));
        background = typedArray.getDrawable(R.styleable.ImgTextButton_background);
        Log.d("imgText background:",""+background);

        typedArray.recycle();
        setAttrs();
    }
    /**
     * 将获得的自定义属性值设置进控件
     */
    private void setAttrs(){
        textView.setText(text);
        textView.setTextSize(textSize);
        textView.setTextColor(textColor);
        imageView.setImageResource(imageDrawable);
        imageView.setScaleType(mScaleTypeArray[imageScaleType]);//设置图片在ImageView中放缩类型
        textView.setBackground(background);//未设置时，背景为null
        imageView.setBackground(background);
        //设置显示风格（可以在不许要布局文件属性控制的情况下，设置子控件如何放置）（适合自定义tab键）
        this.setOrientation(LinearLayout.VERTICAL);
        imageLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        imageLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        imageLayoutParams.width = (int)imageWidth;//设置imageView宽度
        imageLayoutParams.height = (int)imageHeight;//imageView高度
        imageLayoutParams.topMargin = (int)imageMarginTop;//设置imageView与父布局的上间距
        imageView.setLayoutParams(imageLayoutParams);
        textLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textLayoutParams.topMargin = spacing;////设置文字与图标的上下间距
        textLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        textView.setLayoutParams(textLayoutParams);
    }
    /**
     * 设置实现了点击事件接口的子类对象
     * @param listener
     */
    public void setOnClickedListener(OnClickedListener listener){
        this.listener = listener;
//        viewId = id;
    }
    /**
     * 点击事件回调接口
     */
    public interface OnClickedListener{
        void onImgTextClick(View v);
    }

    /**
     * 重写触摸回调方法，在用户点击时，执行点击事件接口的方法onClick();
     * 1. 如何算作用户点击了这个ImgTextButton：当用户按下，和抬起时的位置坐标都落在控件范围内，
     * 并且连续滑动过程中没有离开过控件范围；即算作用户点击了该控件。
     * 2. 点击之后，如果注册了点击事件，那么执行点击事件方法。
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(isTouch){//是否可以点击按钮
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    isActionDown = true;//从该控件按下
                    setPressed(true);
                    Log.d("ImgTextButton", "action_DOWN isActionDown " + isActionDown);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(event.getX() <= 0 || event.getX() >= getWidth() || event.getY() <= 0 || event.getY() >= getHeight()){
                        isActionDown = false;//连续滑动的过程中，离开控件范围（本次点击作废）
                        Log.d("ImgTextButton", "action_MOVE isActionDown if inside " + isActionDown);
                    }
                    Log.d("ImgTextButton", "action_MOVE isActionDown " + isActionDown);
                    break;
                case MotionEvent.ACTION_CANCEL://前驱事件被父控件拦截（NestedScrollView）
                    setPressed(false);
                    isActionDown = false;//上下滑动会被父控件拦截（ACTION_MOVE判断失效）
                    Log.d("ImgTextButton", "action_CANCEL isActionDown " + isActionDown);
                    break;
                case MotionEvent.ACTION_UP:
                    if(isActionDown && listener != null){//点击条件达成，并注册了点击监听事件，执行点击事件方法
                        listener.onImgTextClick(this);
                        Log.d("ImgTextButton", "action_UP isActionDown if inside " + isActionDown);
                    }
                    Log.d("ImgTextButton", "action_UP isActionDown " + isActionDown);
                    setPressed(false);
                    break;
                default:break;
            }
            return true;
        }else{
            return super.onTouchEvent(event);
        }
    }

    /**
     * 设置文字
     * @param text
     * @return
     */
    public ImgTextButton setText(String text){
        this.text = text;
        textView.setText(text);
        return this;
    }
    /**
     * 设置ImageView的资源图片
     * @param ResourceId
     */
    public ImgTextButton setImage(int ResourceId){
        imageDrawable = ResourceId;
        imageView.setImageResource(ResourceId);
        return this;
    }

    /**
     * 设置ImageView的资源图片
     * @param drawable
     */
    public void setImage(Drawable drawable){
        imageView.setImageDrawable(drawable);
    }

    /**
     * 设置ImgTextButton是否可点击
     * @param flag
     * @return
     */
    public ImgTextButton setIsTouch(boolean flag){
        isTouch = flag;
        return this;
    }
    /**
     * 设置图片显示缩放方式
     * @param imageScaleType
     */
    public void setImageScaleType(ScaleType imageScaleType){
        imageView.setScaleType(imageScaleType);
    }
    /**
     * 设置图宽，高
     * @param wh w == h 正方形
     */
    public void setImageWH(int wh){
        imageLayoutParams.width = wh;//设置imageView宽度
        imageLayoutParams.height = wh;//imageView高度
        imageView.setLayoutParams(imageLayoutParams);
    }

    /**
     * 设置图片四面间距
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setImageMargins(int left, int top, int right, int bottom){
        imageLayoutParams.setMargins(left, top, right, bottom);
        imageView.setLayoutParams(imageLayoutParams);
    }

    /**
     * 设置文字大小
     * @param size
     */
    public void setTextSize(int size){
        textView.setTextSize(size);
    }

    /**
     * 设置文字颜色
     * @param color
     */
    public void setTextColor(int color){
        textView.setTextColor(color);
    }

    /**
     * 设置文字四面间距
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setTextMargin(int left, int top, int right, int bottom){

        textLayoutParams.setMargins(left, top, right, bottom);
        textView.setLayoutParams(textLayoutParams);
    }
}
