package com.snowpear.test;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.security.KeyStore;

import javax.crypto.KeyGenerator;

/**
 * Created by wangjinpeng on 16/8/19.
 */
public class FingerPrintLockActivity extends AppCompatActivity {

    private enum Finger_support {
        support, not_support, wait_permission
    }

    private static final int FINGER_PERMISSION_REQUEST_CODE = 1000;

    private static final String FINGER_KEY_NAME = "snowpear";

    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Finger_support finger_support = isDeviceSupport();
        if(Finger_support.not_support.equals(finger_support)){
            toast("该手机不支持指纹解锁");
            onBackPressed();
        }else if(Finger_support.support.equals(finger_support)){
            // 进行下一步
            doFingerPrintLock();
        }else{
            // wait for permission
            toast("正在请求指纹识别权限");
        }
    }

    private void doFingerPrintLock(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FingerprintManager manager = getSystemService(FingerprintManager.class);
            if(checkAndRequestPermission()) {
                manager.authenticate(null, null, 0, new FingerprintManager.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        toast("onAuthenticationError");
                    }

                    @Override
                    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        toast("onAuthenticationSucceeded");
                        onBackPressed();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        toast("onAuthenticationFailed");
                    }
                }, null);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == FINGER_PERMISSION_REQUEST_CODE){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED){
                // 指纹权限授权成功
                // 进行下一步
                doFingerPrintLock();
            }else{
                // 指纹权限授权失败
                toast("请求指纹权限被拒绝");
                onBackPressed();
            }
        }
    }


    /**
     * 判断系统是否支持指纹识别
     * @return
     */

    public Finger_support isDeviceSupport() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                FingerprintManager manager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
                if (manager != null) {
                    if (checkAndRequestPermission()) {
                        return manager.isHardwareDetected() ? Finger_support.support : Finger_support.not_support;
                    } else {
                        return Finger_support.wait_permission;
                    }
                }
            } catch (Exception e) {

            }
        }
        return Finger_support.not_support;
    }

    /**
     * 检测权限
     * @return
     */
    @TargetApi(Build.VERSION_CODES.M)
    public boolean checkAndRequestPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.USE_FINGERPRINT}, FINGER_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
