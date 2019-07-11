package com.test.fan;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.test.model.entity.Readings;
import com.test.util.ACache;
import com.test.util.DBHelper;
import com.test.util.SQLdm;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


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
    private View dialog_view;
    private ACache aCache;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readingsdisplay);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toast_view=View.inflate(this,R.layout.toast_view,null);
        title_tv =(TextView)findViewById(R.id.title);
        content_tv =(TextView)findViewById(R.id.content);
        imageView=(ImageView)findViewById(R.id.imageView);
        group_tv=(TextView)findViewById(R.id.group);
        date_tv=(TextView)findViewById(R.id.date);
        readings =(Readings) getIntent().getSerializableExtra("readings");
        aCache=ACache.get(this);
        initDisplay();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
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
                    ds.setColor(Color.BLACK);       //设置字体颜色
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
                String s = content_tv
                        .getText()
                        .subSequence(content_tv.getSelectionStart()-1,
                                content_tv.getSelectionEnd()).toString();
                String[] ss=getWordInfo(s);
                if(s.replace((char)12288+"","").trim().equals("")||ss[1].equals(""))return;
                if(mBottomDialog==null)
                    initBottomDialog();
                TextView word_tv,express_tv,spell_tv;
                word_tv=(TextView) dialog_view.findViewById(R.id.word);
                express_tv=(TextView) dialog_view.findViewById(R.id.word_express);
                spell_tv=(TextView)dialog_view.findViewById(R.id.spell);
                word_tv.setText(s);
                express_tv.setText((ss[0].equals("") ? s : ss[0]) + "  (" + ss[2] + ")");
                spell_tv.setText("/\' " + ss[1] + " \'/");
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
        String content= aCache.getAsString(readings.getUrl());
        if(content!=null)
        {
            readings.setContent(content);
            setGetWordListener();
        }
        else {
            handler = new Handler() {
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                        setGetWordListener();
                    }
                }
            };
            getReadingsContent(readings.getUrl());
        }
    }

    private void getReadingsContent( final String content_url)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection conn = Jsoup.connect(content_url);
                    conn.userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50");
                    Document content = conn.get();
                    String s=content.select("div.TRS_Editor>p[align=justify],div.TRS_Editor>div>p[align=justify]").text();
                    if(s.equals(""))
                        s=content.select("div.TRS_Editor>p,div.TRS_Editor>div>p").text();
                    s=s.replace(" 　　","\n 　　");
                    readings.setContent(s);
                    aCache.put(content_url,s,43200);
                    Message msg=new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    /**
     * 初始化分享弹出框
     */
    private void initBottomDialog() {
        mBottomDialog = new Dialog(this, R.style.dialog_bottom_full);
        mBottomDialog.setCanceledOnTouchOutside(false);
        Window window = mBottomDialog.getWindow();
        //设置Dialog外窗口可以点击
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//核心
        window.setAttributes(layoutParams);
        window.setDimAmount(0f);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.share_animation);
        dialog_view = View.inflate(this, R.layout.dialog_fan, null);
        dialog_view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBottomDialog != null && mBottomDialog.isShowing()) {
                    mBottomDialog.dismiss();
                }
            }
        });
        final Context context=this;
        dialog_view.findViewById(R.id.collect_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("已成功收藏");
            }
        });
        window.setContentView(dialog_view);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);//设置横向全屏
    }
    private String[] getWordInfo(String s)
    {
        SQLiteDatabase sqLiteDatabase= new SQLdm().openDataBase(ReadingsDisplayActivity.this);
        Cursor words_cursor = sqLiteDatabase.rawQuery("select simplified from words where traditional=\'"+s+"\'",null);
        Cursor dict_cursor=sqLiteDatabase.rawQuery("select spell,express from dict where words=\'"+s+"\'",null);
        String traditional="";
        String spell="";
        String express="";
        while (words_cursor.moveToNext()) {
            traditional = words_cursor.getString(words_cursor.getColumnIndex("simplified"));
        }
        while (dict_cursor.moveToNext()) {
            spell = dict_cursor.getString(dict_cursor.getColumnIndex("spell"));
            express = dict_cursor.getString(dict_cursor.getColumnIndex("express"));
        }
        return new String[]{traditional,spell,express};

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

}
