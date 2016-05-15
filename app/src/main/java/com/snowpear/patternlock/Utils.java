package com.snowpear.patternlock;

import android.content.Context;

/**
 * Created by wangjinpeng on 16/4/19.
 * 图案解锁的工具类
 */
public class Utils {

    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
