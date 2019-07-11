package com.test.fan;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.test.algorithm.Schedule;
import com.test.algorithm.SuperMemo;
import com.test.model.entity.LearnItem;
import com.test.model.entity.ReviewItem;
import com.test.util.SQLdm;
import com.test.view.HanziView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LearnWritingActivity extends AppCompatActivity {

    HanziView hanziView;
    HanziView simplifiedHanzi;
    HanziView traditionalHanzi;

    List<LearnItem> words;
    int currentWord;
    private boolean firstFocusChange = true;

    TextView pinyinTextView;
    TextView phraseTextView;
    Handler textViewHandler;

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
        textViewHandler = new Handler();

        // 根据是否有效时间内去判断currentWord为0，还是继续上一次
        SharedPreferences sharedPreferences = getSharedPreferences("fan_data", 0);
        String lastDay = sharedPreferences.getString("last_learn_date", "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        if (!lastDay.equals(today)) {
            currentWord = sharedPreferences.getInt("current_word", 0);
        }
        else {
            currentWord = 0;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("last_learn_date", today);
            editor.apply();
        }
    }

    @Override
    protected void onDestroy() {
        Schedule schedule = new Schedule(this);
        schedule.updateSharedPreference(words);

        SharedPreferences sharedPreferences = getSharedPreferences("fan_data", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("current_word", currentWord);
        editor.apply();

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

    private List<LearnItem> getTodayWords() {
        final List<LearnItem> todayWords = new Schedule(this).getWords();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SuperMemo superMemo = new SuperMemo(getApplicationContext());
                List<ReviewItem> items = superMemo.getReviewItems();
                synchronized (this) {
                    words.addAll(items);
//                    for (LearnItem item : items) {
//                        words.add(item);
//                    }
                }
            }
        }).start();
        return todayWords;
    }

    public void click(View view) {
        switch (view.getId()) {
            case R.id.button_clean:
                hanziView.resetHanzi();
                break;
            case R.id.button_next:
                if (!hanziView.hanziFinish()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("提示");
                    builder.setMessage("请您写好这个字");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else {
                    updateHanziState();
                    setHanzi();
                }
                break;
        }
    }

    private synchronized void updateHanziState() {
        if (currentWord >= words.size()) return;
        final LearnItem item = words.get(currentWord);
        if (item instanceof ReviewItem) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ((ReviewItem) item).updateDb(getApplicationContext(), hanziView.getWrongTimes());
                }
            }).start();
        }
        else if (hanziView.getWrongTimes() < 5) {
            item.setLearnTimes(item.getLearnTimes() + 1);
            // 加入复习
            if (item.getLearnTimes() > 3) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SuperMemo.addReviewItem(getApplicationContext(), item.getTraditional());
                    }
                }).start();
            }
            else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SQLiteDatabase database = new SQLdm().openDataBase(getApplicationContext());
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String today = sdf.format(new Date());
                        String sql = "update words set learnTimes = 1," +
                                " learnDate = \"" + today + "\", " +
                                "lastTime = \"" + new Date() + "\" " +
                                " where traditional = \"" + item.getTraditional() + "\"";
                        database.execSQL(sql);
                    }
                }).start();
            }
        }
        currentWord = currentWord + 1;
    }

    private synchronized void setHanzi() {
        if (currentWord >= words.size()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("您已经学完今天的任务了！");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LearnWritingActivity.this.finish();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return;
        }
        final LearnItem word = words.get(currentWord);
        traditionalHanzi.setOnClickListener(null);

        // 更新拼音，词组信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 更新拼音
                final String pinyinText = updatePinyin(word);
                textViewHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        pinyinTextView.setText(pinyinText);
                    }
                });

                // 更新词组
                final String phraseText = updatePhrase(word);
                textViewHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        phraseTextView.setText(phraseText);
                    }
                });
            }
        }).start();

        switch (word.getLearnTimes()) {
            case 1:
                studyMode(word);
                break;
            case 2:
                recognizeMode(word);
                break;
            case 3:
                testMode(word);
                break;
            case 4:
                reviewMode(word);
            default:
                break;
        }
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

    private void recognizeMode(LearnItem word) {
        getSupportActionBar().setTitle("再认模式");
        simplifiedHanzi.setHaveOuterBackground(true);
        simplifiedHanzi.setCharacterColor(Color.BLACK);
        simplifiedHanzi.setCharacter(word.getSimplified());
        traditionalHanzi.setHaveOuterBackground(true);
        traditionalHanzi.setLoopAnimate(false);
        traditionalHanzi.setClickToAnimate(true);
        traditionalHanzi.setCharacter(word.getTraditional());
        hanziView.setHaveOuterBackground(true);
        hanziView.setHaveInnerBackground(true);
        hanziView.setQuiz();
        hanziView.setCharacterColor(Color.GRAY);
        hanziView.setTestMode(false);
        hanziView.setCharacter(word.getTraditional());
    }

    private void testMode(LearnItem word) {
        getSupportActionBar().setTitle("测试模式");
        simplifiedHanzi.setHaveOuterBackground(true);
        simplifiedHanzi.setCharacterColor(Color.BLACK);
        simplifiedHanzi.setCharacter(word.getSimplified());
        traditionalHanzi.setHaveOuterBackground(false);
        traditionalHanzi.cleanCharacter();
        hanziView.setHaveOuterBackground(true);
        hanziView.setHaveInnerBackground(true);
        hanziView.setQuiz();
        hanziView.setCharacterColor(Color.TRANSPARENT);
        hanziView.setTestMode(true);
        hanziView.setCharacter(word.getTraditional());
    }

    private void reviewMode(LearnItem word) {
        getSupportActionBar().setTitle("复习模式");
        simplifiedHanzi.setHaveOuterBackground(true);
        simplifiedHanzi.setCharacterColor(Color.BLACK);
        simplifiedHanzi.setCharacter(word.getSimplified());
        traditionalHanzi.setHaveOuterBackground(false);
        traditionalHanzi.cleanCharacter();
        hanziView.setHaveOuterBackground(true);
        hanziView.setHaveInnerBackground(true);
        hanziView.setQuiz();
        hanziView.setCharacterColor(Color.TRANSPARENT);
        hanziView.setTestMode(true);
        hanziView.setCharacter(word.getTraditional());
    }
}
