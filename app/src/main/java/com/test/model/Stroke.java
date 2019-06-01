/*
笔画模型：
每个笔画需要一个Path，这个Path即是要画在对应Canvas上的数据，经过了StrokeParser处理的。
每个笔画需要一个Paint，只有这样才可以控制每一个Path的颜色。

笔画模型的产生：
makemeahanzi的数据，经过StrokeParser处理之后，给到了HanziView之后，
在HanziView中将数据组装起来。（组装的地方在View中完成吗？是否太过于耗时？）
*/

package com.test.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import com.test.fan.R;

public class Stroke {

    private Context context;
    private Path path;
    private Paint paint;

    public Stroke(Context context) {
        this.context = context;
        paint = new Paint();
    }

    public void setPath(Path path) {
        this.path = path;
    }

    // 在canvas上，根据path，用paint，fill这个path
    public void draw(Canvas canvas) {
    }

    // 闪烁当前笔画来提醒用户
    public void prompt(View view) {
    }

    // 渐变为黑色
    public void finish(View view) {
    }
}
