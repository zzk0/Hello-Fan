package com.test.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alibaba.fastjson.JSON;
import com.test.model.dto.StudyPlan;

import java.io.IOException;
import java.text.DateFormat;
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
                SQLiteDatabase database = new SQLdm().openDataBase(context);
                String sql = "select * from words where lastTime <= updateTime " +
                        "or lastTime is not null and updateTime is null";
                Cursor cursor = database.rawQuery(sql, null);
                List<StudyPlan> plans = new ArrayList<>();
                while (cursor.moveToNext()) {
                    StudyPlan plan = new StudyPlan();
                    plan.setTradictional(cursor.getString(cursor.getColumnIndex("traditional")));
//                    plan.setLearnTimes(cursor.getString(cursor.getColumnIndex("traditional")));
                    plan.setUserName(cursor.getString(cursor.getColumnIndex("traditional")));
//                    plan.setNextDate(cursor.getString(cursor.getColumnIndex("traditional")));
                    plan.setEfactor(cursor.getDouble(cursor.getColumnIndex("traditional")));
//                    plan.setUpdateTime(cursor.getString(cursor.getColumnIndex("traditional")));
                    plan.setRepeatTimes(cursor.getInt(cursor.getColumnIndex("traditional")));
                    plan.setLearnDate(cursor.getString(cursor.getColumnIndex("traditional")));
                    plans.add(plan);
                }

                String json = JSON.toJSONString(plans);
                String requestUrl = SERVER_URL + ":" + SEVER_PORT + "/studyPlan/updateAllPlan";
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
