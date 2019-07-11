package com.test.fan;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.roger.catloadinglibrary.CatLoadingView;
import com.test.model.dto.StudyPlan;
import com.test.util.OkHttpRequest;
import com.test.util.SQLdm;
import com.test.view.SignLinesView;
import com.test.view.SignView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.test.util.Constant.SERVER_URL;
import static com.test.util.Constant.SEVER_PORT;


public class HomeFragment extends Fragment {

    //private TextView textViewSlogan;
    private Button buttonStart;
    private Button buttonS2TStart;
    SignView mSignView;
    SignLinesView mSignLinesView;
    private String words;

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);

        buttonStart = view.findViewById(R.id.buttonStart);
        buttonS2TStart = view.findViewById(R.id.buttonS2TStart);

        mSignView = (SignView)view.findViewById(R.id.signView);
        mSignLinesView=(SignLinesView)view.findViewById(R.id.signLineView);

        // LoadingView
        final CatLoadingView catLoadingView = new CatLoadingView();
        catLoadingView.show(getActivity().getSupportFragmentManager(), "");
        catLoadingView.setCancelable(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                //根据数据库中日期进行签到标签的修改
                SQLiteDatabase sqLiteDatabase = new SQLdm().openDataBase(getContext());
                String table = "words";
                Cursor cursor=sqLiteDatabase.query(true,table,new String[]{"learnDate"},"learnDate is not null",null,null,null,null,null);
                mSignLinesView.setSignDays(cursor.getCount());
                //Toast.makeText(getContext(),"提示:"+cursor.getCount(),Toast.LENGTH_LONG).show();
                pullData();
                catLoadingView.setCancelable(true);
                catLoadingView.dismiss();
            }
        }).start();

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LearnWritingActivity.class);
                startActivity(intent);
            }
        });
        buttonS2TStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LearnS2TActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void pullData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("loginInfo", 0);
                String username = sharedPreferences.getString("userName", "");
                String requestUrl = SERVER_URL + ":" + SEVER_PORT + "/studyPlan/getAllPlan?userName=" + username;
                try {
                    String json = OkHttpRequest.get(requestUrl);
                    JSONArray array = JSON.parseArray(json);
                    SQLiteDatabase database = new SQLdm().openDataBase(getActivity());
                    if (array != null) {
                        for (int i = 0; i < array.size(); i++) {
                            StudyPlan plan = array.getObject(i, StudyPlan.class);
                            String sql = "update words set " +
                                    "learnTimes = " + plan.getLearnTimes() + ", " +
                                    "learnDate = \"" + plan.getLearnDate() + "\", " +
                                    "repeatTimes = " + plan.getRepeatTimes() + ", " +
                                    "eFactor = " + plan.getEfactor() + ", " +
                                    "nextDate = \"" + plan.getNextDate() + "\" " +
                                    "where traditional = \"" + plan.getTradictional() + "\"";
                            database.execSQL(sql);
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                requestUrl = SERVER_URL + ":" + SEVER_PORT + "/user/getSharedPreferences?userName=" + username;
                try {
                    String json = OkHttpRequest.get(requestUrl);
                    JSONObject obj = JSONObject.parseObject(json);
                    int currentWord = obj.getInteger("currentWord");
                    int wordsPerDay = obj.getInteger("wordsPerday");
                    String lastLearnDate = obj.getString("lastLearnDate");
                    String todayWords = obj.getString("todayWords");
                    SharedPreferences fanData = getActivity().getSharedPreferences("fan_data", 0);
                    SharedPreferences.Editor editor = fanData.edit();
                    editor.putInt("current_word", currentWord);
                    if (wordsPerDay != 0) {
                        editor.putInt("wordsPerDay", wordsPerDay);
                    }
                    else {
                        editor.putInt("wordsPerDay", 20);
                    }
                    editor.putString("last_learn_date", lastLearnDate);
                    editor.putString("today_words", todayWords);
                    editor.apply();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
