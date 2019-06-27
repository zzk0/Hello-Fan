package com.test.fan;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.test.util.CharacterJsonReader;
import com.test.util.SQLdm;
import com.test.view.SignLinesView;
import com.test.view.SignView;


public class HomeFragment extends Fragment {

    //private TextView textViewSlogan;
    private Button buttonStart;
    SignView mSignView;
    SignLinesView mSignLinesView;
    private String words;

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);

        buttonStart = view.findViewById(R.id.buttonStart);

        mSignView = (SignView)view.findViewById(R.id.signView);
        mSignLinesView=(SignLinesView)view.findViewById(R.id.signLineView);

        //根据数据库中日期进行签到标签的修改
        SQLiteDatabase sqLiteDatabase = new SQLdm().openDataBase(getContext());
        String table = "words";
        Cursor cursor=sqLiteDatabase.query(true,table,new String[]{"learnDate"},"learnDate is not null",null,null,null,null,null);
        mSignLinesView.setSignDays(cursor.getCount());
        //Toast.makeText(getContext(),"提示:"+cursor.getCount(),Toast.LENGTH_LONG).show();

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LearnWritingActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
