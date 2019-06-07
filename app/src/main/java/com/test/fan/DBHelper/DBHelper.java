package com.test.fan.DBHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
 *数据库管理类DBHelper
 */
public class DBHelper extends SQLiteOpenHelper {

    private Context myContext;

    public DBHelper(Context context) {
        super(context, "Hello_Fan2.db", null, 1);
        myContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        executeAssetsSQL(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    /*
     *读取数据库文件,执行数据库中的sql语句
     */
    public void executeAssetsSQL(SQLiteDatabase db){
        BufferedReader in=null;
        try{
            //打开文件
            in=new BufferedReader(
                    new InputStreamReader(myContext.getResources().getAssets().open("word.sql"))
            );
            String expression;
            String buffer="";
            //逐条读取文件中的sql语句
            while((expression=in.readLine())!=null){
                if(!expression.trim().startsWith("--"))
                    buffer+=expression;
                //如果末尾是分号则说明是一条完整的SQL语句,执行
                if(expression.trim().endsWith(");")){
                    db.execSQL(buffer.replace(";",""));
                    buffer="";
                }
            }
        }catch(IOException e){
            Log.i("sql","失败");
        }finally {
            //关闭文件
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                Log.e("db-error", "出错");
            }
        }

    }

}
