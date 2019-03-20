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

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yischool.R;

public class ImgTextButton extends LinearLayout {

    private Context context;
    private ImageView imageView;
    private TextView textView;

    //自定义属性
    private String text;
    private float textSize;
    private int textColor;
    private int textColorPress;
    private int imageDrawable;
    private int spacing;
    private Drawable background;

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
        textColorPress = typedArray.getColor(R.styleable.ImgTextButton_textColorPress, Color.BLACK);
        imageDrawable = typedArray.getResourceId(R.styleable.ImgTextButton_imageDrawable, R.drawable.ic_launcher_foreground);
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
        textView.setBackground(background);//未设置时，背景为null
        imageView.setBackground(background);
        //设置显示风格（可以在不许要布局文件属性控制的情况下，设置子控件如何放置）（适合自定义tab键）
        this.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        imageView.setLayoutParams(lp);
        lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.topMargin = spacing;////设置文字与图标的上下间距
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        textView.setLayoutParams(lp);
    }

    public ImgTextButton setText(String text){
        this.text = text;
        textView.setText(text);
        return this;
    }
    public ImgTextButton setImage(int ResourceId){
        imageDrawable = ResourceId;
        imageView.setImageResource(ResourceId);
        return this;
    }
    public void setTextPressColor(){
        textView.setTextColor(textColorPress);
    }
}
