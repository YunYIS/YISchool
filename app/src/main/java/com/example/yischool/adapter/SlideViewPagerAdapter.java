package com.example.yischool.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class SlideViewPagerAdapter extends PagerAdapter {

    private static final int MAX_COUNT = 1000;//以一个很大的值实现轮播，ViewPager自有缓存机制

    private Context context;
    private List<ImageView> imageViews;

    public SlideViewPagerAdapter(Context context, List<ImageView> imageViews){
        this.imageViews = imageViews;
        this.context = context;
    }
    @Override
    public int getCount() {
       return MAX_COUNT;
    }
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Log.d("SlideAdapter", "position " + position);
        position %= imageViews.size();//在滑动到最后一张图片时，继续向同一方向滑动可以重复播放
        Log.d("SlideAdapter", "position % after " + position);
        ImageView imageView = imageViews.get(position);
        ViewGroup parent = (ViewGroup) imageView.getParent();
        if(parent != null){//避免IllegalStateException（由于切换Fragment引起的onCreateView调用）
            parent.removeView(imageView);
        }
        container.addView(imageView);
        return imageView;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
