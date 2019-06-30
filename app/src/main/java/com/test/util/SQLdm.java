package com.test.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

public class SQLdm {

    String DB_NAME="Hello_Fan2.db";//数据库名
    String PACKAGE_NAME="com.test.fan";//应用包名
    String path= "/data"+Environment.getDataDirectory().getAbsolutePath()
            +"/"+PACKAGE_NAME+"/databases";
    String File_Path=path+"/"+DB_NAME;

    public SQLiteDatabase openDataBase(Context context)
    {
        File file=new File(File_Path);
        if (file.exists())
        {
            return SQLiteDatabase.openOrCreateDatabase(file, null);
        }
        else {
            File newfile=new File(path);
            if (newfile.mkdir())
            {
                Log.i("test","成功");
            }
            else {
                Log.i("test","失败");
            }
            try {
                //获取资源
                AssetManager am=context.getAssets();
                //得到数据库的输入流
                InputStream in=am.open("Hello_Fan2.db");
                //用输出流写到SDcard上
                FileOutputStream fileOutputStream=new FileOutputStream(File_Path);

                byte[] buffer=new byte[1024];
                int count=0;
                while ((count=in.read(buffer))>0)
                {
                    fileOutputStream.write(buffer,0,count);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                in.close();
            }catch (IOException e){
                e.printStackTrace();
                return null;
            }
            return openDataBase(context);
        }

    }
}
