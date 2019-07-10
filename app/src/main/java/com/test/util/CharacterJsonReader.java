/*
使用单例设计模式，全局只要一个Reader

给它一个汉字，它就根据汉字去查询这个汉字的笔画信息的Json
*/

package com.test.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CharacterJsonReader {
    private CharacterJsonReader() { }

    public static String query(Context context, String word)
    {
        SQLiteDatabase database = new SQLdm().openDataBase(context);
        String sql = "select * from wordsJson where word = \"" + word + "\"";
        Cursor cursor = database.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            String result = cursor.getString(cursor.getColumnIndex("json"));
            cursor.close();
            database.close();
            return result;
        }
        return "";
    }
}
