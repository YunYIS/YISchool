package com.example.yischool.mainFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yischool.R;


public class CommunicativeFragment extends Fragment {

    private static CommunicativeFragment communicativeFragment;

    public static CommunicativeFragment newInstance() {
        if(communicativeFragment == null){
            communicativeFragment = new CommunicativeFragment();
        }
        return communicativeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_communicative, container, false);
        return view;
    }
}
