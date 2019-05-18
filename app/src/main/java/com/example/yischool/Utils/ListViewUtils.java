package com.example.yischool.Utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class ListViewUtils {

    public static void setListViewHeight(ListView listview, BaseAdapter adapter){
        int height = 0;
        int count = adapter.getCount();
        for(int i = 0; i < count; i++){
            View temp = adapter.getView(i, null, listview);
            temp.measure(0,0);
            height += temp.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listview.getLayoutParams();
        params.height = height;
        listview.setLayoutParams(params);
    }
}
