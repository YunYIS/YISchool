package com.example.yischool.mainFragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yischool.R;

public class ShoppingCartFragment extends Fragment {

    private static ShoppingCartFragment shoppingCartFragment = new ShoppingCartFragment();

    public static ShoppingCartFragment newInstance(){
        if(shoppingCartFragment == null){
            shoppingCartFragment = new ShoppingCartFragment();
        }
        return shoppingCartFragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);
        return view;
    }
}
