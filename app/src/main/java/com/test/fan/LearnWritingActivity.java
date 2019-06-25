package com.test.fan;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.test.algorithm.Schedule;
import com.test.model.Tuple;
import com.test.util.SQLdm;
import com.test.view.HanziView;

import java.util.ArrayList;
import java.util.List;

public class LearnWritingActivity extends AppCompatActivity {

    HanziView hanziView;
    HanziView simplifiedHanzi;
    HanziView traditionalHanzi;

    List<Tuple<String, String, Integer>> words;
    int currentWord;
    private boolean firstFocusChange = true;

    TextView pinyinTextView;
    TextView phraseTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_writing);

        words = getTodayWords();
        hanziView = findViewById(R.id.hanzi_view);
        simplifiedHanzi = findViewById(R.id.simplified_word);
        traditionalHanzi = findViewById(R.id.traditional_word);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pinyinTextView = findViewById(R.id.pinyin_textview);
        phraseTextView = findViewById(R.id.phrase_textview);

        // 根据是否有效时间内容去判断currentWord为0，还是继续上一次
        SharedPreferences sharedPreferences = getSharedPreferences("fan_data", 0);
        currentWord = sharedPreferences.getInt("current_word", 0);
    }

    @Override
    protected void onDestroy() {
        Schedule schedule = new Schedule(this);
        schedule.updateSharedPreference(words);

        SharedPreferences sharedPreferences = getSharedPreferences("fan_data", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("current_word", currentWord);
        editor.commit();
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (firstFocusChange) {
            firstFocusChange = false;
            setHanzi();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    // 作用：获取今天的字的链表，暂且写成如此，做调试用
    private List<Tuple<String, String, Integer>> getTodayWords() {
        return new Schedule(this).getWords();
    }

    public void click(View view) {
        switch (view.getId()) {
            case R.id.button_clean:
                hanziView.resetHanzi();
                break;
            case R.id.button_next:
                if (hanziView.getWrongTimes() < 5) {
                    words.get(currentWord).third = words.get(currentWord).third + 1;
                }
                currentWord = currentWord + 1;
                setHanzi();
                break;
        }
    }

    private void setHanzi() {
        Tuple<String, String, Integer> word = words.get(currentWord);
        traditionalHanzi.setOnClickListener(null);

        SQLiteDatabase database = new SQLdm().openDataBase(this);

        // 查询拼音
        Cursor cursor = database.rawQuery("select * from dict where words = '" + word.first + "'", null);
        StringBuilder pinyin = new StringBuilder();
        cursor.moveToFirst();
        do {
            pinyin.append(cursor.getString(cursor.getColumnIndex("spell")) + "  ");
        } while (cursor.moveToNext());
        pinyinTextView.setText(getResources().getString(R.string.pinyin) +  ": " + pinyin.toString());
        cursor.close();

        // 查询2~3个词组
        String sql = "select * from dict where words like '" + word.first + "%' order by length(words)";
        cursor = database.rawQuery(sql, null);
        int count = 0;
        StringBuilder phrase = new StringBuilder();
        cursor.moveToNext();
        while (cursor.moveToNext() && count < 3) {
            count = count + 1;
            phrase.append(cursor.getString(cursor.getColumnIndex("words")) + "  ");
        }
        phraseTextView.setText(getResources().getString(R.string.phrase) + ": " + phrase.toString());
        cursor.close();

        switch (word.third) {
            case 0:
                getSupportActionBar().setTitle("学习模式");
                studyMode(word);
                break;
            case 1:
                getSupportActionBar().setTitle("再认模式");
                recognizeMode(word);
                break;
            case 2:
                getSupportActionBar().setTitle("测试模式");
                testMode(word);
                break;
            default:
                break;
        }
    }

    private void studyMode(Tuple<String, String, Integer> word) {
        simplifiedHanzi.setHaveOuterBackground(true);
        simplifiedHanzi.setCharacterColor(Color.BLACK);
        simplifiedHanzi.setCharacter(word.second);
        traditionalHanzi.setHaveOuterBackground(true);
        traditionalHanzi.setLoopAnimate(true);
        traditionalHanzi.setCharacter(word.first);
        hanziView.setHaveOuterBackground(true);
        hanziView.setHaveInnerBackground(true);
        hanziView.setQuiz();
        hanziView.setCharacter(word.first);
    }

    private void recognizeMode(Tuple<String, String, Integer> word) {
        simplifiedHanzi.setHaveOuterBackground(true);
        simplifiedHanzi.setCharacterColor(Color.BLACK);
        simplifiedHanzi.setCharacter(word.second);
        traditionalHanzi.setHaveOuterBackground(true);
        traditionalHanzi.setLoopAnimate(false);
        traditionalHanzi.setClickToAnimate(true);
        traditionalHanzi.setCharacter(word.first);
        hanziView.setHaveOuterBackground(true);
        hanziView.setHaveInnerBackground(true);
        hanziView.setQuiz();
        hanziView.setCharacter(word.first);
    }

    private void testMode(Tuple<String, String, Integer> word) {
        simplifiedHanzi.setHaveOuterBackground(true);
        simplifiedHanzi.setCharacterColor(Color.BLACK);
        simplifiedHanzi.setCharacter(word.second);
        traditionalHanzi.setHaveOuterBackground(false);
        traditionalHanzi.cleanCharacter();
        hanziView.setHaveOuterBackground(true);
        hanziView.setHaveInnerBackground(true);
        hanziView.setQuiz();
        hanziView.setCharacterColor(Color.TRANSPARENT);
        hanziView.setCharacter(word.first);
    }
}
