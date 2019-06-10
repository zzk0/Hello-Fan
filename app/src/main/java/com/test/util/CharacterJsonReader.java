/*
使用单例设计模式，全局只要一个Reader

给它一个汉字，它就根据汉字去查询这个汉字的笔画信息的Json
*/

package com.test.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CharacterJsonReader {

    private CharacterJsonReader() { }

    // 根据汉字去查询这个汉字的笔画信息Json
    /*public static String query(Context context, String word) {
        StringBuilder fileContent = new StringBuilder("");
        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(context.getAssets().open("graphics.txt"), "UTF-8");
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if(line.indexOf(word)!=-1)
                {
                    fileContent.append(line);
                    break;
                }
            }
            reader.close();
        } catch (Exception e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return fileContent.toString();
    }*/
    public static String[] columns={"流水序","教育部字號","Unicode","常用字","简体字","简体JSON","繁体JSON","学习次数"};
    private static String DB_NAME = "test.db";
    public static String query(Context context, String word)
    {
        StringBuilder fileContent = new StringBuilder("");
        SQLiteDatabase sqLiteDatabase = DBManage(context,"com.test");
        String table = "bank";
        Cursor cursor=sqLiteDatabase.query(table,columns,"常用字 like '"+""+word+"'",null,null,null,null);
        while(cursor.moveToNext()) {
            String result = cursor.getString(cursor.getColumnIndex("繁体JSON"));
            if(result.indexOf(word)!=-1)
            {
                fileContent.append(result);
                break;
            }
        }
        return fileContent.toString();
    }
    public static SQLiteDatabase DBManage(Context mContext,String packname) {
        String dbPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/databases/" + DB_NAME;
        if (!new File(dbPath).exists()) {
            try {
                boolean flag = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/databases/").mkdirs();
                boolean newFile = new File(dbPath).createNewFile();
                try {
                    FileOutputStream out = new FileOutputStream(dbPath);
                    InputStream in = mContext.getAssets().open("test.db");
                    byte[] buffer = new byte[1024];
                    int readBytes = 0;
                    while ((readBytes = in.read(buffer)) != -1)
                        out.write(buffer, 0, readBytes);
                    in.close();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return SQLiteDatabase.openOrCreateDatabase(dbPath, null);
    }
}
