package com.example.yischool;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import searcheditview.EditTextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditTextView editTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        editTextView = findViewById(R.id.search_edit_text);
        editTextView.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_edit_text:
                Intent startSearchActivity = new Intent(this, SearchActivity.class);
                startActivity(startSearchActivity);
                break;
            default:break;
        }
    }
}
