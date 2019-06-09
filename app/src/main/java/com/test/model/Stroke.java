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

import com.test.algorithm.Geometry;

import java.util.List;

public class Stroke {

    private Context context;
    private Path path;
    private Path mediansPath;
    private List<GPoint2D> median;
    private List<GPoint2D> resampleMedian;
    private Paint paint;
    private Paint strokePaint;

    private Stroke nextStroke; // 下一个笔画
    private int writingSpeed = 20; // 写字的速度
    private float strokeWidth = 50.0f; // 笔画的宽度

    public Stroke(Context context) {
        this.context = context;
        paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        strokePaint = new Paint();
        strokePaint.setColor(Color.BLACK);
        strokePaint.setStyle(Paint.Style.FILL);
        strokePaint.setAntiAlias(true);

        mediansPath = new Path();
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return this.path;
    }

    public void setMedian(List<GPoint2D> median) {
        this.median = median;

        int sampleNumber = (int) Geometry.lengthOfPoints(median) / writingSpeed;
        resampleMedian = Geometry.resample(median, sampleNumber);
    }

    public List<GPoint2D> getMedian() {
        return this.median;
    }

    public void setNextStroke(Stroke nextStroke) {
        this.nextStroke = nextStroke;
    }

    // 在canvas上，根据path，用paint，fill这个path
    public void draw(Canvas canvas) {
        canvas.drawPath(path, paint);
        if (mediansPath != path) {
            canvas.save();
            canvas.clipPath(path);
            canvas.drawPath(mediansPath, strokePaint);
            canvas.restore();
        }
        else {
            canvas.drawPath(mediansPath, strokePaint);
        }
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
        colorFade.setDuration(300);
        colorFade.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.invalidate();
            }
        });
        colorFade.start();
    }

    // 不断地在笔画中心添加圆来实现
    public void animateStroke(final View view) {
        mediansPath.moveTo(resampleMedian.get(0).x, resampleMedian.get(0).y);
        for (int i = 0; i < resampleMedian.size(); i++) {
            GPoint2D center = resampleMedian.get(i);
            mediansPath.addCircle(center.x, center.y, strokeWidth, Path.Direction.CCW);
            view.invalidate();
            try {
                Thread.sleep(50);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        mediansPath = path;
        view.invalidate();
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
        for (GPoint2D point : resampleMedian) {
            point.scale(scale, scale);
        }
        strokeWidth = strokeWidth * scale;
    }

    // 设置颜色
    public void setColor(int color) {
        paint.setColor(color);
    }

    public void reset(final View view) {
        paint.setColor(Color.GRAY);
        mediansPath = new Path();
        view.invalidate();
    }
}
