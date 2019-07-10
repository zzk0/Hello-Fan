package com.test.fan;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.test.model.entity.LearnItem;
import com.test.util.SQLdm;
import com.test.view.HanziView;

import java.util.ArrayList;
import java.util.List;

public class LearnOneWordActivity extends AppCompatActivity {

    private HanziView hanziView;
    private HanziView simplifiedHanzi;
    private HanziView traditionalHanzi;
    private TextView pinyinTextView;
    private TextView phraseTextView;
    private List<String> words;

    private boolean firstFocusChange = true;
    private int currentWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_one_word);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        hanziView = findViewById(R.id.hanzi_view_one);
        simplifiedHanzi = findViewById(R.id.simplified_word_one);
        traditionalHanzi = findViewById(R.id.traditional_word_one);
        pinyinTextView = findViewById(R.id.pinyin_textview_one);
        phraseTextView = findViewById(R.id.phrase_textview_one);
        Button buttonClean = findViewById(R.id.button_clean_one);
        Button buttonNext = findViewById(R.id.button_next_one);
        buttonClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hanziView.resetHanzi();
            }
        });
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHanzi();
            }
        });


        Intent intent = getIntent();
        String phrase = intent.getStringExtra("phrase");
        words = phraseToWords(phrase);
        currentWord = 0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (firstFocusChange) {
            firstFocusChange = false;
            setHanzi();
        }
    }

    private void setHanzi() {
        if (currentWord >= words.size()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("您已经学完这个词！");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LearnOneWordActivity.this.finish();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return;
        }

        final LearnItem word = wordToLearnItem(words.get(currentWord));
        currentWord = currentWord + 1;
        traditionalHanzi.setOnClickListener(null);

        studyMode(word);

        // 更新拼音，词组信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 更新拼音
                final String pinyinText = updatePinyin(word);
                pinyinTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        pinyinTextView.setText(pinyinText);
                    }
                });

                // 更新词组
                final String phraseText = updatePhrase(word);
                phraseTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        phraseTextView.setText(phraseText);
                    }
                });
            }
        }).start();
    }

    private void studyMode(LearnItem word) {
        getSupportActionBar().setTitle("学习模式");
        simplifiedHanzi.setHaveOuterBackground(true);
        simplifiedHanzi.setCharacterColor(Color.BLACK);
        simplifiedHanzi.setCharacter(word.getSimplified());
        traditionalHanzi.setHaveOuterBackground(true);
        traditionalHanzi.setLoopAnimate(true);
        traditionalHanzi.setCharacter(word.getTraditional());
        hanziView.setHaveOuterBackground(true);
        hanziView.setHaveInnerBackground(true);
        hanziView.setQuiz();
        hanziView.setCharacterColor(Color.GRAY);
        hanziView.setTestMode(false);
        hanziView.setCharacter(word.getTraditional());
    }

    private List<String> phraseToWords(String phrase) {
        List<String> words = new ArrayList<>();
        for (int i = 0; i < phrase.length(); i++) {
            words.add("" + phrase.charAt(i));
        }
        return words;
    }

    private String updatePinyin(LearnItem word) {
        SQLiteDatabase database = new SQLdm().openDataBase(this);

        // 查询拼音
        Cursor cursor = database.rawQuery("select * from dict where words = '" + word.getTraditional() + "'", null);
        StringBuilder pinyin = new StringBuilder();
        while (cursor.moveToNext()) {
            pinyin.append(cursor.getString(cursor.getColumnIndex("spell"))).append("  ");
        }
        cursor.close();
        return getResources().getString(R.string.pinyin) +  ": " + pinyin.toString();
    }

    private String updatePhrase(LearnItem word) {
        SQLiteDatabase database = new SQLdm().openDataBase(this);

        // 查询2~3个词组
        String sql = "select * from dict where words like '%" + word.getTraditional() + "%' order by length(words)";
        Cursor cursor = database.rawQuery(sql, null);
        int count = 0;
        StringBuilder phrase = new StringBuilder();
        while (cursor.moveToNext() && count < 3) {
            count = count + 1;
            phrase.append(cursor.getString(cursor.getColumnIndex("words"))).append("  ");
        }
        cursor.close();
        return getResources().getString(R.string.phrase) + ": " + phrase.toString();
    }

    private LearnItem wordToLearnItem(String word) {
        SQLiteDatabase database = new SQLdm().openDataBase(getApplicationContext());
        Cursor cursor = database.rawQuery("select * from words where traditional = '" + word + "'", null);
        if (cursor.moveToFirst()) {
            String simplified = cursor.getString(cursor.getColumnIndex("simplified"));
            LearnItem item = new LearnItem(word, simplified, 1);
            return item;
        }
        else {
            LearnItem item = new LearnItem(word, word, 1);
            return item;
        }
    }
}
