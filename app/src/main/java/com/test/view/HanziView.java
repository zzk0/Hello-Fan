package com.test.view;

import android.content.Context;
import android.gesture.GestureOverlayView;
import android.gesture.GesturePoint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.test.model.GPoint2D;
import com.test.model.Hanzi;

import java.util.ArrayList;
import java.util.List;

public class HanziView extends GestureOverlayView implements GestureOverlayView.OnGestureListener {

    private Hanzi hanzi;
    private int wrongTimes;
    private int color;
    private boolean loopAnimateStrokes;
    private boolean animateStrokes;
    private boolean haveOutterBackground;
    private boolean haveInnerBackground;
    private boolean haveAddListener;
    private boolean quizAnimate;
    private boolean clickToAnimate;
    private int outterBackgroundColor;
    private int innerBackgroundColor;

    public HanziView(Context context) {
        super(context);
        init();
    }

    public HanziView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public HanziView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        wrongTimes = 0;
        color = Color.GRAY;
        loopAnimateStrokes = false;
        animateStrokes = false;
        haveOutterBackground = false;
        haveInnerBackground = false;
        setUncertainGestureColor(Color.TRANSPARENT);
        setGestureColor(Color.TRANSPARENT);
        outterBackgroundColor = 0x8A8A8AFF;
        innerBackgroundColor = 0x8A8A8AFF;
    }

    // 做测试
    public void setQuiz() {
        if (!haveAddListener) {
            haveAddListener = true;
            addOnGestureListener(this);
        }
    }

    // 允许写下笔顺
    public void setQuizAnimate() {
        if (!haveAddListener) {
            haveAddListener = true;
            addOnGestureListener(this);
        }
        quizAnimate = true;
    }

    // 设置汉字的颜色
    public void setCharacterColor(int color) {
        this.color = color;
    }

    public void setLoopAnimate(boolean loopAnimateStrokes) {
        this.loopAnimateStrokes = loopAnimateStrokes;
    }

    public void setAnimate(boolean animateStrokes) {
        this.animateStrokes = animateStrokes;
    }

    public void setHaveInnerBackground(boolean haveInnerBackground) {
        this.haveInnerBackground = haveInnerBackground;
    }

    public void setHaveOutterBackground(boolean haveOutterBackground) {
        this.haveOutterBackground = haveOutterBackground;
    }

    public void setInnerBackgroundColor(int innerBackgroundColor) {
        this.innerBackgroundColor = innerBackgroundColor;
    }

    public void setOutterBackgroundColor(int outterBackgroundColor) {
        this.outterBackgroundColor = outterBackgroundColor;
    }

    public void setClickToAnimate(boolean clickToAnimate) {
        this.clickToAnimate = clickToAnimate;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (hanzi != null) {
            hanzi.draw(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    // 使用这个方法来更新汉字
    public void setCharacter(String word) {
        int mWidth = this.getMeasuredWidth();
        if (hanzi == null) {
            hanzi = new Hanzi(getContext());
        }
        hanzi.setColor(color);
        hanzi.setHaveOutterBackground(haveOutterBackground);
        hanzi.setHaveInnerBackground(haveInnerBackground);
        hanzi.setInnerBackgroundColor(innerBackgroundColor);
        hanzi.setOutterBackgroundColor(outterBackgroundColor);
        hanzi.setAnimateStrokesLoop(loopAnimateStrokes);
        hanzi.setAnimateStrokesOnce(animateStrokes);
        hanzi.setCharacter(this, word, mWidth);
    }

    public void cleanCharacter() {
        hanzi.clean();
        this.invalidate();
    }

    // 这个方法提供给手势事件调用
    // 当用户写错了笔画的时候，给出一些提示（prompt）
    public void prompt() {
        hanzi.prompt(this);
    }

    // 这个方法提供给手势事件调用
    // 当用户写对了笔画的时候，将这个字涂黑
    public void advance() {
        hanzi.finishOneStroke(this);
    }

    public void resetHanzi() {
        hanzi.reset(this);
    }

    public boolean hanziFinish() {
        return hanzi.isFinish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (clickToAnimate && event.getAction() == MotionEvent.ACTION_UP) {
            hanzi.animateStroke(this);
        }
//        if (!quizAnimate) return super.onTouchEvent(event);
//        if (event.getAction() == MotionEvent.ACTION_UP) {
//            hanzi.doWritingUpdateCurrentStroke();
//        }
//        else {
//            float x = event.getX();
//            float y = event.getY();
//            hanzi.doWriting(x, y);
//        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onGesture(GestureOverlayView overlay, MotionEvent event) {
    }

    @Override
    public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
    }

    @Override
    public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
        if (hanzi.isFinish()) {
            return;
        }
        List<GPoint2D> userStroke = new ArrayList<>();
        for (GesturePoint point : overlay.getCurrentStroke()) {
            GPoint2D newPoint = new GPoint2D(point.x, point.y);
            userStroke.add(newPoint);
        }
        if (hanzi.strokeMatch(userStroke)) {
            hanzi.finishOneStroke(this);
            wrongTimes = 0;
        }
        else {
            wrongTimes = wrongTimes + 1;
            if (wrongTimes > 2) {
                hanzi.prompt(this);
            }
        }
    }

    @Override
    public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
    }
}
