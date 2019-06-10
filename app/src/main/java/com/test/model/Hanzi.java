package com.test.model;

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
    private String character;

    // 绘图
    private List<Stroke> strokes;
    private StrokesMatcher strokesMatcher;
    private Path background;
    private Paint backgroundPaint;
    private Thread animateThread;

    // 状态
    private int wordId;
    private int color;
    private int currentStroke;
    private boolean animateStrokesLoop;
    private boolean animateStrokesOnce;
    private boolean haveBackground;
    private boolean haveScaledBackground;
    private boolean finishSetCharacter;

    // 常量
    private static final float SVG_WIDTH = 1024.0f;

    public Hanzi(Context context) {
        this.context = context;
        strokesMatcher = new StrokesMatcher();
        color = Color.GRAY;
        animateStrokesLoop = false;
        animateStrokesOnce = false;
        haveBackground = false;
        haveScaledBackground = false;
        finishSetCharacter = true;
        wordId = 0;

        initBackground();
    }

    private void initBackground() {
        background = new Path();
        background.moveTo(0.0f, 0.0f);
        background.lineTo(SVG_WIDTH, 0.0f);
        background.lineTo(0.0f, SVG_WIDTH);
        background.lineTo(SVG_WIDTH, SVG_WIDTH);
        background.lineTo(0.0f, 0.0f);
        background.lineTo(0.0f, SVG_WIDTH);
        background.moveTo(SVG_WIDTH, 0.0f);
        background.lineTo(SVG_WIDTH, SVG_WIDTH);
        background.moveTo(0.0f, SVG_WIDTH / 2.0f);
        background.lineTo(SVG_WIDTH, SVG_WIDTH / 2.0f);
        background.moveTo(SVG_WIDTH / 2.0f, 0.0f);
        background.lineTo(SVG_WIDTH / 2.0f, SVG_WIDTH);

        backgroundPaint = new Paint();
        backgroundPaint.setStrokeWidth(5.0f);
        backgroundPaint.setColor(0x8A8A8AFF);
        backgroundPaint.setStyle(Paint.Style.STROKE);
    }

    // 这段代码开启多线程，需要注意可能遇到的一些问题
    public void setCharacter(final View view, final String character, final int width) {
        if (!finishSetCharacter) {
            return;
        }
        this.character = character;
        this.currentStroke = 0;
        this.strokes = new ArrayList<>();
        this.wordId = this.wordId + 1;
        this.finishSetCharacter = false;
        if (animateThread != null) {
            this.animateThread.interrupt();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                setHanziSize(width, width);
                if (!haveScaledBackground && haveBackground) {
                    float scale = width / SVG_WIDTH;
                    Matrix matrix = new Matrix();
                    matrix.setScale(scale, scale);
                    background.transform(matrix);
                    haveScaledBackground = true;
                }
                if (animateStrokesLoop) {
                    loopAnimateStroke(view);
                }
                else if (animateStrokesOnce) {
                    animateStroke(view);
                }
                finishSetCharacter = true;
                view.invalidate();
            }
        }).start();
    }

    // 请在设置汉字之前设置调用
    public void setColor(int color) {
        this.color = color;
    }

    public void setAnimateStrokesLoop(boolean animateStrokesLoop) {
        this.animateStrokesLoop = animateStrokesLoop;
    }

    public void setAnimateStrokesOnce(boolean animateStrokesOnce) {
        this.animateStrokesOnce = animateStrokesOnce;
    }

    public void setHaveBackground(boolean haveBackground) {
        this.haveBackground = haveBackground;
    }

    // 根据HanziView的大小来设置Stroke的大小
    public void setHanziSize(int width, int height) {
        for (Stroke stroke : strokes) {
            stroke.setSize(width, height);
        }
    }

    // 调用每个笔画的draw方法
    public void draw(Canvas canvas) {
        if (haveBackground) {
            canvas.drawPath(background, backgroundPaint);
        }
        for (int i = strokes.size() - 1; i >= 0; i--) {
            strokes.get(i).draw(canvas);
        }
    }

    public void animateStroke(final View view) {
        animateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < strokes.size(); i++) {
                    strokes.get(i).animateStroke(view);
                }
            }
        });
        animateThread.start();
    }

    public void loopAnimateStroke(final View view) {
        animateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int currentId = wordId;
                for (int i = 0; i < strokes.size(); i++) {
                    if (wordId != currentId) break;
                    strokes.get(i).animateStroke(view);
                    if (i == (strokes.size() - 1)) {
                        i = -1;
                        reset(view);
                    }
                }
            }
        });
        animateThread.start();
    }

    public void doWriting(float x, float y) {
        if (currentStroke >= strokes.size()) {
            return;
        }
        if (!strokes.get(currentStroke).finishWritingThisStroke()) {
            strokes.get(currentStroke).doWriting(x, y);
        }
    }

    public void doWritingUpdateCurrentStroke() {
        if (strokes.get(currentStroke).finishWritingThisStroke()) {
            currentStroke = currentStroke + 1;
        }
    }

    // 这个方法用来闪一闪这个文字，来提醒用户下一个要写的字在哪里
    // 需要View的作用，做动画特效需要调用View的invalidate方法，这个模型里面没有view，只好传一个进来了。
    public void prompt(View view) {
        if (currentStroke < strokes.size()) {
            strokes.get(currentStroke).prompt(view);
        }
    }

    public boolean strokeMatch(List<GPoint2D> userStroke) {
        return strokesMatcher.match(userStroke, strokes.get(currentStroke).getMedian());
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
