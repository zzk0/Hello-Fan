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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import java.util.List;

public class Stroke {

    private Context context;
    private Path path;
    private Path mediansPath;
    private Path testPath;
    private List<GPoint2D> median;
    private Paint paint;
    private Paint strokePaint;

    // 下一个笔画
    private Stroke nextStroke;

    public Stroke(Context context) {
        this.context = context;
        paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);

        strokePaint = new Paint();
        strokePaint.setColor(Color.BLACK);
        strokePaint.setStrokeCap(Paint.Cap.ROUND);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(45);

        testPath = new Path();
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return this.path;
    }

    public void setMediansPath(Path mediansPath) {
        this.mediansPath = mediansPath;
    }

    public void setMedian(List<GPoint2D> median) {
        this.median = median;
    }

    public void setNextStroke(Stroke nextStroke) {
        this.nextStroke = nextStroke;
    }

    // 在canvas上，根据path，用paint，fill这个path
    public void draw(Canvas canvas) {
        canvas.drawPath(path, paint);
        canvas.drawPath(testPath, strokePaint);
    }

    // 闪烁当前笔画来提醒用户
    public void prompt(final View view) {
        final ObjectAnimator colorFade = ObjectAnimator.ofObject(paint, "color", new ArgbEvaluator(), Color.GRAY, Color.RED, Color.GRAY);
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
        final ObjectAnimator colorFade = ObjectAnimator.ofObject(paint, "color", new ArgbEvaluator(), Color.GRAY, Color.BLACK);
        colorFade.setDuration(1000);
        colorFade.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.invalidate();
            }
        });
        colorFade.start();
    }

    // 这个方法中设置了Duration为1秒，这意味着，每个笔画的写完的时间开销都一样
    // 但实际上应该根据笔画的长度来决定。后期可以计算笔画长度，然后根据长度计算时间。
    public void animateStroke(final View view) {
        testPath.moveTo(median.get(0).x, median.get(0).y);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < median.size(); i++) {
                    testPath.lineTo(median.get(i).x, median.get(i).y);
                    view.invalidate();
                    try {
                        Thread.sleep(50);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (nextStroke != null) {
                    nextStroke.animateStroke(view);
                }
            }
        }).start();
//        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
//        final long time = (long) (medianLength() / 55.0f) * 1000;
//        valueAnimator.setDuration(time);
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float t = (float) animation.getAnimatedValue();
//                int lastIndex = median.size() - 1;
//                int index = (int) t * lastIndex;
//                if (index == (median.size() - 1)) {
//                    testPath.lineTo(median.get(lastIndex).x, median.get(lastIndex).y);
//                    if (nextStroke != null) {
//                        nextStroke.animateStroke(view);
//                    }
//                    return;
//                }
//                float interpolateValue = t - 1.0f * index / median.size();
//                GPoint2D interploatePoint = interpolate(median.get(index), median.get(index + 1), interpolateValue);
//                testPath.lineTo(interploatePoint.x, interploatePoint.y);
//                view.invalidate();
//            }
//        });
//        valueAnimator.start();
    }

    // 设置大小, 不使用高度。
    public void setSize(int width, int height) {
        float scale = width / 1024.0f;
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        path.transform(matrix);
        for (GPoint2D point : median) {
            point.scale(scale, scale);
        }
    }

    public void reset(final View view) {
        paint.setColor(Color.GRAY);
        testPath = new Path();
        view.invalidate();
    }

    // 返回Median的总长度
    public float medianLength() {
        float length = 0.0f;
        for (int i = 0; i < median.size() - 1; i++) {
            length += median.get(i).distanceTo(median.get(i + 1));
        }
        return length;
    }

    private GPoint2D interpolate(GPoint2D a, GPoint2D b, float t) {
        float xx = a.x + t * (b.x - a.x);
        float yy = a.y + t * (b.y - a.y);
        return new GPoint2D(xx, yy);
    }
}
