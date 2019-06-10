package com.test.fan;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.test.model.Tuple;
import com.test.view.HanziView;

import java.util.ArrayList;
import java.util.List;

public class LearnWritingActivity extends AppCompatActivity {

    HanziView hanziView;
    HanziView simplifiedHanzi;
    HanziView traditionalHanzi;

    List<Tuple<String, String, Integer>> words;
    int currentWord = 0;
    private boolean firstFocusChange = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_writing);

        words = getTodayWords();
        hanziView = findViewById(R.id.hanzi_view);
        simplifiedHanzi = findViewById(R.id.simplified_word);
        traditionalHanzi = findViewById(R.id.traditional_word);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        List<Tuple<String, String, Integer>> words = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("fan_data", 0);
        String todayWords = sharedPreferences.getString("words", "");
        for (int i = 0; i < todayWords.length(); i += 3) {
            words.add(new Tuple<>("" + todayWords.charAt(i), "" + todayWords.charAt(i + 1), Integer.valueOf(todayWords.charAt(i + 2))));
        }
        return words;
    }

    public void click(View view) {
        switch (view.getId()) {
            case R.id.button_clean:
                hanziView.resetHanzi();
                break;
            case R.id.button_next:
                currentWord = currentWord + 1;
                setHanzi();
                break;
        }
    }

    private void setHanzi() {
        simplifiedHanzi.setHaveOutterBackground(true);
        simplifiedHanzi.setCharacterColor(Color.BLACK);
        simplifiedHanzi.setCharacter(words.get(currentWord).second);
        traditionalHanzi.setHaveOutterBackground(true);
        traditionalHanzi.setLoopAnimate(true);
        traditionalHanzi.setCharacter(words.get(currentWord).first);
        hanziView.setHaveOutterBackground(true);
        hanziView.setHaveInnerBackground(true);
        hanziView.setQuiz();
        hanziView.setCharacter(words.get(currentWord).first);
    }
}
