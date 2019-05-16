package com.example.yischool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.yischool.register.ForgetPasswordActivity;
import com.example.yischool.register.RegisterActivity;

import org.raphets.roundimageview.RoundImageView;

import java.util.regex.Pattern;

import com.example.yischool.Bean.ServerDatabaseBean.User;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

import static com.example.yischool.Utils.ToastUtils.toastMessage;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    public static final int REGISTER_REQUEST_CODE = 1;
    public final Context CONTEXT = LoginActivity.this;

    private ImageView backButton;
    private RoundImageView circleHeadImage;
    private EditText accountEditView;
    private EditText passwordEditView;
    private CheckBox rememberPasswordCheckbox;
    private Button loginButton;
    private Button registerButton;
    private Button forgetPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //初始化控件
        backButton = findViewById(R.id.back_button);
        circleHeadImage = findViewById(R.id.circle_head_image);
        accountEditView = findViewById(R.id.account_phone_edit_view);
        passwordEditView = findViewById(R.id.password_edit_view);
        rememberPasswordCheckbox = findViewById(R.id.remember_password_checkbox);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);
        forgetPasswordButton = findViewById(R.id.forget_password_button);

        backButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        forgetPasswordButton.setOnClickListener(this);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.back_button:
                finish();
                break;
            case R.id.login_button:
                userLogin();
                break;
            case R.id.register_button:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivityForResult(intent, REGISTER_REQUEST_CODE, null);
                break;
            case R.id.forget_password_button:
                Intent intent1 = new Intent(this, ForgetPasswordActivity.class);
                startActivity(intent1);
                break;
            default:break;
        }
    }

    /**
     * 用户登录，区分手机号和用户名登录
     * 如果登录成功，自动跳转，否则，只提示错误信息
     */
    private void userLogin(){
        String account = getAndCheckPhoneNumber();
        final String password = getAndCheckPassword();
        if(password != null){
            if(account != null){//说明account为手机号
                BmobUser.loginByAccount(account, password, new LogInListener<User>() {
                    @Override
                    public void done(User user, BmobException e) {
                        if(user != null){
                            if (e == null) {
                                successLogin(user, password);
                            } else {
                                toastMessage(CONTEXT, "登录失败,用户名或密码错误");
                            }
                        }
                    }
                });
            } else {
                account = getAndCheckAccountName();
                if(account != null){//说明account为用户名
                    BmobUser.loginByAccount(account, password, new LogInListener<User>() {
                        @Override
                        public void done(User user, BmobException e) {
                            if(user != null){
                                if (e == null) {
                                    successLogin(user, password);
                                } else {
                                    toastMessage(CONTEXT, "登录失败,用户名或密码错误");
                                }
                            }
                        }
                    });
                }else {
                    toastMessage(CONTEXT,"用户名/手机号错误");
                }
            }
        }

    }
    private void successLogin(User user, String password){
        toastMessage(CONTEXT, "登录成功");
        InitApplication.setCurrentUser(user);//当前登录用户
        Boolean isRememberPassword = rememberPasswordCheckbox.isChecked();//是否记住密码
        //保存用户信息,下次进入自动登陆（所以只本地保存（用户名，密码，手机号，以及记住密码等基本数据））
        SharedPreferences sharedPreferences = getSharedPreferences("LoginUserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", user.getUsername());
        editor.putString("mobilePhone", user.getMobilePhoneNumber());
        editor.putString("password", password);
        editor.putBoolean("isRememberPassword", isRememberPassword);
        editor.apply();
        Intent intent = new Intent(CONTEXT, MainActivity.class);
        startActivity(intent);
    }
    /**
     * 获取输入并校验手机号输入格式是否正确
     * @return 返回正确格式的手机号, 若返回null，则说明输入的手机号不正确
     */
    private String getAndCheckPhoneNumber(){
        Editable editable = accountEditView.getText();
        if(editable == null){
            return null;
        }else{
            String phoneNumber = editable.toString();
            if(phoneNumber.length() == 0){
                return null;
            }else{
                if(Pattern.matches(RegisterActivity.REGEX_PHOTO_NUMBER, phoneNumber)){
                    return phoneNumber;
                }else{
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
                if(Pattern.matches(RegisterActivity.REGEX_PASSWORD, password)){
                    return password;
                }else{
                    toastMessage(CONTEXT,"密码格式错误");
                    return null;
                }
            }
        }
    }
    /**
     * 获取并校验用户名
     * @return
     */
    private String getAndCheckAccountName(){
        final String REGEX_ACCOUNT_NAME = "^[\u4e00-\u9fa5_]\\w{2,15}$";
        Editable editable = accountEditView.getText();
        if(editable != null){
            String accountName = editable.toString();
            if(accountName.length() == 0){
                return null;
            }else{
                if(Pattern.matches(REGEX_ACCOUNT_NAME, accountName)){
                    return accountName;
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
        Log.d("LoginActivity", "AccountDataActivity destroy, SingleTask mainActivity");
    }
}
