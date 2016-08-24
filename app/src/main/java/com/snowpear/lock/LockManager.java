package com.snowpear.lock;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.snowpear.lock.fingerprint.Finger_support;

/**
 * Created by wangjinpeng on 16/4/19.
 * 图案解锁的控制器
 */
final public class LockManager {

    private static final String TAG = LockManager.class.getSimpleName();

    private final static LockManager instance = new LockManager();

    private static final String PATTERN_KEY_NAME = "pattern_key";
    private static final String NUMBER_KEY_NAME = "number_key";
    private static final String LOCK_PRE_NAME = "lock_pre";

    private Context context;

    public static LockManager getInstance() {
        return instance;
    }

    private LockManager() {
    }

    public void init(Context context) {
        this.context = context.getApplicationContext();
    }

    public void clear(Context context){
        SharedPreferences preferences = context.getSharedPreferences(LOCK_PRE_NAME, 0);
        SharedPreferences.Editor editer = preferences.edit();
        editer.clear();
        boolean commit = editer.commit();
    }

    public String sign(String password) {
        if(context == null){
            throw new IllegalStateException("must excute init before sign");
        }

        if(password == null || "".equals(password)){
            throw new IllegalArgumentException("password can not be null or \"\" when sign");
        }
        return Utils.SHA_512(password);
    }

    public boolean savePatternPassword(String password){
        String sign = sign(password);
        return setSignedPassword(sign, PATTERN_KEY_NAME);
    }

    public boolean saveNumberSignPassword(String password){
        String sign = sign(password);
        return setSignedPassword(sign, NUMBER_KEY_NAME);
    }

    private boolean setSignedPassword(String sign, String name){
        SharedPreferences preferences = context.getSharedPreferences(LOCK_PRE_NAME, 0);
        SharedPreferences.Editor editer = preferences.edit();
        editer.putString(name, sign);
        return editer.commit();
    }

    private String getSignedPassword(String name){
        SharedPreferences preferences = context.getSharedPreferences(LOCK_PRE_NAME, 0);
        return preferences.getString(name, "");
    }

    public boolean verify(String password, String signedPassword){
        if(context == null){
            throw new IllegalStateException("must excute init before verify");
        }
        if(password == null || "".equals(password)){
            throw new IllegalArgumentException("password can not be null or \"\" when verify");
        }
        return Utils.SHA_512(password).equals(signedPassword);
    }

    public boolean verifyNumberPassword(String password){
        return verify(password, getSignedPassword(NUMBER_KEY_NAME));
    }

    public boolean verifyPatternPassword(String password){
        return verify(password, getSignedPassword(PATTERN_KEY_NAME));
    }

    public boolean isNumberLockOpen(){
        return !TextUtils.isEmpty(getSignedPassword(NUMBER_KEY_NAME));
    }

    public boolean isPatternLockOpen(){
        return !TextUtils.isEmpty(getSignedPassword(PATTERN_KEY_NAME));
    }

    public boolean isFingerPrintLockOpen(){
        return !TextUtils.isEmpty(getSignedPassword(PATTERN_KEY_NAME));
    }



    private static final int FINGER_PERMISSION_REQUEST_CODE = 1000;

    /**
     * 判断系统是否支持指纹识别
     * @return
     */
    public Finger_support isFingerPrintDeviceSupport(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                FingerprintManager manager = (FingerprintManager) activity.getSystemService(Context.FINGERPRINT_SERVICE);
                if (manager != null) {
                    if (checkAndRequestPermission(activity)) {
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
    public boolean checkAndRequestPermission(Activity activity){
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.USE_FINGERPRINT}, FINGER_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }
}
