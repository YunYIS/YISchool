package com.example.yischool.mainFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yischool.R;


public class PublishFragment extends Fragment {

    private static PublishFragment publishFragment;

    public static PublishFragment newInstance() {
        if(publishFragment == null){
            publishFragment = new PublishFragment();
        }
        return publishFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_publish, container, false);
        return view;
    }


}
