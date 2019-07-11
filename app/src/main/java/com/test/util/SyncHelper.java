package com.test.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.test.model.dto.StudyPlan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.test.util.Constant.SERVER_URL;
import static com.test.util.Constant.SEVER_PORT;

public class SyncHelper {

    private Context context;

    public SyncHelper(Context context) {
        this.context = context;
    }

    /**
     *     private int id;
     *     private String tradictional;
     *     private Date learnTimes;
     *     private String value;
     *     private double efactor;
     *     private Date nextDate;
     *     private Date updateTime;
     *     private int repeatTimes;
     *     private String learnDate;
     * @return 同步成功或失败
     */
    public boolean sync() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 上传SharedPreferences
                SharedPreferences sharedPreferences = context.getSharedPreferences("fan_data", 0);
                SimpleDateFormat sdf = new SimpleDateFormat("YYYY-mm-dd hh:mm:ss");
                String updateTime = sdf.format(new Date());
                sharedPreferences.edit().putString("updateTime", updateTime).apply();

                String lastLearnDate = sharedPreferences.getString("last_learn_date", "");
                int currentWord = sharedPreferences.getInt("current_word", 0);
                String todayWords = sharedPreferences.getString("today_words", "");
                int wordsPerDay = sharedPreferences.getInt("wordsPerDay", 0);

                SharedPreferences sp1 = context.getSharedPreferences("loginInfo", 0);
                String username = sp1.getString("userName", "");

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userName", username);
                jsonObject.put("lastLearnDate", lastLearnDate);
                jsonObject.put("currentWord", currentWord);
                jsonObject.put("todayWords", todayWords);
                jsonObject.put("wordsPerday", wordsPerDay);

                String requestUrl = SERVER_URL + ":" + SEVER_PORT + "/user/updateSharedPreferences";
                try {
                    String result = OkHttpRequest.post(requestUrl, jsonObject.toJSONString());
                    System.out.println(result);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                SQLiteDatabase database = new SQLdm().openDataBase(context);
                String sql = "select * from words where lastTime > updateTime " +
                        "or (lastTime is not null and updateTime is null)";
                Cursor cursor = database.rawQuery(sql, null);
                List<StudyPlan> plans = new ArrayList<>();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                while (cursor.moveToNext()) {
                    try {
                        StudyPlan plan = new StudyPlan();
                        plan.setTradictional(cursor.getString(cursor.getColumnIndex("traditional")));
                        plan.setLearnTimes(cursor.getInt(cursor.getColumnIndex("learnTimes")));
                        plan.setUserName(username);
                        String nextDate = cursor.getString(cursor.getColumnIndex("nextDate"));
                        if (nextDate != null) {
                            plan.setNextDate(format.parse(nextDate));
                        }
                        plan.setEfactor(cursor.getDouble(cursor.getColumnIndex("eFactor")));
                        plan.setUpdateTime(new Date());
                        plan.setRepeatTimes(cursor.getInt(cursor.getColumnIndex("repeatTimes")));
                        plan.setLearnDate(cursor.getString(cursor.getColumnIndex("learnDate")));
                        plans.add(plan);

                        sql = "update words set " +
                            "updateTime = \"" + new Date() + "\" " +
                            "where traditional = \"" + cursor.getString(cursor.getColumnIndex("traditional")) + "\"";
                        database.execSQL(sql);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // 上传数据库
                String json = JSON.toJSONString(plans);
                requestUrl = SERVER_URL + ":" + SEVER_PORT + "/studyPlan/updateAllPlan";
                try {
                    Object result = OkHttpRequest.post(requestUrl, json);
                    System.out.println("");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return true;
    }
}
