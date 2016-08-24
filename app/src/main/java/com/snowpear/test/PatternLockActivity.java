package com.snowpear.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.snowpear.lock.patternlock.CirclePattern;
import com.snowpear.lock.patternlock.PatternView;
import com.snowpear.lock.R;

public class PatternLockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_lock);
        PatternView patternView = new PatternView(this, CirclePattern.class);
        ((ViewGroup)findViewById(R.id.container)).addView(patternView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
}
