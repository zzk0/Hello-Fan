/*
笔画模型：
每个笔画需要一个Path，这个Path即是要画在对应Canvas上的数据，经过了StrokeParser处理的。
每个笔画需要一个Paint，只有这样才可以控制每一个Path的颜色。
多个笔画构成一个Hanzi模型。
*/

package com.test.model;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class Stroke {

    private Context context;
    private Path path;
    private Paint paint;

    public Stroke(Context context) {
        this.context = context;
        paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);
    }

    public void setPath(Path path) {
        this.path = path;
    }

    // 在canvas上，根据path，用paint，fill这个path
    public void draw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }

    // 闪烁当前笔画来提醒用户
    public void prompt(final View view) {
        ObjectAnimator colorFade = ObjectAnimator.ofObject(paint, "color", new ArgbEvaluator(), Color.GRAY, Color.RED, Color.GRAY);
        colorFade.setDuration(1000);
        colorFade.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.invalidate();
            }

        });
        colorFade.start();
    }

    // 渐变为黑色
    public void finish(final View view) {
        ObjectAnimator colorFade = ObjectAnimator.ofObject(paint, "color", new ArgbEvaluator(), Color.GRAY, Color.BLACK);
        colorFade.setDuration(1000);
        colorFade.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.invalidate();
            }

        });
        colorFade.start();
    }

    // 设置大小
    public void setSize(int width, int height) {

    }

    public void reset(final View view) {
        paint.setColor(Color.GRAY);
        view.invalidate();
    }
}
