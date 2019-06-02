package com.test.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.test.model.Hanzi;
import com.test.model.Stroke;
import com.test.util.CharacterJsonReader;

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
        if (hanzi == null) {
            hanzi = new Hanzi(getContext());
        }
        hanzi.setCharacter(word);
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

    private List<Path> getStrokes() { return null; }

    private String getCharacterInfo() { return "a json object"; }
}
