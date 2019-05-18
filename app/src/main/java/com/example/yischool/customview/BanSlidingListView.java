package com.example.yischool.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * @author 张云天
 * date on 2019/5/16
 * describe: 禁止滑动ListView，解决嵌套滑动冲突
 */
public class BanSlidingListView extends ListView {


    public BanSlidingListView(Context context) {
        super(context);
    }

    public BanSlidingListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BanSlidingListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置不滚动
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

    }

}
