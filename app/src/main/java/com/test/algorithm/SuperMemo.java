/*
实现SM2算法。
参考链接：https://www.supermemo.com/en/archives1990-2015/english/ol/sm2
http://www.blueraja.com/blog/477/a-better-spaced-repetition-learning-algorithm-sm2

需要考虑的问题是：用户没有按时复习改如何改变？
策略是数据库存储的日期，如果在今天之前，全部都要进行复习
*/

package com.test.algorithm;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.test.model.entity.ReviewItem;
import com.test.util.SQLdm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SuperMemo {

    private Context context;

    /**
     * 传入的context是ApplicationContext
     * @param context
     */
    public SuperMemo(Context context) {
        this.context = context;
    }

    /**
     * 查数据库获取要复习的条目，在返回之前，确保已经将Json读取到SharePreferences
     * sql语句：select * from words where nextDate <= date('now');
     * @return 要复习的条目
     */
    public List<ReviewItem> getReviewItems() {
        SQLiteDatabase database = new SQLdm().openDataBase(context);
        String sql = "select * from words where nextDate <= date('now')";
        Cursor cursor = database.rawQuery(sql, null);
        List<ReviewItem> items = new ArrayList<>();
        while (cursor.moveToNext()) {
            String traditional = cursor.getString(cursor.getColumnIndex("traditional"));
            int repeatTimes = cursor.getInt(cursor.getColumnIndex("repeatTimes"));
            float eFactor = cursor.getFloat(cursor.getColumnIndex("eFactor"));

            sql = "select * from words where traditional = \"" + traditional + "\"";
            Cursor wordCursor = database.rawQuery(sql, null);
            wordCursor.moveToFirst();
            String simplified = wordCursor.getString(wordCursor.getColumnIndex("simplified"));
            wordCursor.close();

            items.add(new ReviewItem(traditional, simplified, 4, repeatTimes, eFactor));
        }
        cursor.close();
        database.close();
        return items;
    }

    /**
     *  插入一条复习的记录
     * @param traditional
     */
    public static void addReviewItem(Context context, String traditional) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());

        SQLiteDatabase database = new SQLdm().openDataBase(context);
        try {
            String sql = "update words set " +
                    "eFactor = " + 2.5 + ", " +
                    "repeatTimes = " + 1 + ", " +
                    "nextDate = \"" + today + "\" " +
                    "where traditional = \"" + traditional + "\"";
            database.execSQL(sql);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            database.close();
        }
    }
}
