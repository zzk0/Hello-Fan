package com.test.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class HeightAdaptImageView extends ImageView {

    public HeightAdaptImageView(Context context) {
        super(context);
    }

    public HeightAdaptImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public HeightAdaptImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
