package com.test.view;

import android.content.Context;
import android.gesture.GestureOverlayView;
import android.gesture.GesturePoint;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.test.model.GPoint2D;
import com.test.model.Hanzi;

import java.util.ArrayList;
import java.util.List;

public class HanziView extends GestureOverlayView implements GestureOverlayView.OnGestureListener {

    private Hanzi hanzi;
    private int wrongTimes;
    private boolean haveAddListener;
    private boolean clickToAnimate;

    public HanziView(Context context) {
        super(context);
        init(context);
    }

    public HanziView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public HanziView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        hanzi = new Hanzi(context);
        wrongTimes = 0;
        haveAddListener = false;
        clickToAnimate = false;
    }

    // 做测试
    public void setQuiz() {
        if (!haveAddListener) {
            haveAddListener = true;
            addOnGestureListener(this);
        }
    }

    // 设置汉字的颜色
    public void setCharacterColor(int color) {
        hanzi.color = color;
    }

    // 设置田字格外框颜色
    public void setInnerBackgroundColor(int innerBackgroundColor) {
        hanzi.innerBackgroundColor = innerBackgroundColor;
    }

    // 设置田字格内部颜色
    public void setOuterBackgroundColor(int outerBackgroundColor) {
        hanzi.outerBackgroundColor = outerBackgroundColor;
    }

    // 设置动画一次
    public void setAnimate(boolean animateStrokes) {
        hanzi.animateStrokesOnce = animateStrokes;
        hanzi.animateStroke(this);
    }

    // 设置是否循环动画
    public void setLoopAnimate(boolean loopAnimateStrokes) {
        hanzi.animateStrokesLoop = loopAnimateStrokes;
        hanzi.animateStroke(this);
    }

    // 设置田字格外框是否存在
    public void setHaveInnerBackground(boolean haveInnerBackground) {
        hanzi.haveInnerBackground = haveInnerBackground;
    }

    // 设置田字格内部是否存在
    public void setHaveOuterBackground(boolean haveOuterBackground) {
        hanzi.haveOuterBackground = haveOuterBackground;
    }

    // 设置是否可以点击使之动画
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
        hanzi.setCharacter(this, word, mWidth);
    }

    public void cleanCharacter() {
        hanzi.clean();
        this.invalidate();
    }

    // 这个方法提供给手势事件调用, 当用户写错了笔画的时候，给出一些提示（prompt）
    public void prompt() {
        hanzi.prompt(this);
    }

    // 这个方法提供给手势事件调用, 当用户写对了笔画的时候，将这个字涂黑
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
