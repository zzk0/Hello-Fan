/*
使用单例设计模式，全局只要一个Reader

给它一个汉字，它就根据汉字去查询这个汉字的笔画信息的Json
*/

package com.test.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CharacterJsonReader {
    private CharacterJsonReader() { }

    public static String query(Context context, String word, boolean isTraditional)
    {
        SQLiteDatabase sqLiteDatabase = DBManage(context);
        String table = "words";
        Cursor cursor;
        if (isTraditional) {
            cursor = sqLiteDatabase.rawQuery("select * from " + table + " where traditional = '" + word + "'", null);
        }
        else {
            cursor = sqLiteDatabase.rawQuery("select * from " + table + " where simplified = '" + word + "'", null);
        }

        String result = "";
        if (cursor.moveToFirst()) {
            if (isTraditional) {
                result = cursor.getString(cursor.getColumnIndex("tradJson"));
            }
            else {
                result = cursor.getString(cursor.getColumnIndex("simpJson"));
            }
        }
        cursor.close();
        sqLiteDatabase.close();
        return result;
    }

    public static SQLiteDatabase DBManage(Context mContext)
    {
        return new SQLdm().openDataBase(mContext);
    }
}
