package com.example.yischool.mainFragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.yischool.LoginActivity;
import com.example.yischool.R;

/**
 * @author 张云天
 * date on 2019/4/6
 * describe: 尚未登陆时的提示页面
 */
public class LoginHintFragment extends Fragment {

    private static LoginHintFragment loginHintFragment;
    private Context context;
    private Button toLoginButton;
    public LoginHintFragment(){}

    public static Fragment newInstance(){
        if(loginHintFragment == null){
            loginHintFragment = new LoginHintFragment();
        }
        return loginHintFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_hint, container, false);
        toLoginButton = view.findViewById(R.id.to_login_button);
        toLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

}
