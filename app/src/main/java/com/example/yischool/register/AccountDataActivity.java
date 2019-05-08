package com.example.yischool.register;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.yischool.InitApplication;
import com.example.yischool.MainActivity;
import com.example.yischool.R;
import com.google.gson.reflect.TypeToken;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import Bean.ServerDatabaseBean.User;
import Bean.jsonBean.CityBean;
import Bean.jsonBean.SchoolBean;
import Utils.JSONUtils;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import customview.EditSpinner;

import static Utils.ToastUtils.toastMessage;
import static com.example.yischool.InitApplication.getAppContext;

/**
 * @author 张云天
 * date on 2019/4/23
 * describe: 注册流程第二个页面，必填项（三个）和选填信息，功能逻辑
 */
public class AccountDataActivity extends AppCompatActivity {

    public static final String TAG = "AccountDataActivity";
    public final Context CONTEXT = AccountDataActivity.this;

    private ImageView backButton;//返回按钮
    private AppCompatEditText accountNameEdit;//用户名编辑
    private EditSpinner schoolEditSpinner;//学校选择编辑
    private EditSpinner majorEditSpinner;//专业选择编辑
    private EditText emailEdit;//邮箱编辑
    private EditText birthdayEdit;//生日（日期）选择器
    private RadioGroup sexRadioGroup;
    private EditText hobbiesEdit;//喜欢标签编辑
    private View addButton;//添加喜欢标签
    private Button completeButton;//完成注册按钮
    private TagFlowLayout labelDisplayLayout;//按钮展示布局
    private View hintView1;//提示小图标（必填项提示）
    private View hintView2;
    private View hintView3;

    private Snackbar snackbar;//提示删除标签
    private List<String> labelStrings = new ArrayList<>();//标签展示数据源
    private Date userBirthday;//用户生日

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_data);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        backButton = findViewById(R.id.back_button);
        accountNameEdit = findViewById(R.id.account_edit_view);
        schoolEditSpinner = findViewById(R.id.school_edit_view);
        majorEditSpinner = findViewById(R.id.major_edit_view);
        emailEdit = findViewById(R.id.email_edit_view);
        birthdayEdit = findViewById(R.id.birthday_edit_view);
        sexRadioGroup = findViewById(R.id.sex_radio_group);
        hobbiesEdit = findViewById(R.id.hobbies_edit_view);
        addButton = findViewById(R.id.add_label_button);
        completeButton = findViewById(R.id.complete_button);
        labelDisplayLayout = findViewById(R.id.label_display_layout);
        hintView1 = findViewById(R.id.view_hint_1);
        hintView2 = findViewById(R.id.view_hint_2);
        hintView3 = findViewById(R.id.view_hint_3);

        schoolEditSpinner.initData(getSchoolData(),hintView2);
        majorEditSpinner.initData(getMajorData(),hintView3);
        //返回按钮点击事件，返回上一步，不保存临时数据
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        accountNameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    String s = getAndCheckAccountName();
                    if(s.equals("0") || s.equals("1")){
                        hintView1.setBackgroundResource(R.drawable.ic_required_hint);
                    }else{
                        hintView1.setBackgroundResource(R.drawable.ic_correct);
                    }
                }
            }
        });
        //生日编辑器，点击弹出DatePickerDialog选择日期
        birthdayEdit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = new DatePickerDialog(AccountDataActivity.this,AlertDialog.THEME_HOLO_DARK);
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatePicker datePicker = datePickerDialog.getDatePicker();
                                int year = datePicker.getYear();
                                int month = datePicker.getMonth()+1;
                                int day = datePicker.getDayOfMonth();
                                userBirthday = new Date(year, month, day);
                                birthdayEdit.setText(year+"-"+month+"-"+day);
                            }
                        });
                datePickerDialog.show();
            }
        });
        final TagAdapter tagAdapter = new TagAdapter<String>(labelStrings) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView textView = new TextView(CONTEXT);
                textView.setBackgroundResource(R.drawable.label_selector);
                textView.setTextColor(Color.BLACK);
                //字体大小8sp
//                textView.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 8,CONTEXT.getResources().getDisplayMetrics()));
                textView.setTextSize(12f);
                textView.setText(s);
                return textView;
            }
        };
        labelDisplayLayout.setAdapter(tagAdapter);
        //添加标签按钮点击事件，添加用户输入的标签在下面ScrollView布局中显示
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hobbiesEdit.getText() != null){
                    final String labelString = hobbiesEdit.getText().toString();
                    if(labelString.length() > 0){
                        labelStrings.add(labelString);
                        tagAdapter.notifyDataChanged();
                        hobbiesEdit.setText("");
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    }
                }
            }
        });
        //使用提示，删除选中的标签
        labelDisplayLayout.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(final Set<Integer> selectPosSet) {
                if(selectPosSet != null && selectPosSet.size() > 0){
                    if(snackbar == null){//因为有该if判断，所以setAction不能写在此处
                        snackbar = Snackbar.make(labelDisplayLayout, "是否删除所选中的标签", Snackbar.LENGTH_INDEFINITE);
                    }
                    snackbar.setAction("是", new View.OnClickListener() {//点击事件必须写在此处，不然selectPosSet变化时，遍历的Set不会变化
                        @Override
                        public void onClick(View v) {
                            //删除选中的标签
                            List<String> tempList = new ArrayList<>();
                            for(Integer i : selectPosSet){
                                tempList.add(labelStrings.get((int)i));
                                Log.d(TAG, "SnackBar onClick selectSet size=" + selectPosSet.size());
                                Log.d(TAG, "SnackBar onClick" + labelStrings.size() +" i="+ i);
                            }
                            labelStrings.removeAll(tempList);
                            tagAdapter.notifyDataChanged();
                            snackbar.dismiss();
                        }
                    });
                    Log.d(TAG, "onSelected executed! selectSet size=" + selectPosSet.size());
                    if(!snackbar.isShown()){//因为有该if判断，所以setAction不能写在此处
                        snackbar.show();
                    }
                }else{
                    if(snackbar.isShown()){
                        snackbar.dismiss();
                    }
                }
            }
        });
        //完成按钮点击事件，获取并校验输入框已输入的值，上传后台数据库，验证注册是否成功
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                User user = (User) intent.getSerializableExtra("temp_user");
                String accountName = getAndCheckAccountName();
                //用户名，学校，专业这三个是必填项
                if("0".equals(accountName)){
                    toastMessage(CONTEXT, "用户名为空");
                }else if(accountName.equals("1")){
                    toastMessage(CONTEXT, "用户名格式错误");
                }else {
                    user.setUsername(accountName);
                    if(!schoolEditSpinner.CONTENT_CORRECT){
                        toastMessage(CONTEXT, "学校名错误");
                    }else{
                        user.setCollege(schoolEditSpinner.getText());
                        if(!majorEditSpinner.CONTENT_CORRECT){
                            toastMessage(CONTEXT, "专业名错误");
                        }else{
                            user.setMajor(majorEditSpinner.getText());
                            //至此三个必填项已正确填写，其余项选填
                            if(getAndCheckEmail() != null){//如果用户填写了正确格式的邮箱，则保存，否则，忽略
                                user.setEmail(getAndCheckEmail());
                            }
                            if(userBirthday != null){//选填
                                user.setBirthday(new BmobDate(userBirthday));
                            }
                            if(sexRadioGroup.getCheckedRadioButtonId() != -1){//选填
                                if(sexRadioGroup.getCheckedRadioButtonId() == R.id.male_radio_button){
                                    user.setSex("男");
                                }else if(sexRadioGroup.getCheckedRadioButtonId() == R.id.female_radio_button){
                                    user.setSex("女");
                                }
                            }
                            if(labelStrings.size() > 0){
                                user.setHobbies(labelStrings);
                            }
                            Log.d(TAG, user.toString());
                            user.signUp(new SaveListener<User>() {
                                @Override
                                public void done(User user, BmobException e) {
                                    if(e == null){
                                        InitApplication.setCurrentUser(user);//设置全局变量当前登陆用户
                                        toastMessage(CONTEXT, "注册成功！");
                                        Intent completeIntent = new Intent(CONTEXT, MainActivity.class);
                                        startActivity(completeIntent);
                                    }else{
                                        if(e.getErrorCode() == 202){
                                            toastMessage(CONTEXT, "用户名已存在");
                                        }else{
                                            toastMessage(CONTEXT, "注册失败");
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }
    /**
     * 获取本地学校数据源
     * @return
     */
    private List<String> getSchoolData(){
        String jsonData = JSONUtils.getLocalData(getAppContext(), "city_schools.json");
        List<CityBean> cityBeans = JSONUtils.resolveJsonArray(new TypeToken<List<CityBean>>(){}.getType(), jsonData);
        Log.d(TAG, "json: "+jsonData);
        Log.d(TAG, cityBeans == null ? "null" : ""+cityBeans.size());
        List<String> allDataList = new ArrayList<>();
        //按城市—学校遍历json数据
        if(cityBeans != null && cityBeans.size() > 0){
            for(CityBean cityBean : cityBeans){
                List<SchoolBean> schoolBeans = cityBean.getSchool();
                for(SchoolBean schoolBean : schoolBeans){
                    allDataList.add(schoolBean.getName());
                }
            }
        }
        return allDataList;
    }
    /**
     * 获取本地专业数据源
     * @return
     */
    private List<String> getMajorData(){
        String jsonData = JSONUtils.getLocalData(getAppContext(), "majors.json");
        List<String> majors = JSONUtils.resolveJsonArray(new TypeToken<List<String>>(){}.getType(), jsonData);
        Log.d(TAG, "json: "+jsonData);
        Log.d(TAG, majors == null ? "null" : ""+majors.size());
        return majors;
    }
    /**
     * 获取并校验用户名
     * @return
     */
    private String getAndCheckAccountName(){
        final String REGEX_ACCOUNT_NAME = "^[\u4e00-\u9fa5_]\\w{2,15}$";
        Editable editable = accountNameEdit.getText();
        if(editable != null){
            String accountName = editable.toString();
            if(accountName.length() == 0){
                return "0";
            }else{
                if(Pattern.matches(REGEX_ACCOUNT_NAME, accountName)){
                    return accountName;
                }else{
                    return "1";
                }
            }
        }
        return "0";
    }
    /**
     * 获取并校验邮箱
     * @return null 表示未填或格式错误
     */
    private String getAndCheckEmail(){
        final String REGEX_EMAIL = "^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)$";
        Editable editable = emailEdit.getText();
        if(editable != null){
            String email = editable.toString();
            if(email.length() == 0){
                return null;
            }else{
                if(Pattern.matches(REGEX_EMAIL, email)){
                    return email;
                }else{
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "AccountDataActivity destroy, SingleTask mainActivity");
    }
}
