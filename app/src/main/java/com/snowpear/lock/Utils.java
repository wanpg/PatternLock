package com.snowpear.lock;

import android.content.Context;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by wangjinpeng on 16/4/19.
 * 图案解锁的工具类
 */
public class Utils {

    private static final String TAG = "Lock";
    private static final boolean DEBUG = true;

    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static void debug(String string) {
        if (DEBUG) {
            Log.d(TAG, string);
        }
    }

    /**
     * 传入文本内容，返回 SHA-256 串
     *
     * @param text
     * @return
     */
    public static String SHA_256(final String text) {
        return SHA(text, "SHA-256");
    }

    /**
     * 传入文本内容，返回 SHA-512 串
     *
     * @param text
     * @return
     */
    public static String SHA_512(final String text) {
        return SHA(text, "SHA-512");
    }

    /**
     * 字符串 SHA512 加密
     *
     * @param text 需要加密的内容
     * @return
     */
    private static String SHA(final String text, final String type) {
        // 返回值
        String strResult = null;
        // 是否是有效字符串
        if (text != null && text.length() > 0) {
            try {
                // SHA 加密开始
                // 创建加密对象 并傳入加密類型
                MessageDigest messageDigest = MessageDigest.getInstance(type);
                // 传入要加密的字符串
                messageDigest.update(text.getBytes());
                // 得到 byte 類型结果
                byte[] byteBuffer = messageDigest.digest();
                // 將 byte 轉換爲 string
                StringBuilder strHexString = new StringBuilder();
                // 遍歷 byte buffer
                for (int i = 0; i < byteBuffer.length; i++) {
                    String hex = Integer.toHexString(0xff & byteBuffer[i]);
                    if (hex.length() == 1) {
                        strHexString.append('0');
                    }
                    strHexString.append(hex);
                }
                // 得到返回結果
                strResult = strHexString.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return strResult;
    }
}
