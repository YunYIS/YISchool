package com.example.yischool;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class CommunicativeFragment extends Fragment {

    private static CommunicativeFragment fragment = new CommunicativeFragment();

    public static CommunicativeFragment newInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_communicative, container, false);
        return view;
    }
}
