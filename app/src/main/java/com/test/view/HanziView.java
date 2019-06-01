package com.test.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.test.model.Hanzi;
import com.test.model.Stroke;

import java.util.List;

public class HanziView extends View {

    private Hanzi hanzi;

    public HanziView(Context context) {
        super(context);
    }

    public HanziView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public HanziView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    // 当写完一个字，想要进入下一个字的时候
    // 使用这个方法来更新字
    public void updateCharacter() {}

    // 这个方法提供给手势事件调用
    // 当用户写错了笔画的时候，给出一些提示（prompt）
    public void prompt() {}

    // 这个方法提供给手势事件调用
    // 当用户写对了笔画的时候，将这个字涂黑
    public void advance() { }

    private List<Path> getStrokes() { return null; }

    private String getCharacterInfo() { return "a json object"; }
}
