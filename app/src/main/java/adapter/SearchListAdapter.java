package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Iterator;

/**
 * @author 张云天
 * @date on 2019/3/19
 * @describe 自定义Search history ListView Adapter
 * item布局使用Android系统自带布局simple_list_item_1
 * 主要为了实现使用HashSet作为数据源，因为搜索历史记录不能重复
 */
public class SearchListAdapter extends BaseAdapter {

    private HashSet<String> dataSet;
    private Context context;

    public SearchListAdapter(Context context, HashSet<String> dataSet){
        this.dataSet = dataSet;
        this.context = context;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public Object getItem(int position) {
        String retStr = null;
        //访问集合position位置的元素
        Iterator<String> iterator = dataSet.iterator();
        for(int i = 0; i < position; i++){
            iterator.next();
        }
        if(iterator.hasNext()){
            retStr = iterator.next();
        }
        return retStr;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent,false);
        TextView textView = v.findViewById(android.R.id.text1);
        textView.setText((String)getItem(position));
        return v;
    }
}
