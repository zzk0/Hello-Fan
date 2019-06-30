package com.test.model.entity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import com.test.algorithm.StrokesMatcher;
import com.test.util.CharacterJsonReader;
import com.test.util.StrokeParser;

import java.util.ArrayList;
import java.util.List;

public class Hanzi {

    private Context context;
    private List<Stroke> strokes;
    private StrokesMatcher strokesMatcher;
    
    // 绘图
    private Path outerBackground;
    private Path innerBackground;
    private Paint outerBackgroundPaint;
    private Paint innerBackgroundPaint;
    private Thread animateThread;

    // 状态
    private int currentStroke;
    private boolean haveScaledBackground;

    // 外部可访问状态
    public int color;
    public int outerBackgroundColor;
    public int innerBackgroundColor;
    public boolean animateStrokesLoop;
    public boolean animateStrokesOnce;
    public boolean haveOuterBackground;
    public boolean haveInnerBackground;
    public boolean testMode;

    // 控制多线程的状态
    private int wordId;
    private boolean finishSetCharacter;
    private boolean strokeAnimating;

    // 常量
    private static final float SVG_WIDTH = 1024.0f;

    public Hanzi(Context context) {
        this.context = context;
        strokes = new ArrayList<>();
        strokesMatcher = new StrokesMatcher();
        initStates();
        initBackground();
    }

    private void initStates() {
        color = Color.GRAY;
        outerBackgroundColor = 0x8A8A8AFF;
        innerBackgroundColor = 0x8A8A8AFF;
        wordId = 0;
        currentStroke = 0;
        animateStrokesLoop = false;
        animateStrokesOnce = false;
        haveOuterBackground = false;
        haveInnerBackground = false;
        haveScaledBackground = false;
        finishSetCharacter = true;
        strokeAnimating = false;
        testMode = false;
    }

    private void initBackground() {
        float length = SVG_WIDTH / 40.0f;

        // 设置背景内部Path
        innerBackground = new Path();
        innerBackground.moveTo(length / 2.0f, SVG_WIDTH / 2.0f);
        for (int i = 1; i < 40; i += 2) {
            innerBackground.lineTo( length / 2.0f + length * i, SVG_WIDTH / 2.0f);
            innerBackground.moveTo(length / 2.0f + length * (i + 1), SVG_WIDTH / 2.0f);
        }
        innerBackground.moveTo(SVG_WIDTH / 2.0f, 0.0f);
        for (int i = 1; i < 40; i += 2) {
            innerBackground.lineTo(SVG_WIDTH / 2.0f, length / 2.0f + length * i);
            innerBackground.moveTo(SVG_WIDTH / 2.0f, length / 2.0f + length * (i + 1));
        }

        // 设置背景内部画笔
        innerBackgroundPaint = new Paint();
        innerBackgroundPaint.setStrokeWidth(5.0f);
        innerBackgroundPaint.setStyle(Paint.Style.STROKE);

        // 设置背景外部Path
        outerBackground = new Path();
        outerBackground.moveTo(0.0f, 0.0f);
        outerBackground.lineTo(0.0f, SVG_WIDTH);
        outerBackground.lineTo(SVG_WIDTH, SVG_WIDTH);
        outerBackground.lineTo(SVG_WIDTH, 0.0f);
        outerBackground.lineTo(0.0f, 0.0f);

        // 设置背景外部画笔
        outerBackgroundPaint = new Paint();
        outerBackgroundPaint.setStrokeWidth(15.0f);
        outerBackgroundPaint.setStyle(Paint.Style.STROKE);
    }

    // 这段代码开启多线程，需要注意可能遇到的一些问题
    public void setCharacter(final View view, final String character, final int width) {
        // 如果没有完成设置汉字，则直接返回
        if (!finishSetCharacter) {
            return;
        }

        // 设置各种状态
        this.strokes = new ArrayList<>();
        this.currentStroke = 0;
        this.wordId = this.wordId + 1;
        this.finishSetCharacter = false;
        if (animateThread != null) {
            strokeAnimating = false;
            animateThread.interrupt();
        }
        strokesMatcher.setThreshold(width, testMode);

        // 设置绘图相关状态
        innerBackgroundPaint.setColor(innerBackgroundColor);
        outerBackgroundPaint.setColor(outerBackgroundColor);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 读取笔画信息
                String json = CharacterJsonReader.query(context, character);
                List<Path> paths = StrokeParser.parse(json);
                List<List<GPoint2D>> medians = StrokeParser.getMedians(json);

                // 对strokes内容进行初始化
                for (int i = 0; i < paths.size(); i++) {
                    Stroke stroke = new Stroke();
                    stroke.setPath(paths.get(i));
                    stroke.setMedian(medians.get(i));
                    stroke.setColor(color);
                    strokes.add(stroke);
                }

                // 设置视图
                setHanziSize(width, width);
                if (!haveScaledBackground) {
                    float scale = width / SVG_WIDTH;
                    Matrix matrix = new Matrix();
                    matrix.setScale(scale, scale);
                    outerBackground.transform(matrix);
                    innerBackground.transform(matrix);
                    haveScaledBackground = true;
                }
                animateStroke(view);

                // 更新状态
                finishSetCharacter = true;
                view.postInvalidate();
            }
        }).start();
    }

    // 根据HanziView的大小来设置Stroke的大小
    public void setHanziSize(int width, int height) {
        for (Stroke stroke : strokes) {
            stroke.setSize(width, height);
        }
    }

    public void clean() {
        strokes.clear();
        color = Color.GRAY;
        animateStrokesLoop = false;
        animateStrokesOnce = false;
        haveOuterBackground = false;
        haveInnerBackground = false;
        haveScaledBackground = false;
        finishSetCharacter = true;
        wordId = 0;

        initBackground();
    }

    // 调用每个笔画的draw方法
    public void draw(Canvas canvas) {
        if (haveOuterBackground) {
            canvas.drawPath(outerBackground, outerBackgroundPaint);
        }
        if (haveInnerBackground) {
            canvas.drawPath(innerBackground, innerBackgroundPaint);
        }
        for (int i = strokes.size() - 1; i >= 0; i--) {
            strokes.get(i).draw(canvas);
        }
    }

    // 此处应该是有内存泄漏的。当用户在学习界面的时候，点击返回。这时候线程仍在继续运行直到所有笔画写完。
    public void animateStroke(final View view) {
        if (!animateStrokesOnce && !animateStrokesLoop) {
            return;
        }

        animateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                reset(view);
                int currentId = wordId;
                strokeAnimating = true;
                for (int i = 0; i < strokes.size(); i++) {
                    if (wordId != currentId) {
                        break;
                    }
                    strokes.get(i).animateStroke(view);
                    if (animateStrokesLoop && i == (strokes.size() - 1)) {
                        i = -1;
                        reset(view);
                    }
                }
                strokeAnimating = false;
            }
        });

        if (!strokeAnimating) {
            animateThread.start();
        }
    }

    public boolean strokeMatch(List<GPoint2D> userStroke) {
        return strokesMatcher.match(userStroke, strokes.get(currentStroke).getMedian());
    }

    // 这个方法用来闪一闪这个文字，来提醒用户下一个要写的字在哪里
    public void prompt(View view) {
        if (currentStroke < strokes.size()) {
            strokes.get(currentStroke).prompt(view);
        }
    }

    // 这个方法用来完成这个笔画的渲染，当这个笔画写好了之后，从灰色渐变成黑色
    public void finishOneStroke(View view) {
        if (currentStroke < strokes.size()) {
            strokes.get(currentStroke).finish(view);
            currentStroke = currentStroke + 1;
        }
    }

    public void reset(View view) {
        if (!finishSetCharacter) {
            return;
        }
        for (int i = 0; i < strokes.size(); i++) {
            strokes.get(i).reset(view);
        }
        currentStroke = 0;
    }

    public boolean isFinish() {
        return currentStroke == strokes.size();
    }
}
