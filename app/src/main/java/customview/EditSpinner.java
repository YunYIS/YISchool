package customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.example.yischool.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 张云天
 * date on 2019/4/18
 * describe: 组合控件，输入框自动搜索，弹出匹配成功的数据项，
 *  失去焦点时对输入数据进行校验，匹配成功，则CONTENT_CORRECT=true，可以通过获取标志位，来判断输入数据是否正确（例如，输入的学校是否存在）
 */
public class EditSpinner extends LinearLayout {

    public static final String TAG = "EditSpinner";
    public boolean CONTENT_CORRECT = false;//校验输入内容

    private EditText editText;
    private ListView listView;
    private PopupWindow mPopupWindow ;
    private View hintView;//提示小图标（不包含在组合控件中）由参数传递

    private ArrayAdapter listViewAdapter;
    private List<String> allDataList;//所有学校数据

    private String hint;//提示内容
    private int textHintColor;//提示内容颜色
    private float textSize;//字体大小

    public EditSpinner(Context context) {
        super(context);
        initView(null);
    }
    public EditSpinner(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }
    public EditSpinner(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }
    /**
     * 初始化子View
     * @param attrs
     */
    private void initView(AttributeSet attrs){
        //获取自定义属性
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.EditSpinner);
        hint = typedArray.getString(R.styleable.EditSpinner_hint);
        textHintColor = typedArray.getColor(R.styleable.EditSpinner_textColorHint, Color.WHITE);
        textSize = typedArray.getDimension(R.styleable.EditSpinner_editTextSize, 12);
        typedArray.recycle();
        //设置属性，和子View布局参数
        editText = new EditText(getContext());
        editText.setHint(hint);
        editText.setHintTextColor(textHintColor);
        editText.getPaint().setTextSize(textSize);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        addView(editText, lp);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>0){
                    matchAndShowPopupWindow(charSequence.toString());//根据输入匹配数据项
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "onFocusChange execute! hasFocus: " + hasFocus);
                if(!hasFocus){
                    if(editText.getText() != null){
                        String contentStr = editText.getText().toString();
                        if(contentStr.length() > 0){
                            for(String str : allDataList){
                                if(str.equals(contentStr)){
                                    CONTENT_CORRECT = true;
                                    hintView.setBackgroundResource(R.drawable.ic_correct);
                                }
                            }
                        }
                    }
                } else {
                    CONTENT_CORRECT = false;
                    hintView.setBackgroundResource(R.drawable.ic_required_hint);
                }
            }
        });
    }
    /**
     * 初始化数据
     * @param allDataList 数据源
     * @param view
     */
    public void initData(List<String> allDataList, View view){
        if(allDataList != null){
            this.allDataList = allDataList;
        }else{
            this.allDataList = new ArrayList<>();
        }
        if(view != null){
            hintView = view;
        }else{
            throw new NullPointerException();
        }

    }
    /**
     * 初始化PopupWindow
     * @param size 弹出的数据大小
     * @return
     */
    private PopupWindow initPopupWindow(final List<String> matchDataList, int size){

        //初始化列表数据
        listView = new ListView(getContext());
        listViewAdapter = new ArrayAdapter<>(getContext(), R.layout.simple_list_item, matchDataList);
        listView.setAdapter(listViewAdapter);
        //点击某一条
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                String item = matchDataList.get(i);
                editText.setText(item);
                editText.setSelection(item.length());//光标放到最后
                mPopupWindow.dismiss();
            }
        });

        PopupWindow popupWindow = new PopupWindow(getContext());
        popupWindow.setWidth(editText.getMeasuredWidth());//popupWindow width和editView宽度相同
        if (size>4) {
            Log.d(TAG, "listView count: " + listView.getChildCount() + " adapter count: " + listViewAdapter.getCount());
            //获取ListView item的高度
            View itemView = listViewAdapter.getView(0, null, listView);
            itemView.measure(0, 0);
            int height = itemView.getMeasuredHeight();
            Log.d(TAG, "listView item height: " + height);
            popupWindow.setHeight(4 * height);//最多显示4个item高度
        } else {
            popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        popupWindow.setContentView(listView);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xACFFFFFF));
        //解决popupWindow遮住输入法问题
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(false);

        return popupWindow;
    }

    /**
     * 将EditView中输入的内容匹配，并弹出匹配项
     * @param contentStr 搜索内容
     */
    private void matchAndShowPopupWindow(String contentStr){

        List<String> matchDataList = new ArrayList<>();
        for(String str : allDataList){
            if(str.contains(contentStr)){
                matchDataList.add(str);
            }
        }
        if(mPopupWindow != null){
            mPopupWindow.dismiss();//关闭老的PopupWindow
        }
        if(matchDataList.size() > 0){
            mPopupWindow = initPopupWindow(matchDataList, matchDataList.size());
            mPopupWindow.showAsDropDown(editText);
        }
    }
    /**
     * 获取输入框中的内容
     * @return 没有内容则返回 ""
     */
    public String getText(){
        if(editText.getText() != null){
            return editText.getText().toString();
        }else{
            return "";
        }
    }
}
