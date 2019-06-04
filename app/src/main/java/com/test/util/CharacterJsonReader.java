/*
使用单例设计模式，全局只要一个Reader

给它一个汉字，它就根据汉字去查询这个汉字的笔画信息的Json
*/

package com.test.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CharacterJsonReader {

    private CharacterJsonReader() { }

    // 根据汉字去查询这个汉字的笔画信息Json
    public static String query(Context context, String word) {
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
    }
}
