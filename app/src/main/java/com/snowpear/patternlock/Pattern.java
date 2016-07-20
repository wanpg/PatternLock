package com.snowpear.patternlock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.snowpear.common.Utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 图案类,指的是一个个图案
 */
public abstract class Pattern {

    private Context context;

    /**
     * 在创建此类后会第一时间执行此方法
     * @param context
     * @param x
     * @param y
     * @param patternInterface
     */
    public void onCreate(Context context, int x, int y, PatternInterface patternInterface){
        this.x = x;
        this.y = y;
        this.patternInterface = patternInterface;
        this.context = context;
        //计算动画执行的次数  最好是
        maxTime = getDuation() / 16;
    }

    public interface PatternInterface {
        void onPatternUpdate();
    }

    private PatternInterface patternInterface;

    //横向坐标
    private int x;
    //纵向坐标
    private int y;

    private float posX;
    private float posY;

    /**
     * 是否是在附近
     *
     * @param x
     * @param y
     * @return
     */
    final boolean isNear(float x, float y) {
        //以pattern坐标为中心， getMaxDistance 为半径的方位都属于贴近
        return Math.sqrt(Math.pow(x - posX, 2) + Math.pow(y - posY, 2)) < getMaxDistance();
    }

    final boolean isInArea(PointF ...pointFs){
        if(pointFs != null && pointFs.length >= 3){
            int verticesCount = pointFs.length;
            int nCross = 0;
            for (int i = 0; i < verticesCount; ++ i) {
                PointF p1 = pointFs[i];
                PointF p2 = pointFs[(i + 1) % verticesCount];

                // 求解 y=p.y 与 p1 p2 的交点
                if ( p1.y == p2.y) {   // p1p2 与 y=p0.y平行
                    continue;
                }
                if ( posY < Math.min(p1.y, p2.y) ) { // 交点在p1p2延长线上
                    continue;
                }
                if ( posY >= Math.max(p1.y, p2.y) ) { // 交点在p1p2延长线上
                    continue;
                }
                // 求交点的 X 坐标
                float tmpx = (posY - p1.y) * (p2.x - p1.x) / (p2.y - p1.y) + p1.x;
                if ( tmpx > posX ) { // 只统计单边交点
                    nCross++;
                }

            }
            // 单边交点为偶数，点在多边形之外
            return (nCross%2==1);
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof Pattern && x == ((Pattern) o).x && y == ((Pattern) o).y;
    }

    private Timer mAnimTimer;
    private int time = 0;
    private int maxTime = 0;
    private synchronized void stopAnim(){
        if(mAnimTimer != null) {
            mAnimTimer.cancel();
        }
        mAnimTimer = null;
        onAnimStop();
        time = 0;
    }

    final public synchronized void setSelected() {
        Utils.debug("选择了");
        //此处执行一次动画
        stopAnim();

        mAnimTimer = new Timer();
        mAnimTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (time > 12) {
                    stopAnim();
                } else {
                    onAnimProgress(time * 100f / maxTime /100f);
                    time++;
                }
                if (patternInterface != null) {
                    patternInterface.onPatternUpdate();
                }
            }
        }, 20, 20);
    }

    public abstract void onDrawPattern(Canvas canvas, Paint paint);

    protected abstract float getMaxDistance();

    protected abstract int getDuation();
    protected abstract void onAnimProgress(float progress);
    protected abstract void onAnimStop();

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    @Override
    public String toString() {
//        return super.toString();
        return "(" + x + "," + y + ")";
    }
}