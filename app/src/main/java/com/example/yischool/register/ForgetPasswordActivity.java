package com.example.yischool.register;

import android.content.Context;
import android.content.Intent;
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

import com.example.yischool.LoginActivity;
import com.example.yischool.R;

import java.util.regex.Pattern;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.example.yischool.Utils.ToastUtils.toastMessage;

/**
 * @author 张云天
 * date on 2019/4/23
 * describe: 忘记密码页面功能逻辑
 */
public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String LOG_TAG = "ForgetPasswordActivity";
    public final Context CONTEXT = ForgetPasswordActivity.this;

    private ImageView backButton;
    private EditText telephoneEditView;
    private EditText passwordEditView;
    private ImageView displayPasswordImageButton;
    private EditText verificationCodeEditView;
    private Button getCodeButton;
    private Button nextButton;

    private String phoneNumber;
    private String newPassword;
    private String verifyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        backButton = findViewById(R.id.back_button);
        telephoneEditView = findViewById(R.id.telephone_edit_view);
        passwordEditView = findViewById(R.id.password_edit_view);
        displayPasswordImageButton = findViewById(R.id.display_password_image_button);
        verificationCodeEditView = findViewById(R.id.verification_code_edit_view);
        getCodeButton = findViewById(R.id.get_code_button);
        nextButton = findViewById(R.id.next_button);

        backButton.setOnClickListener(this);
        displayPasswordImageButton.setOnClickListener(this);
        getCodeButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
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
                    displayPasswordImageButton.setImageResource(R.drawable.ic_display_password);
                    passwordEditView.setInputType(129);
                }else{
                    displayPasswordImageButton.setImageResource(R.drawable.ic_hidden_password);
                    passwordEditView.setInputType(128);
                }
                //光标放到最后
                if(passwordEditView.getText() != null){
                    passwordEditView.setSelection(passwordEditView.getText().toString().length());
                }
                break;
            case R.id.next_button:
                newPassword = getAndCheckPassword();
                verifyCode = getAndCheckVerifyCode();
                if(phoneNumber == null){//考虑到不点击获取验证码按钮，直接点击下一步的情况
                    toastMessage(CONTEXT, "手机号错误");
                }
                if(phoneNumber != null && newPassword != null && verifyCode != null){//不为空，则说明三个输入数据格式都正确
                    checkAndReset(verifyCode, newPassword);//验证，修改密码和跳转
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
                if(Pattern.matches(RegisterActivity.REGEX_PHOTO_NUMBER, phoneNumber)){
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
            String newPassword = editable.toString();
            if(newPassword.length() == 0){
                toastMessage(CONTEXT,"请输入密码");
                return null;
            }else{
                if(Pattern.matches(RegisterActivity.REGEX_PASSWORD, newPassword)){
                    return newPassword;
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
     * 首先检查验证码是否正确（服务器端）。若正确：执行验证码的密码重置操作,并提示信息和跳转：；否则提示错误信息
     * @param code 验证码
     * @param newPassword 新密码
     */
    private void checkAndReset(String code, final String newPassword){
        BmobUser.resetPasswordBySMSCode(code, newPassword, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    toastMessage(CONTEXT, "修改密码成功");
                    Intent intent = new Intent(CONTEXT, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    toastMessage(CONTEXT,"修改密码失败");
                }
            }
        });
    }

    class VerifyCodeCountTimer extends CountDownTimer {

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
