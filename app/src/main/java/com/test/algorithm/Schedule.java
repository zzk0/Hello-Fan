/*
安排要学习的字。

安排的方法如下：
sharedPreference中，保留一个学习的序列，格式为：{繁体字 简体字 学习次数}
根据长度，判断是否需要去数据库中获取更多的字。长度公式：每天学习个数 * 3
当用户点击下一个的按钮的时候，根据写错的次数去更新sharedPreference中的学习次数。
如果写错的字数大于一个值5？7？，那么就保持不变。如果小于那个值，就学习次数加1。
当学习次数大过三次的时候，就去掉这个字。

初始情况，words为空。去查到n个字，读取每个json
*/

package com.test.algorithm;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.test.model.Tuple;
import com.test.util.SQLdm;

import java.util.ArrayList;
import java.util.List;

public class Schedule {

    private Context context;

    public Schedule(Context context) {
        this.context = context;
    }

    /**
     * 从sharedPreference中获取今天需要练的字，如果不够，从数据库中取
     * @return words三元组列表，格式{繁体，简体，学习次数}
     */
    public List<Tuple<String, String, Integer>> getWords() {
        List<Tuple<String, String, Integer>> words = new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences("fan_data", 0);
        String todayWords = sharedPreferences.getString("today_words", "");
        if (todayWords.length() < 3 * 20) {
            int wordsNeedToQuery = 20 - todayWords.length() / 3;
            String newWords = getNewWords(wordsNeedToQuery);
            todayWords = todayWords + newWords;
        }
        for (int i = 0; i < todayWords.length(); i += 3) {
            String traditional = "" + todayWords.charAt(i);
            String simplified = "" + todayWords.charAt(i + 1);
            int learnTimes = todayWords.charAt(i + 2) - '0';
            Tuple<String, String, Integer> tuple = new Tuple<>(traditional, simplified, learnTimes);
            words.add(tuple);
        }
        return words;
    }

    /**
     * 这个函数用于更新SharedPreferences内的东西，更新学习的字条目
     * 如果学习次数超过了需要学习的次数，就删除掉这个字，并且删除对应的json
     * @param words
     */
    public void updateSharedPreference(List<Tuple<String, String, Integer>> words) {
        StringBuilder wordsList = new StringBuilder();
        for (Tuple<String, String, Integer> word : words) {
            if (word.third > 3) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("fan_data", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(word.first);
                editor.remove(word.second);
                editor.commit();
                continue;
            }
            wordsList.append(word.first);
            wordsList.append(word.second);
            wordsList.append(word.third.toString());
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
                int learnTimes = 0;
                String traditional = cursor.getString(cursor.getColumnIndex("traditional"));
                String simplified = cursor.getString(cursor.getColumnIndex("simplified"));
                String tradJson = cursor.getString(cursor.getColumnIndex("tradJson"));
                String simpJson = cursor.getString(cursor.getColumnIndex("simpJson"));
                newWords.append(traditional).append(simplified).append(learnTimes);

                // 存储json到本地
                SharedPreferences sharedPreferences = context.getSharedPreferences("fan_data", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(traditional, tradJson);
                editor.putString(simplified, simpJson);
                editor.commit();

                // 更新这个字的学习日期
            } while (i < wordsNeedToQuery && cursor.moveToNext());
        }
        return newWords.toString();
    }
}
