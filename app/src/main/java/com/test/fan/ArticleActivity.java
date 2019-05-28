/*
说明：这个是阅读文章的Activity，在ReadingFragment的列表中，选中一个item弹出来的Activity
*/

package com.test.fan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
    }
}
