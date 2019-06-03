package com.test.fan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import com.squareup.picasso.Picasso;
import com.test.model.Hanzi;
import com.test.view.HanziView;
import com.test.view.HeightAdaptImageView;

import java.util.ArrayList;
import java.util.List;

public class LearnWritingActivity extends AppCompatActivity {

    HeightAdaptImageView imageViewSimplified;
    HeightAdaptImageView imageViewTraditional;
    HanziView hanziView;

    List<String> words;
    int currentWord = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_writing);

        imageViewSimplified = findViewById(R.id.imageview_simplified);
        imageViewTraditional = findViewById(R.id.imageview_traditional);
        Picasso.get().load("http://img.qqzhi.com/uploads/2019-04-29/191416264.jpg").into(imageViewSimplified);
        Picasso.get().load("http://img.qqzhi.com/uploads/2019-05-05/200800182.jpg").into(imageViewTraditional);

        hanziView = findViewById(R.id.hanzi_view);
        hanziView.setCharacter("龍");
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

    public void click(View view) {
        switch (view.getId()) {
            case R.id.button_clean:
                hanziView.resetHanzi();
                break;
            case R.id.button_next:
                hanziView.advance();
                break;
            case R.id.button_prompt:
                hanziView.prompt();
                break;
        }
    }
}
