package com.snowpear.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.snowpear.lock.LockManager;
import com.snowpear.lock.R;
import com.snowpear.lock.fingerprint.Finger_support;

public class MainActivity extends AppCompatActivity{

    private View area_lock_setting, area_fingerprint_lock;
    private Button btn_toggle_lock, btn_change_password;
    private TextView tv_lock_time;
    private ToggleButton toggle_exit, toggle_fingerprint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LockManager.getInstance().init(this);

        area_lock_setting = findViewById(R.id.lock_setting_area);
        area_fingerprint_lock = findViewById(R.id.fingerprint_lock_area);

        btn_toggle_lock = (Button) findViewById(R.id.toggle_password);
        btn_change_password = (Button) findViewById(R.id.change_password);

        tv_lock_time = (TextView) findViewById(R.id.tv_lock_time);

        toggle_exit = (ToggleButton) findViewById(R.id.toggle_exit);
        toggle_fingerprint = (ToggleButton) findViewById(R.id.toggle_fingerprint_lock);

        tv_lock_time.setText("自动");

        findViewById(R.id.auto_lock_area).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        findViewById(R.id.auto_exit_area).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle_exit.toggle();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(LockManager.getInstance().isNumberLockOpen() || LockManager.getInstance().isPatternLockOpen()){
            area_lock_setting.setVisibility(View.VISIBLE);
            btn_toggle_lock.setText("关闭密码");
            btn_toggle_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            btn_change_password.setEnabled(true);
            Finger_support support = LockManager.getInstance().isFingerPrintDeviceSupport(this);
            area_fingerprint_lock.setVisibility(Finger_support.support.equals(support) ? View.VISIBLE : View.GONE);
        }else{
            area_lock_setting.setVisibility(View.GONE);
            btn_toggle_lock.setText("启用密码");
            btn_toggle_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            btn_change_password.setEnabled(false);
        }
    }
}
