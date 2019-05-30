package com.test.fan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import com.squareup.picasso.Picasso;
import com.test.view.HeightAdaptImageView;

import java.util.ArrayList;
import java.util.List;

public class LearnWritingActivity extends AppCompatActivity {

    HeightAdaptImageView imageViewSimplified;
    HeightAdaptImageView imageViewTraditional;
    WebView webView;
    Button buttonClean;
    Button buttonNext;

    List<String> words;
    int currentWord = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_writing);

        imageViewSimplified = findViewById(R.id.imageview_simplified);
        imageViewTraditional = findViewById(R.id.imageview_traditional);
        webView = findViewById(R.id.webview_writing);
        buttonClean = findViewById(R.id.button_clean);
        buttonNext = findViewById(R.id.button_next);

        Picasso.get().load("http://img.qqzhi.com/uploads/2019-04-29/191416264.jpg").into(imageViewSimplified);
        Picasso.get().load("http://img.qqzhi.com/uploads/2019-05-05/200800182.jpg").into(imageViewTraditional);

        webView.setWebChromeClient(new WebChromeClient());
        WebSettings set = webView.getSettings();
        set.setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/1.html");

        // 根据words中第一个字去设置WebView中的内容
        words = getTodayWords();
        webView.loadUrl("javascript: newCharacter(" + words.get(currentWord) +")");
        currentWord = currentWord + 1;

        buttonClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl("javascript:reset()");
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentWord + 1 < words.size()) {
                    webView.loadUrl("javascript: newCharacter(\'" + words.get(currentWord) +"\')");
                    currentWord = currentWord + 1;
                }
            }
        });
    }

    // 作用：获取今天的字的链表，暂且写成如此，做调试用
    private List<String> getTodayWords() {
        List<String> words = new ArrayList<>();
        words.add("龍");
        words.add("黨");
        words.add("齊");
        words.add("飛");
        words.add("風");
        words.add("電");
        words.add("靈");
        words.add("醫");
        return words;
    }
}
