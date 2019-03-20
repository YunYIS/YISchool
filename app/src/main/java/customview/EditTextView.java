package customview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.yischool.R;

/**
 * @author 张云天
 * @date on 2019/3/17
 * @describe 自定义搜索输入框
 */
public class EditTextView extends AppCompatEditText {

    public static boolean IS_IC_CLEAN_VISIBLE = false;//清除图标是否已显示。已显示：true; 未显示：false;

    private Drawable clearDrawable;//清除图标ic_clean.xml

    public EditTextView(Context context) {
        super(context);
        init();
    }

    public EditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 控件相关初始化操作
     */
    public void init(){
        clearDrawable = getResources().getDrawable(R.drawable.ic_clean);
        //在EditText上、下、左、右设置图标;传入的Drawable的宽高为固有宽高
        setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search,0,
                0, 0);
    }

    /**
     * 自定义点击清除图标时的事件响应，点击清除图标，清空输入框内容
     * getIntrinsicWidth()获取Drawable的固有宽高（当前48px）
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            //当手指抬起的位置在清除图标的区域，则视为点击了清除图标
            case MotionEvent.ACTION_UP:
                Drawable drawable = clearDrawable;
                //控件宽 - rightPadding - 清除图标宽 <= getX <= 控件宽 - rightPadding
                if (IS_IC_CLEAN_VISIBLE && event.getX() <= (getWidth() - getPaddingRight())
                        && event.getX() >= (getWidth() - getPaddingRight() - drawable.getIntrinsicWidth())) {
                    setText("");
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 当输入框内容变化时，调用该方法。判断输入框内容长度是否为0：若不为0，则设置清除图标；若为0，则不设置清除图标。
     * @param text 输入框内容字符队列
     */
    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        //设置清除图标已显示标志位
        if(text.length()>0){
            IS_IC_CLEAN_VISIBLE = true;
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0,
                     R.drawable.ic_clean , 0);
        }else {
            IS_IC_CLEAN_VISIBLE = false;
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, 0 , 0);
        }
    }
}
