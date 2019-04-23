package com.example.yischool.mainFragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yischool.R;

public class PersonalFragment extends Fragment {

    private static PersonalFragment personalFragment;

    public static PersonalFragment newInstance(){
        if(personalFragment == null){
            personalFragment = new PersonalFragment();
        }
        return personalFragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        return view;
    }
}
