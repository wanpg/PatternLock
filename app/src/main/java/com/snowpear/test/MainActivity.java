package com.snowpear.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.snowpear.lock.R;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_1:
                startActivity(new Intent(MainActivity.this, PatternLockActivity.class));
                break;
            case R.id.btn_2:
                startActivity(new Intent(MainActivity.this, FingerLockActivity.class));
                break;
            case R.id.btn_3:
                startActivity(new Intent(MainActivity.this, com.example.android.fingerprintdialog.MainActivity.class));
                break;
        }
    }
}
