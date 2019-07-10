package com.test.util;

import android.content.Context;

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
                List<StudyPlan> plans = new ArrayList<>();
                for (int i = 0; i < 1; i++) {
                    StudyPlan plan = new StudyPlan();
                    plan.setTradictional("你");
                    plan.setLearnTimes(new Date());
                    plan.setUserName("zzk");
                    plan.setNextDate(new Date());
                    plan.setEfactor(2.333);
                    plan.setUpdateTime(new Date());
                    plan.setRepeatTimes(3);
                    plan.setLearnDate(new SimpleDateFormat("YYYY-mm-dd").format(new Date()));
                    plan.setValue("");
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
