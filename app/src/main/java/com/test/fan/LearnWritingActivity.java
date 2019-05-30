package com.test.fan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.squareup.picasso.Picasso;
import com.test.view.HeightAdaptImageView;

import java.util.ArrayList;
import java.util.List;

public class LearnWritingActivity extends AppCompatActivity {

    HeightAdaptImageView imageViewSimplified;
    HeightAdaptImageView imageViewTraditional;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_writing);

        imageViewSimplified = findViewById(R.id.imageViewSimplified);
        imageViewTraditional = findViewById(R.id.imageViewTraditional);

        Picasso.get().load("http://img.qqzhi.com/uploads/2019-04-29/191416264.jpg").into(imageViewSimplified);
        Picasso.get().load("http://img.qqzhi.com/uploads/2019-05-05/200800182.jpg").into(imageViewTraditional);

        List<String> words = getTodayWords();
        // 根据words中第一个字去设置WebView中的内容
    }

    // 作用：获取今天的字的链表，暂且写成如此，做调试用
    private List<String> getTodayWords() {
        List<String> words = new ArrayList<>();
        words.add("龍");
        return words;
    }
}
