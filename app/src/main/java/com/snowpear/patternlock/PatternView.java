package com.snowpear.patternlock;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Build;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.snowpear.common.Utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by wangjinpeng on 16/4/19.
 * 图案解锁的主界面
 */
final public class PatternView extends View implements Pattern.PatternInterface {

    public PatternView(Context context) {
        super(context);
        init();
    }

    public PatternView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PatternView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PatternView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public PatternView(Context context, Class<? extends Pattern> patternClazz) {
        super(context);
        this.patternClazz = patternClazz;
        init();
    }

    private boolean isInited = false;

    private int matrix = 3;

    /**
     * view宽
     */
    private int width;

    /**
     * view高
     */
    private int height;

    private static final int VERIFY_NONE = 0;
    private static final int VERIFY_SUCCESS = 1;
    private static final int VERIFY_FAILED = 2;
    private int result = VERIFY_NONE;

    /**
     * 选中的图案
     */
    private ArrayList<Pattern> selectPatterns = new ArrayList<>();

    /**
     * 所有图案
     */
    private ArrayList<Pattern> patterns = new ArrayList<>();

    /**
     * 当前touch的点
     */
    private PointF curPointF;

    /**
     * 没有选中的paint
     */
    private Paint normalPaint;

    /**
     * 选中的paint
     */
    private Paint selectPaint;

    /**
     * 错误的paint
     */
    private Paint errorPaint;

    private Class<? extends Pattern> patternClazz = CirclePattern.class;

    private void init() {
        if (isInited) {
            return;
        }
        isInited = true;

        int strokeEidth = Utils.dp2px(getContext(), 2);

        reset(matrix);

        normalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectPaint.setStrokeWidth(strokeEidth);
        errorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        errorPaint.setStrokeWidth(strokeEidth);

        normalPaint.setColor(Color.WHITE);
        selectPaint.setColor(Color.BLUE);
        errorPaint.setColor(Color.RED);
    }


    private void reset(int matrix) {
        selectPatterns.clear();
        result = VERIFY_NONE;
        if (this.matrix != matrix || matrix * matrix != patterns.size()) {
            patterns.clear();
            this.matrix = matrix;
            for (int x = 0; x < matrix; x++) {
                for (int y = 0; y < matrix; y++) {
                    Pattern pattern = createPattern(x, y);
                    patterns.add(pattern);
                }
            }
            calPatternPos();
        }
    }

    private Pattern createPattern(int x, int y) {
        if (patternClazz != null) {
            try {
                Pattern pattern = patternClazz.getConstructor().newInstance();
                pattern.onCreate(getContext(), x, y, this);
                return pattern;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void reset() {
        reset(matrix);
    }

    private void checkSelectPattern(final float x, final float y) {

        Pattern lastPattern = null;

        if (!selectPatterns.isEmpty()) {
            lastPattern = selectPatterns.get(selectPatterns.size() - 1);
        }

        Pattern checkPattern = null;

        for (Pattern pattern : patterns) {
            if (!isPatternSelect(pattern) && pattern.isNear(x, y)) {
                checkPattern = pattern;
                break;
            }
        }

        if (checkPattern != null) {
            if (lastPattern != null) {
                //说明是有链接线的

                //包含的范围
                PointF[] pointFs = new PointF[4];

                if(x == lastPattern.getPosX()){
                    //垂直线
                    pointFs[0] = new PointF(x - lastPattern.getMaxDistance(), y);
                    pointFs[1] = new PointF(x - lastPattern.getMaxDistance(), lastPattern.getPosY());
                    pointFs[2] = new PointF(x + lastPattern.getMaxDistance(), lastPattern.getPosY());
                    pointFs[3] = new PointF(x + lastPattern.getMaxDistance(), y);
                }else if(y == lastPattern.getPosY()){
                    //水平线
                    pointFs[0] = new PointF(x, y - lastPattern.getMaxDistance());
                    pointFs[1] = new PointF(lastPattern.getPosX(), y - lastPattern.getMaxDistance());
                    pointFs[2] = new PointF(lastPattern.getPosX(), y + lastPattern.getMaxDistance());
                    pointFs[3] = new PointF(x, y + lastPattern.getMaxDistance());
                }else{
                    float k = (y - lastPattern.getPosY()) / (x - lastPattern.getPosX());
                    float kSin = (float) Math.abs(Math.sin(Math.atan(k)));
                    float kCos = (float) Math.abs(Math.cos(Math.atan(k)));
                    float difX = lastPattern.getMaxDistance() * kSin;
                    float difY = lastPattern.getMaxDistance() * kCos * (Math.abs(k) / k) * -1;
//                    float difX = (float) Math.sqrt(Math.pow(lastPattern.getMaxDistance(), 2) + Math.pow(lastPattern.getMaxDistance()/k, 2));
                    pointFs[0] = new PointF(x - difX, y - difY);
                    pointFs[1] = new PointF(lastPattern.getPosX() - difX, lastPattern.getPosY() - difY);
                    pointFs[2] = new PointF(lastPattern.getPosX() + difX, lastPattern.getPosY() + difY);
                    pointFs[3] = new PointF(x + difX, y + difY);
                }

                ArrayList<Pattern> curSelectPatterns = new ArrayList<>();
                for (Pattern pattern : patterns) {
                    if (!isPatternSelect(pattern) && pattern != checkPattern) {
                        if(pattern.isInArea(pointFs)){
                            curSelectPatterns.add(pattern);
                            pattern.setSelected();
                        }
                    }
                }

                if (!curSelectPatterns.isEmpty()) {
                    final Pattern finalLastPattern = lastPattern;
                    Collections.sort(curSelectPatterns, new Comparator<Pattern>() {
                        @Override
                        public int compare(Pattern pattern1, Pattern pattern2) {
                            float deltX = x - finalLastPattern.getPosX();
                            float deltY = y - finalLastPattern.getPosY();
                            float deltPX = pattern1.getPosX() - pattern2.getPosX();
                            float deltPY = pattern1.getPosY() - pattern2.getPosY();
                            if (deltX > 0) {
                                return deltPX > 0 ? -1 : 1;
                            } else if (deltX < 0) {
                                return deltPX < 0 ? -1 : 1;
                            } else if (deltX == 0) {
                                return (deltY > 0 && deltPY > 0) ? -1 : 1;
                            }
                            return 0;
                        }
                    });
                }
                selectPatterns.addAll(curSelectPatterns);
            }
            checkPattern.setSelected();
            selectPatterns.add(checkPattern);
        }
    }

    private void performVibrator() {
        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 100};   // 停止 开启 停止 开启
        vibrator.vibrate(pattern, -1);
    }

    private boolean isPatternSelect(Pattern pattern) {
        return selectPatterns.contains(pattern);
    }


    private Runnable resetRunnable = new Runnable() {
        @Override
        public void run() {
            reset();
            postInvalidate();
        }
    };

    /**
     * 校验
     */
    private void verify() {
        result = VERIFY_FAILED;
        invalidate();
        Utils.debug("输出坐标顺序为:" + selectPatterns.toString());
        postDelayed(resetRunnable, 1000);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (patterns.size() > 0 && width > 0 && height > 0) {
            for (Pattern pattern : patterns) {
                pattern.onDrawPattern(canvas, isPatternSelect(pattern) ? (result == VERIFY_FAILED ? errorPaint : selectPaint) : normalPaint);
            }

            int selectSize = selectPatterns.size();
            for (int i = 0; i < selectSize; i++) {
                Pattern patternStart = selectPatterns.get(i);
                if (i < selectSize - 1) {
                    Pattern patternEnd = selectPatterns.get(i + 1);
                    canvas.drawLine(patternStart.getPosX(), patternStart.getPosY(), patternEnd.getPosX(), patternEnd.getPosY(), result == VERIFY_FAILED ? errorPaint : selectPaint);
                } else {
                    if (curPointF != null && !patternStart.isNear(curPointF.x, curPointF.y)) {
                        canvas.drawLine(patternStart.getPosX(), patternStart.getPosY(), curPointF.x, curPointF.y, result == VERIFY_FAILED ? errorPaint : selectPaint);
                    }
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        calPatternPos();
    }

    private void calPatternPos() {
        //创建 矩阵  pattern
        if (width > 0 && height > 0 && patterns.size() > 0) {

            int left = getPaddingLeft();
            int right = getPaddingRight();
            int top = getPaddingTop();
            int bottom = getPaddingBottom();

            int realWidth = width - left - right;
            int realHeight = height - top - bottom;

            float start = Math.abs(realWidth - realHeight) / 2;

            float widthSpace = (realWidth - ((realWidth > realHeight) ? start : 0) * 2) / matrix / 2;
            float heightSpace = (realHeight - ((realWidth > realHeight) ? 0 : start) * 2) / matrix / 2;

            for (Pattern pattern : patterns) {
                pattern.setPosX((realWidth > realHeight) ? start : 0 + (2 * pattern.getX() + 1) * widthSpace + left);
                pattern.setPosY((realWidth > realHeight) ? 0 : start + (2 * pattern.getY() + 1) * heightSpace + top);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (result == VERIFY_FAILED) {
            removeCallbacks(resetRunnable);
            reset();
        }

        if (result == VERIFY_NONE) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
                if (curPointF == null) {
                    curPointF = new PointF();
                }
                curPointF.set(event.getX(), event.getY());
                checkSelectPattern(curPointF.x, curPointF.y);
                invalidate();
            } else if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
                if (curPointF == null) {
                    curPointF = new PointF();
                }
                curPointF = null;
                checkSelectPattern(event.getX(), event.getY());
                invalidate();
                verify();
            }
            return true;
        }
        return false;
    }

    @Override
    public void onPatternUpdate() {
        postInvalidate();
    }
}
