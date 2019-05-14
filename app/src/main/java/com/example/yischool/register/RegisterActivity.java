package com.example.yischool.register;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.yischool.R;

import java.util.regex.Pattern;

import Bean.ServerDatabaseBean.User;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

import static Utils.ToastUtils.toastMessage;

/**
 * @author 张云天
 * date on 2019/4/6
 * describe: 注册流程第一个活动，验证手机号，并输入密码，进入下一步
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String LOG_TAG = "RegisterActivity";
    public final Context CONTEXT= RegisterActivity.this;
    public static final String REGEX_PHOTO_NUMBER =
            "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";//手机号验证正则表达式
    public static final String REGEX_PASSWORD = "^[a-zA-Z0-9]{8,15}$";//密码（大小写字母+数字，8-15位）

    private User user;//保存验证成功用户信息
    private String phoneNumber;
    private String password;
    private String verifyCode;

    private EditText telephoneEditView;
    private EditText passwordEditView;
    private EditText verificationCodeEditView;
    private ImageView backButton;
    private ImageView displayPasswordBtn;
    private Button getCodeButton;
    private Button nextButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        telephoneEditView = findViewById(R.id.telephone_edit_view);
        passwordEditView = findViewById(R.id.password_edit_view);
        verificationCodeEditView = findViewById(R.id.verification_code_edit_view);
        getCodeButton = findViewById(R.id.get_code_button);
        nextButton = findViewById(R.id.next_button);
        backButton = findViewById(R.id.back_button);
        displayPasswordBtn = findViewById(R.id.display_password_image_button);

        displayPasswordBtn.setOnClickListener(this);
        getCodeButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_button:
                finish();
                break;
            case R.id.get_code_button:
                phoneNumber = getAndCheckPhoneNumber();
                if(phoneNumber != null){//不为空，则说明手机号格式正确
                    sendVerifyCode(phoneNumber);
                }
                break;
            case R.id.display_password_image_button:
                if(passwordEditView.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD){
                    displayPasswordBtn.setImageResource(R.drawable.ic_display_password);
                    passwordEditView.setInputType(129);
                }else{
                    displayPasswordBtn.setImageResource(R.drawable.ic_hidden_password);
                    passwordEditView.setInputType(128);
                }
                //光标放到最后
                if(passwordEditView.getText() != null){
                    passwordEditView.setSelection(passwordEditView.getText().toString().length());
                }
                break;
            case R.id.next_button:
                password = getAndCheckPassword();
                verifyCode = getAndCheckVerifyCode();
                if(phoneNumber == null){//考虑到不点击获取验证码按钮，直接点击下一步的情况
                    toastMessage(CONTEXT, "手机号错误");
                }
                if(phoneNumber != null && password != null && verifyCode != null){//不为空，则说明三个输入数据格式都正确
                    checkAndSave(phoneNumber, verifyCode, password);
                    if(user != null){//验证成功，则跳转下个活动继续完善用户信息
                        //临时保存用户信息
                        SharedPreferences sharedPreferences = getSharedPreferences("LoginUserData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("mobilePhone", user.getMobilePhoneNumber());
                        editor.putString("password", password);
                        editor.apply();
                        //跳转
                        Intent intent = new Intent(RegisterActivity.this, AccountDataActivity.class);
                        intent.putExtra("temp_user", user);//将该活动页面的用户信息传递到下一个活动
                        startActivity(intent);
                    }
                }
                break;
            default:break;
        }
    }
    /**
     * 获取输入并校验手机号输入格式是否正确
     * @return 返回正确格式的手机号, 若返回null，则说明输入的手机号不正确
     */
    private String getAndCheckPhoneNumber(){
        Editable editable = telephoneEditView.getText();
        if(editable == null){
            toastMessage(CONTEXT,"请输入手机号");
            return null;
        }else{
            String phoneNumber = editable.toString();
            if(phoneNumber.length() == 0){
                toastMessage(CONTEXT,"请输入手机号");
                return null;
            }else{
                if(Pattern.matches(REGEX_PHOTO_NUMBER, phoneNumber)){
                    return phoneNumber;
                }else{
                    toastMessage(CONTEXT,"手机号格式错误");
                    return null;
                }
            }
        }
    }

    /**
     * 获取输入并校验密码输入格式是否正确
     * @return 返回正确格式的密码, 若返回null，则说明输入的密码格式不正确
     */
    private String getAndCheckPassword(){
        Editable editable = passwordEditView.getText();
        if(editable == null){
            toastMessage(CONTEXT,"请输入密码");
            return null;
        }else{
            String password = editable.toString();
            if(password.length() == 0){
                toastMessage(CONTEXT,"请输入密码");
                return null;
            }else{
                if(Pattern.matches(REGEX_PASSWORD, password)){
                    return password;
                }else{
                    toastMessage(CONTEXT,"密码格式错误");
                    return null;
                }
            }
        }
    }

    /**
     * 获取输入并校验验证码输入格式是否正确
     * @return 返回正确格式的验证码；若返回null，则说明输入的验证码格式不正确
     */
    private String getAndCheckVerifyCode(){
        Editable editable = verificationCodeEditView.getText();
        if(editable == null){
            toastMessage(CONTEXT,"请输入验证码");
            return null;
        }else {
            String verifyCode = editable.toString();
            if(verifyCode.length() != 6){
                toastMessage(CONTEXT,"验证码错误");
                return null;
            }else {
                return verifyCode;
            }
        }
    }
    /**
     * 发送验证码，若发送成功，则Button显示倒数计时，并且不能再次点击
     * @param phoneNumber 手机号
     */
    private void sendVerifyCode(String phoneNumber){

        BmobSMS.requestSMSCode(phoneNumber, "YIS注册验证", new QueryListener<Integer>() {
            @Override
            public void done(Integer smsId, BmobException e) {
                if (e == null) {
                    toastMessage(CONTEXT,"发送验证码成功");
                    VerifyCodeCountTimer verifyCodeCountTimer = new VerifyCodeCountTimer(180000, 1000);
                    verifyCodeCountTimer.start();
                } else {
                    toastMessage(CONTEXT,"发送验证码失败");
                    Log.d(LOG_TAG, "发送验证码失败 "+e.getErrorCode()+" "+e.getMessage());
                }
            }
        });
    }
    /**
     * 首先检查验证码是否正确（服务器端）。若正确：绑定手机号，服务器存储用户信息；否则提示错误信息
     * @param phoneNumber
     * @param code
     */
    private void checkAndSave(final String phoneNumber, String code, final String password){
        BmobSMS.verifySmsCode(phoneNumber, code, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.d(LOG_TAG, "验证码验证成功，在此时进行绑定操作:");
                    user = new User();
                    user.setMobilePhoneNumber(phoneNumber);
                    user.setMobilePhoneNumberVerified(true);
                    user.setPassword(password);
                } else {
                    Log.d(LOG_TAG,"验证码验证失败：" + e.getErrorCode() + "-" + e.getMessage() + "\n");
                    if(e.getErrorCode() == 207){
                        toastMessage(CONTEXT,"验证码错误");
                    }else if(e.getErrorCode() == 209){
                        toastMessage(CONTEXT,"该手机号码已经存在");
                    }
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "AccountDataActivity destroy, SingleTask mainActivity");
    }

    class VerifyCodeCountTimer extends CountDownTimer{

        public VerifyCodeCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程
            getCodeButton.setClickable(false);
            int ret = (int) (millisUntilFinished / 1000);
            getCodeButton.setText(""+ret);
        }
        @Override
        public void onFinish() {//计时结束
            getCodeButton.setText("获取验证码");
            getCodeButton.setClickable(true);
        }
    }
}
                  