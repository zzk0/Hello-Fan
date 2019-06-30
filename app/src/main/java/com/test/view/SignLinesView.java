package com.test.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.test.fan.R;

/**
 * Author： huanglele on 2017/9/11.
 */

public class SignLinesView extends View {
    private static final String TAG = "SignLinesView";
    private int centerX,centerY;
    private int radius = 33;
    private Paint circlePaint;
    private Paint numPaint;
    private Paint linesPaint;
    private Paint bitmapPaint;
    private Paint textPaint;

    private int linesWidth;
    private int signDays = 0;

    public SignLinesView(Context context) {
        super(context);
    }

    public SignLinesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        linesPaint = new Paint();
        linesPaint.setAntiAlias(true);
        linesPaint.setColor(getResources().getColor(R.color.lineColor));
        linesPaint.setStyle(Paint.Style.FILL);
        linesPaint.setStrokeWidth(4);

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(getResources().getColor(R.color.lineColor));
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(4);

        numPaint = new Paint();
        numPaint.setAntiAlias(true);
        numPaint.setTextSize(36);
        numPaint.setColor(getResources().getColor(R.color.lineColor));
        numPaint.setStyle(Paint.Style.FILL);
        numPaint.setTextAlign(Paint.Align.CENTER);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(getResources().getColor(R.color.textColor));
        textPaint.setTextSize(32);
        textPaint.setTextAlign(Paint.Align.CENTER);

        bitmapPaint = new Paint();
        bitmapPaint.setColor(getResources().getColor(R.color.white));
        bitmapPaint.setAntiAlias(true);
        bitmapPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        centerX = w/2;
        centerY = h/2;
        linesWidth =( (w-30)-radius*2*5)/5;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        drawLines(canvas,centerX-3*radius-linesWidth,centerY-2*radius-linesWidth,centerX-3*radius-2*linesWidth,centerY-2*radius-linesWidth);
        drawLines(canvas,centerX-radius,centerY-2*radius-linesWidth,centerX-radius-linesWidth,centerY-2*radius-linesWidth);
        drawLines(canvas,centerX+radius+linesWidth,centerY-2*radius-linesWidth,centerX+radius,centerY-2*radius-linesWidth);
        drawLines(canvas,centerX+3*radius+2*linesWidth,centerY-radius*2-linesWidth,centerX+3*radius+linesWidth,centerY-radius*2-linesWidth);
        drawLines(canvas,centerX+4*radius+2*linesWidth,centerY-radius,centerX+4*radius+2*linesWidth,centerY-radius-linesWidth);
        drawLines(canvas,centerX+3*radius+linesWidth,centerY,centerX+3*radius+linesWidth*2,centerY);
        drawLines(canvas,centerX+radius,centerY,centerX+radius+linesWidth,centerY);
        drawLines(canvas,centerX-radius,centerY,centerX-radius-linesWidth,centerY);
        drawLines(canvas,centerX-3*radius-linesWidth,centerY,centerX-3*radius-2*linesWidth,centerY);
        drawLines(canvas,centerX-4*radius-2*linesWidth,centerY+radius,centerX-4*radius-2*linesWidth,centerY+radius+linesWidth);
        drawLines(canvas,centerX-radius*3-2*linesWidth,centerY+2*radius+linesWidth,centerX-3*radius-linesWidth,centerY+2*radius+linesWidth);
        drawLines(canvas,centerX-linesWidth-radius,centerY+2*radius+linesWidth,centerX-radius,centerY+2*radius+linesWidth);
        drawLines(canvas,centerX+radius,centerY+2*radius+linesWidth,centerX+radius+linesWidth,centerY+2*radius+linesWidth);
        drawLines(canvas,centerX+3*radius+linesWidth,centerY+2*radius+linesWidth,centerX+linesWidth*2+3*radius,centerY+2*radius+linesWidth);

        int i = 0;
        drawText(canvas,""+ "第" + i + "天",centerX-4*radius-2*linesWidth,centerY-linesWidth+10);
        i++;
        drawText(canvas,""+ "第" + i + "天",centerX-2*radius-linesWidth,centerY-linesWidth+10);
        i++;
        drawText(canvas,""+ "第" + i + "天",centerX,centerY-linesWidth+10);
        i++;
        drawText(canvas,""+ "第" + i + "天",centerX+2*radius+linesWidth,centerY-linesWidth+10);
        i++;
        drawText(canvas,""+ "第" + i + "天",centerX+2*radius+2*linesWidth,centerY-linesWidth+10);
        i++;
        drawText(canvas,""+ "第" + i + "天",centerX+4*radius+2*linesWidth,centerY+2*radius+10);
        i++;
        drawText(canvas,""+ "第" + i + "天",centerX+2*radius+linesWidth,centerY+2*radius+10);
        i++;
        drawText(canvas,""+ "第" + i + "天",centerX,centerY+2*radius+10);
        i++;
        drawText(canvas,""+"第" + i + "天",centerX-radius*2-linesWidth,centerY+2*radius+10);
        i++;
        drawText(canvas,""+"第" + i + "天",centerX-2*linesWidth-radius-30,centerY+2*radius+10);
        i++;
        drawText(canvas,""+ "第" + i + "天",centerX-4*radius-2*linesWidth,centerY+4*radius+linesWidth+10);
        i++;
        drawText(canvas,""+ "第" + i + "天",centerX-2*radius-linesWidth,centerY+4*radius+linesWidth+10);
        i++;
        drawText(canvas,""+ "第" + i + "天",centerX,centerY+4*radius+linesWidth+10);
        i++;
        drawText(canvas,""+ "第" + i + "天",centerX+2*radius+linesWidth,centerY+4*radius+linesWidth+10);
        i++;
        drawText(canvas,""+ "第" + i + "天",centerX+4*radius+2*linesWidth,centerY+4*radius+linesWidth+10);


        Bitmap bm = BitmapFactory.decodeResource(getContext().getResources(),R.mipmap.pros);

        drawSwitch(canvas,bm);

        if (signDays>15){
            if(signDays%15==0)
            {
                signDays=15;
            }
            else {
                signDays=signDays%15;
                drawSwitch(canvas,bm);
            }
            /*canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
            canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
            canvas.drawBitmap(bm,centerX-radius,centerY-3*radius-linesWidth,bitmapPaint);
            canvas.drawBitmap(bm,centerX+radius+linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
            canvas.drawBitmap(bm,centerX+radius*3+2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
            canvas.drawBitmap(bm,centerX+radius*3+2*linesWidth,centerY-radius,bitmapPaint);
            canvas.drawBitmap(bm,centerX+radius+linesWidth,centerY-radius,bitmapPaint);
            canvas.drawBitmap(bm,centerX-radius,centerY-radius,bitmapPaint);
            canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY-radius,bitmapPaint);
            canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY-radius,bitmapPaint);
            canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY+radius+linesWidth,bitmapPaint);
            canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY+radius+linesWidth,bitmapPaint);
            canvas.drawBitmap(bm,centerX-radius,centerY+radius+linesWidth,bitmapPaint);
            canvas.drawBitmap(bm,centerX+radius+linesWidth,centerY+radius+linesWidth,bitmapPaint);
            canvas.drawBitmap(bm,centerX+3*radius+2*linesWidth,centerY+radius+linesWidth,bitmapPaint);*/
        }
        bm.recycle();

    }

    private void drawText(Canvas canvas,String days,int x,int y){
        canvas.drawText(days,x,y,textPaint);
    }

    private void drawLines(Canvas canvas,float startX, float startY, float stopX, float stopY){
        canvas.drawLine(startX,startY,stopX,stopY,linesPaint);
    }

    private void drawCircle(Canvas canvas,int x,int y,String num){
        canvas.drawCircle(x,y,radius,circlePaint);
        canvas.drawText(num,x,y+13,numPaint);
    }

    public void setSignDays(int days){
        this.signDays = days;
        invalidate();
    }

    private void drawSwitch(Canvas canvas,Bitmap bm)
    {
        switch (signDays){
            case 0:
                drawCircle(canvas,centerX-4*radius-2*linesWidth,centerY-2*radius-linesWidth,"+1");
                drawCircle(canvas,centerX-2*radius-linesWidth,centerY-2*radius-linesWidth,"+1");
                drawCircle(canvas,centerX,centerY-2*radius-linesWidth,"+1");
                drawCircle(canvas,centerX+2*radius+linesWidth,centerY-2*radius-linesWidth,"+1");
                drawCircle(canvas,centerX+4*radius+2*linesWidth,centerY-radius*2-linesWidth,"+1");
                drawCircle(canvas,centerX+4*radius+2*linesWidth,centerY,"+1");
                drawCircle(canvas,centerX+2*radius+linesWidth,centerY,"+1");
                drawCircle(canvas,centerX,centerY,"+1");
                drawCircle(canvas,centerX-radius*2-linesWidth,centerY,"+1");
                drawCircle(canvas,centerX-4*radius-2*linesWidth,centerY,"+1");
                drawCircle(canvas,centerX-4*radius-2*linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX-2*radius-linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX+2*radius+linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX+4*radius+2*linesWidth,centerY+2*radius+linesWidth,"+1");
                break;
            case 1:

                drawCircle(canvas,centerX-2*radius-linesWidth,centerY-2*radius-linesWidth,"+1");
                drawCircle(canvas,centerX,centerY-2*radius-linesWidth,"+1");
                drawCircle(canvas,centerX+2*radius+linesWidth,centerY-2*radius-linesWidth,"+1");
                drawCircle(canvas,centerX+4*radius+2*linesWidth,centerY-radius*2-linesWidth,"+1");
                drawCircle(canvas,centerX+4*radius+2*linesWidth,centerY,"+1");
                drawCircle(canvas,centerX+2*radius+linesWidth,centerY,"+1");
                drawCircle(canvas,centerX,centerY,"+1");
                drawCircle(canvas,centerX-radius*2-linesWidth,centerY,"+1");
                drawCircle(canvas,centerX-4*radius-2*linesWidth,centerY,"+1");
                drawCircle(canvas,centerX-4*radius-2*linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX-2*radius-linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX+2*radius+linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX+4*radius+2*linesWidth,centerY+2*radius+linesWidth,"+1");
                canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                break;
            case 2:

                drawCircle(canvas,centerX,centerY-2*radius-linesWidth,"+1");
                drawCircle(canvas,centerX+2*radius+linesWidth,centerY-2*radius-linesWidth,"+1");
                drawCircle(canvas,centerX+4*radius+2*linesWidth,centerY-radius*2-linesWidth,"+1");
                drawCircle(canvas,centerX+4*radius+2*linesWidth,centerY,"+1");
                drawCircle(canvas,centerX+2*radius+linesWidth,centerY,"+1");
                drawCircle(canvas,centerX,centerY,"+1");
                drawCircle(canvas,centerX-radius*2-linesWidth,centerY,"+1");
                drawCircle(canvas,centerX-4*radius-2*linesWidth,centerY,"+1");
                drawCircle(canvas,centerX-4*radius-2*linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX-2*radius-linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX+2*radius+linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX+4*radius+2*linesWidth,centerY+2*radius+linesWidth,"+1");
                canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                break;
            case 3:

                drawCircle(canvas,centerX+2*radius+linesWidth,centerY-2*radius-linesWidth,"+1");
                drawCircle(canvas,centerX+4*radius+2*linesWidth,centerY-radius*2-linesWidth,"+1");
                drawCircle(canvas,centerX+4*radius+2*linesWidth,centerY,"+1");
                drawCircle(canvas,centerX+2*radius+linesWidth,centerY,"+1");
                drawCircle(canvas,centerX,centerY,"+1");
                drawCircle(canvas,centerX-radius*2-linesWidth,centerY,"+1");
                drawCircle(canvas,centerX-4*radius-2*linesWidth,centerY,"+1");
                drawCircle(canvas,centerX-4*radius-2*linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX-2*radius-linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX+2*radius+linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX+4*radius+2*linesWidth,centerY+2*radius+linesWidth,"+1");

                canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-radius,centerY-3*radius-linesWidth,bitmapPaint);

                break;
            case 4:

                drawCircle(canvas,centerX+4*radius+2*linesWidth,centerY-radius*2-linesWidth,"+1");
                drawCircle(canvas,centerX+4*radius+2*linesWidth,centerY,"+1");
                drawCircle(canvas,centerX+2*radius+linesWidth,centerY,"+1");
                drawCircle(canvas,centerX,centerY,"+1");
                drawCircle(canvas,centerX-radius*2-linesWidth,centerY,"+1");
                drawCircle(canvas,centerX-4*radius-2*linesWidth,centerY,"+1");
                drawCircle(canvas,centerX-4*radius-2*linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX-2*radius-linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX+2*radius+linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX+4*radius+2*linesWidth,centerY+2*radius+linesWidth,"+1");

                canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-radius,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius+linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                break;
            case 5:

                drawCircle(canvas,centerX+4*radius+2*linesWidth,centerY,"+1");
                drawCircle(canvas,centerX+2*radius+linesWidth,centerY,"+1");
                drawCircle(canvas,centerX,centerY,"+1");
                drawCircle(canvas,centerX-radius*2-linesWidth,centerY,"+1");
                drawCircle(canvas,centerX-4*radius-2*linesWidth,centerY,"+1");
                drawCircle(canvas,centerX-4*radius-2*linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX-2*radius-linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX+2*radius+linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX+4*radius+2*linesWidth,centerY+2*radius+linesWidth,"+1");

                canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-radius,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius+linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius*3+2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                break;
            case 6:

                drawCircle(canvas,centerX+2*radius+linesWidth,centerY,"+1");
                drawCircle(canvas,centerX,centerY,"+1");
                drawCircle(canvas,centerX-radius*2-linesWidth,centerY,"+1");
                drawCircle(canvas,centerX-4*radius-2*linesWidth,centerY,"+1");
                drawCircle(canvas,centerX-4*radius-2*linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX-2*radius-linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX+2*radius+linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX+4*radius+2*linesWidth,centerY+2*radius+linesWidth,"+1");

                canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-radius,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius+linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius*3+2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius*3+2*linesWidth,centerY-radius,bitmapPaint);

                break;
            case 7:

                drawCircle(canvas,centerX,centerY,"+1");
                drawCircle(canvas,centerX-radius*2-linesWidth,centerY,"+1");
                drawCircle(canvas,centerX-4*radius-2*linesWidth,centerY,"+1");
                drawCircle(canvas,centerX-4*radius-2*linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX-2*radius-linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX+2*radius+linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX+4*radius+2*linesWidth,centerY+2*radius+linesWidth,"+1");

                canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-radius,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius+linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius*3+2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius*3+2*linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius+linesWidth,centerY-radius,bitmapPaint);
                break;
            case 8:

                drawCircle(canvas,centerX-radius*2-linesWidth,centerY,"+1");
                drawCircle(canvas,centerX-4*radius-2*linesWidth,centerY,"+1");
                drawCircle(canvas,centerX-4*radius-2*linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX-2*radius-linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX+2*radius+linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX+4*radius+2*linesWidth,centerY+2*radius+linesWidth,"+1");

                canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-radius,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius+linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius*3+2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius*3+2*linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius+linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX-radius,centerY-radius,bitmapPaint);
                break;
            case 9:

                drawCircle(canvas,centerX-4*radius-2*linesWidth,centerY,"+1");
                drawCircle(canvas,centerX-4*radius-2*linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX-2*radius-linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX+2*radius+linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX+4*radius+2*linesWidth,centerY+2*radius+linesWidth,"+1");

                canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-radius,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius+linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius*3+2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius*3+2*linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius+linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX-radius,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY-radius,bitmapPaint);
                break;
            case 10:

                drawCircle(canvas,centerX-4*radius-2*linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX-2*radius-linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX+2*radius+linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX+4*radius+2*linesWidth,centerY+2*radius+linesWidth,"+1");

                canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-radius,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius+linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius*3+2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius*3+2*linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius+linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX-radius,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY-radius,bitmapPaint);
                break;
            case 11:

                drawCircle(canvas,centerX-2*radius-linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX+2*radius+linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX+4*radius+2*linesWidth,centerY+2*radius+linesWidth,"+1");

                canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-radius,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius+linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius*3+2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius*3+2*linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius+linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX-radius,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY+radius+linesWidth,bitmapPaint);
                break;
            case 12:

                drawCircle(canvas,centerX,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX+2*radius+linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX+4*radius+2*linesWidth,centerY+2*radius+linesWidth,"+1");

                canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-radius,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius+linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius*3+2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius*3+2*linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius+linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX-radius,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY+radius+linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY+radius+linesWidth,bitmapPaint);
                break;
            case 13:

                drawCircle(canvas,centerX+2*radius+linesWidth,centerY+2*radius+linesWidth,"+1");
                drawCircle(canvas,centerX+4*radius+2*linesWidth,centerY+2*radius+linesWidth,"+1");

                canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-radius,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius+linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius*3+2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius*3+2*linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius+linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX-radius,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY+radius+linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY+radius+linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-radius,centerY+radius+linesWidth,bitmapPaint);
                break;
            case 14:

                drawCircle(canvas,centerX+4*radius+2*linesWidth,centerY+2*radius+linesWidth,"+1");

                canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-radius,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius+linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius*3+2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius*3+2*linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius+linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX-radius,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY+radius+linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY+radius+linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-radius,centerY+radius+linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius+linesWidth,centerY+radius+linesWidth,bitmapPaint);
                break;
            case 15:

                canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-radius,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius+linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius*3+2*linesWidth,centerY-3*radius-linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius*3+2*linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius+linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX-radius,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY-radius,bitmapPaint);
                canvas.drawBitmap(bm,centerX-5*radius-2*linesWidth,centerY+radius+linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-3*radius-linesWidth,centerY+radius+linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX-radius,centerY+radius+linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+radius+linesWidth,centerY+radius+linesWidth,bitmapPaint);
                canvas.drawBitmap(bm,centerX+3*radius+2*linesWidth,centerY+radius+linesWidth,bitmapPaint);
                break;
        }
    }
}
