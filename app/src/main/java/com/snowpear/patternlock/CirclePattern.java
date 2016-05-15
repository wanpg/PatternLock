package com.snowpear.patternlock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by wangjinpeng on 16/4/21.
 */
public class CirclePattern extends Pattern {

    /**
     * 默认的半径
     */
    private float defRadius;
    private float radius;

    @Override
    public void onCreate(Context context, int x, int y, PatternInterface patternInterface) {
        super.onCreate(context, x, y, patternInterface);
        super.onCreate(context, x, y, patternInterface);
        this.defRadius = Utils.dp2px(context, 6);
        radius = defRadius;
    }

    @Override
    public void onDrawPattern(Canvas canvas, Paint paint) {
        canvas.drawCircle(getPosX(), getPosY(), radius, paint);
    }

    @Override
    protected float getMaxDistance() {
        return radius * 5f;
    }

    @Override
    protected int getDuation() {
        return 240;
    }

    @Override
    protected void onAnimProgress(float progress) {
        if (progress < 0.5f) {
            //放大
            radius = defRadius + defRadius * progress / 0.5f;
        } else {
            //缩小
            radius = defRadius + defRadius * (1f - progress) / 0.5f;
        }
    }

    @Override
    protected void onAnimStop() {
        radius = defRadius;
    }
}
