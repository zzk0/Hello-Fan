package com.test.fan;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.test.model.Readings;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ReadingsDisplayActivity extends AppCompatActivity {
    private View toast_view;
    private Dialog mBottomDialog;
    private Handler handler;
    private TextView title_tv;
    private Readings readings;
    private ImageView imageView;
    private TextView content_tv;
    private TextView group_tv;
    private TextView date_tv;
    private View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readingsdisplay);
        toast_view=View.inflate(this,R.layout.toast_view,null);
        title_tv =(TextView)findViewById(R.id.title);
        content_tv =(TextView)findViewById(R.id.content);
        imageView=(ImageView)findViewById(R.id.imageView);
        group_tv=(TextView)findViewById(R.id.group);
        date_tv=(TextView)findViewById(R.id.date);
        readings =(Readings) getIntent().getSerializableExtra("readings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        System.out.println(readings.getUrl());
        initDisplay();

    }
    private void setGetWordListener()
    {
        SpannableStringBuilder s = new SpannableStringBuilder(readings.getContent());
        for(int i=0;i<s.length();i++){
            s.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View v) {
                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(0xff000000);       //设置字体颜色
                    ds.setUnderlineText(false);      //设置下划线
                }
            },i,i+1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        //设置textview中所有文字为spannable格式
        content_tv.setText(s,TextView.BufferType.SPANNABLE);
        //启用上面为每个字绑定的ClickableSpan
        content_tv.setMovementMethod(LinkMovementMethod.getInstance());
        final Context context=this;
        content_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //若没有绑定clickableSpan，无法使用subSequence方法
                //若tv.getSelectionStart()-1,则输出点击的文字以及其上一个文字
                //若tv.getSelectionEnd()+1,则输出点击的文字以及其下一个文字，如此类推
                //通过标点判断还可截取一段文字中我们所点击的那句话
                TextView tv = (TextView) v;
                String s = tv
                        .getText()
                        .subSequence(tv.getSelectionStart()-1,
                                tv.getSelectionEnd()).toString();
                if(mBottomDialog==null)
                    initBottomDialog();
                updateDate(s);
                if(!mBottomDialog.isShowing())
                    mBottomDialog.show();
            }
        });

    }
    private void initDisplay()
    {
        if(!readings.getGroup().equals(""))
            group_tv.setText(readings.getGroup()+"    ");
        date_tv.setText(readings.getDate());
        title_tv.setText(readings.getTitle());
        if(!readings.getImg_url().equals(""))
        {
            Glide.with(this).load(readings.getImg_url()).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into( imageView );
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                getReadingsContent(readings.getUrl());
            }
        }).start();
        handler=new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    setGetWordListener();
                }
            }
        };
    }
    private void showToast(String s)
    {
        Toast toast=new Toast(this);
        TextView toast_tv=(TextView)toast_view.findViewById(R.id.toast_tv);
        toast_tv.setText(s);
        toast.setView(toast_view);
        toast.setGravity(Gravity.BOTTOM,0,75);
        toast.show();
    }
    private void getReadingsContent( String content_url)
    {

                Document content= null;
                try {
                    content = Jsoup.connect(content_url).get();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                String s=content.select("div.TRS_Editor>p[align=justify],div.TRS_Editor>div>p[align=justify]").text();
                if(s.equals(""))
                    s=content.select("div.TRS_Editor>p,div.TRS_Editor>div>p").text();
                s=s.replace(" 　　","\n 　　");
                readings.setContent(s);
                Message msg=new Message();
                msg.what = 1;
                handler.sendMessage(msg);
    }
    /**
     * 显示分享弹出框
     */
    private void updateDate(String s)
    {
        TextView word_tv=(TextView)view.findViewById(R.id.word);
        TextView info_tv=(TextView)view.findViewById(R.id.word_info);
        word_tv.setText(s);
        info_tv.setText(s);
    }
    /**
     * 初始化分享弹出框
     */

    private void initBottomDialog() {
        mBottomDialog = new Dialog(this, R.style.dialog_bottom_full);
        mBottomDialog.setCanceledOnTouchOutside(false);
        Window window = mBottomDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.share_animation);
        view = View.inflate(this, R.layout.dialog_fan, null);
        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBottomDialog != null && mBottomDialog.isShowing()) {
                    mBottomDialog.dismiss();
                }
            }
        });
        final Context context=this;
        view.findViewById(R.id.collect_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("已成功收藏");
            }
        });
        window.setContentView(view);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);//设置横向全屏
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
