package com.ubimax.sample.base;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class SwitchButton extends View {
    private int widthSize;
    private int heightSize;
    private boolean isOn = true;
    private float WhiteRoundRect_width, WhiteRoundRect_height;
    private float Circle_X, Circle_Y, WhiteRoundRect_X, WhiteRoundRect_Y;
    private float Radius;
    private float currentValue;
    private int currentAlphaofGreen, currentAlphaofGray;
    private int duration = 200;
    private SwitchButtonChangeListener changeListener;

    public SwitchButton(Context context) {
        super(context);
    }

    public SwitchButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        initData();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthSize = MeasureSpec.getSize(widthMeasureSpec);
        heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
        initData();
    }

    void initData() {
        if (isOn) {
            currentValue = widthSize - 0.5f * heightSize;
            currentAlphaofGreen = 255;
            currentAlphaofGray = 0;
        } else {
            currentValue = 0.5f * heightSize;
            currentAlphaofGreen = 0;
            currentAlphaofGray = 255;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isOn) {
            DrawBackGreenRoundRect(canvas);
            DrawCircle(canvas);
        } else {
            DrawBackGrayRoundRect(canvas);
            DrawBackWhiteRoundRect(canvas);
            DrawCircle(canvas);
        }
    }

    private void DrawBackGrayRoundRect(Canvas canvas) {
        Paint paint0 = new Paint();
        paint0.setStyle(Paint.Style.FILL);
        paint0.setColor(Color.LTGRAY);
        paint0.setAntiAlias(true);
        paint0.setAlpha(currentAlphaofGray);
        RectF roundRect = new RectF(0, 0, widthSize, heightSize);
        canvas.drawRoundRect(roundRect, heightSize * 0.5f, heightSize * 0.5f, paint0);
    }

    private void DrawBackGreenRoundRect(Canvas canvas) {
        Paint paint1 = new Paint();
        paint1.setStyle(Paint.Style.FILL);
        paint1.setColor(0xff2E5BFF);
        paint1.setAntiAlias(true);
        paint1.setAlpha(currentAlphaofGreen);
        RectF roundRect = new RectF(0, 0, widthSize, heightSize);
        canvas.drawRoundRect(roundRect, heightSize * 0.5f, heightSize * 0.5f, paint1);
    }

    private void DrawCircle(Canvas canvas) {
        Circle_Y = heightSize * 0.5f;
        Radius = heightSize * 0.45f;
        Paint paint2 = new Paint();
        paint2.setStyle(Paint.Style.FILL);
        paint2.setColor(Color.WHITE);
        paint2.setAntiAlias(true);
        canvas.drawCircle(currentValue, Circle_Y, Radius, paint2);
    }

    private void DrawBackWhiteRoundRect(Canvas canvas) {
        Paint paint3 = new Paint();
        paint3.setStyle(Paint.Style.FILL);
        paint3.setColor(Color.LTGRAY);
        paint3.setAntiAlias(true);
        paint3.setAlpha(currentAlphaofGray);
        WhiteRoundRect_X = heightSize * 0.05f;
        WhiteRoundRect_Y = heightSize * 0.05f;
        WhiteRoundRect_width = widthSize - 0.05f * heightSize;
        WhiteRoundRect_height = heightSize * 0.95f;
        RectF rectf = new RectF(WhiteRoundRect_X, WhiteRoundRect_Y, WhiteRoundRect_width, WhiteRoundRect_height);
        canvas.drawRoundRect(rectf, WhiteRoundRect_height * 0.5f, WhiteRoundRect_height * 0.5f, paint3);
    }

    /**
     * 添加了过渡值动画，实现了平缓运动
     *
     * @param startValue
     * @param endValue
     */
    private void setAnimation(float startValue, float endValue) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(startValue, endValue);
        valueAnimator.setDuration(duration);
        valueAnimator.setTarget(currentValue);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }

    private void setAlphaAnimationofGray(int startValue, int endValue) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(startValue, endValue);
        valueAnimator.setDuration(duration);
        valueAnimator.setTarget(currentAlphaofGray);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentAlphaofGray = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }

    private void setAlphaAnimationofGreen(int startValue, int endValue) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(startValue, endValue);
        valueAnimator.setDuration(duration);
        valueAnimator.setTarget(currentAlphaofGreen);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentAlphaofGreen = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_MOVE:
                return false;
            case MotionEvent.ACTION_UP:
                isOn = !isOn;
                invalidate();
                break;
            default:
                break;
        }
        switchStateAnimation();
        if (changeListener != null) {
            changeListener.onChanger(isOn);
        }
        return super.onTouchEvent(event);
    }

    public void updateSwitchButtonState(boolean isOn) {
        this.isOn = isOn;
        switchStateAnimation();
    }

    private void switchStateAnimation() {
        if (isOn) {
            float startCircle_X = 0.5f * heightSize;
            float endCircle_X = widthSize - 0.5f * heightSize;
            setAnimation(startCircle_X, endCircle_X);
            setAlphaAnimationofGray(255, 0);
            setAlphaAnimationofGreen(0, 255);
        } else {
            float startCircle_X = widthSize - 0.5f * heightSize;
            float endCircle_X = heightSize * 0.5f;
            setAnimation(startCircle_X, endCircle_X);
            setAlphaAnimationofGray(0, 255);
            setAlphaAnimationofGreen(255, 0);
        }
    }

    public boolean getSwitchButtonState() {
        return isOn;
    }

    public void setSwitchButtonState(boolean isOn) {
        this.isOn = isOn;
    }

    public void setOnSwitchButtonChangeListener(SwitchButtonChangeListener listener) {
        changeListener = listener;
    }

    public interface SwitchButtonChangeListener {
        void onChanger(boolean isOn);
    }
}
