/*
安排要学习的字。

安排的方法如下：
sharedPreference中，保留一个学习的序列，格式为：{繁体字 简体字 学习次数}
根据长度，判断是否需要去数据库中获取更多的字。长度公式：每天学习个数 * 3
当用户点击下一个的按钮的时候，根据写错的次数去更新sharedPreference中的学习次数。
如果写错的字数大于一个值5？7？，那么就保持不变。如果小于那个值，就学习次数加1。
当学习次数大过三次的时候，就去掉这个字。

初始情况，words为空。去查到n个字，读取每个json

后期的改进:https://www.supermemo.com/en/archives1990-2015/english/ol/sm2
使用SM-2算法来加强记忆。

学习流程：
前期安排字的时候，按照老流程进行。
当经过了测试模式之后，开始使用SM-2算法来安排记忆。
*/

package com.test.algorithm;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.test.model.entity.LearnItem;
import com.test.util.SQLdm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Schedule {

    private Context context;

    private static int wordsPerDay = -1;

    public Schedule(Context context) {
        this.context = context;
        if (wordsPerDay == -1) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("fan_data", 0);
            wordsPerDay = sharedPreferences.getInt("wordsPerDay", 20);
        }
    }

    /**
     * 从sharedPreference中获取今天需要练的字，如果不够，从数据库中取
     * @return List<LearnItem>
     */
    public List<LearnItem> getWords() {
        List<LearnItem> words = new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences("fan_data", 0);
        String todayWords = sharedPreferences.getString("today_words", "");
        if (todayWords.length() < 3 * wordsPerDay) {
            int wordsNeedToQuery = wordsPerDay - todayWords.length() / 3;
            String newWords = getNewWords(wordsNeedToQuery);
            todayWords = todayWords + newWords;
        }
        for (int i = 0; i < todayWords.length(); i += 3) {
            String traditional = "" + todayWords.charAt(i);
            String simplified = "" + todayWords.charAt(i + 1);
            int learnTimes = todayWords.charAt(i + 2) - '0';
            LearnItem item = new LearnItem(traditional, simplified, learnTimes);
            words.add(item);
        }
        return words;
    }

    /**
     * 这个函数用于更新SharedPreferences内的东西，更新学习的字条目
     * 如果学习次数超过了需要学习的次数，就删除掉这个字，并且删除对应的json
     * @param words
     */
    public void updateSharedPreference(List<LearnItem> words) {
        StringBuilder wordsList = new StringBuilder();
        for (LearnItem word : words) {
            if (word.getLearnTimes() > 3) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("fan_data", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(word.getTraditional());
                editor.remove(word.getSimplified());
                editor.commit();
                continue;
            }
            wordsList.append(word.getTraditional());
            wordsList.append(word.getSimplified());
            wordsList.append(word.getLearnTimes());
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("fan_data", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("today_words", wordsList.toString());
        editor.commit();
    }

    /**
     * 1. 查询wordsNeedToQuery个字
     * 2. 将查到的条目的字的两个json存储到sharedPreferences
     * @param wordsNeedToQuery
     * @return wordsNeedToQuery个字的学习列表 格式： {繁体}{简体}{次数}
     */
    private String getNewWords(int wordsNeedToQuery) {
        SQLiteDatabase database = new SQLdm().openDataBase(context);
        // 这里需要找到一条就马上结束
        Cursor cursor = database.rawQuery("select * from words where learnTimes = 0", null);
        StringBuilder newWords = new StringBuilder();
        if (cursor.moveToFirst()) {
            int i = 0;
            do {
                i = i + 1;
                int learnTimes = 1;
                String traditional = cursor.getString(cursor.getColumnIndex("traditional"));
                String simplified = cursor.getString(cursor.getColumnIndex("simplified"));
                newWords.append(traditional).append(simplified).append(learnTimes);

                // 更新这个字的学习日期
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                String today = sdf.format(new Date());
                String sql = "update words set learnTimes = 1," +
                        "lastTime = \"" + new Date() + "\" " +
                        " where traditional = \"" + traditional + "\"";
                database.execSQL(sql);
            } while (i < wordsNeedToQuery && cursor.moveToNext());
        }
        return newWords.toString();
    }
}
