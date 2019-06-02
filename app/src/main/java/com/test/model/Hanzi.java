package com.test.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.view.View;

import com.test.util.CharacterJsonReader;
import com.test.util.StrokeParser;

import java.util.ArrayList;
import java.util.List;

public class Hanzi {

    private Context context;
    private String character;
    private int currentStroke;
    private List<Stroke> strokes;

    public Hanzi(Context context) {
        this.context = context;
    }

    public void setCharacter(String character) {
        this.character = character;
        this.currentStroke = 0;
        this.strokes = new ArrayList<>();
        String json = CharacterJsonReader.query(character);
        List<Path> paths = StrokeParser.parse(json);
        // 对strokes内容进行初始化
        for (Path path : paths) {
            Stroke stroke = new Stroke(context);
            stroke.setPath(path);
            strokes.add(stroke);
        }
    }

    // 调用每个笔画的draw方法
    public void draw(Canvas canvas) {
        for (Stroke stroke : strokes) {
            stroke.draw(canvas);
        }
    }

    // 这个方法用来闪一闪这个文字，来提醒用户下一个要写的字在哪里
    // 需要View的作用，做动画特效需要调用View的invalidate方法，这个模型里面没有view，只好传一个进来了。
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
        for (int i = 0; i < strokes.size(); i++) {
            strokes.get(i).reset(view);
        }
        currentStroke = 0;
    }

    // 根据HanziView的大小来设置Stroke的大小
    public void setHanziSize(int width, int height) {

    }

    public boolean isFinish() {
        return currentStroke == strokes.size();
    }
}
