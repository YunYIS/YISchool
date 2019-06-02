package com.example.yischool.mainFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.yischool.R;
import com.example.yischool.adapter.SessionListAdapter;

import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;


public class CommunicativeFragment extends Fragment {

    private static CommunicativeFragment communicativeFragment;

    private ListView sessionListView;
    private SessionListAdapter adapter;
    private BmobIMUserInfo userInfo;//用户信息
    private BmobIMConversation conversation;


    public static CommunicativeFragment newInstance() {
        if(communicativeFragment == null){
            communicativeFragment = new CommunicativeFragment();
        }
        return communicativeFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_communicative, container, false);

        sessionListView = view.findViewById(R.id.session_list_view);


        return view;
    }
}
