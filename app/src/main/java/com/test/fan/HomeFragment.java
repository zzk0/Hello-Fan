package com.test.fan;

import android.content.ContentValues;
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

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.test.util.CharacterJsonReader;
import com.test.view.SignLinesView;
import com.test.view.SignView;

import org.threeten.bp.LocalDate;

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
        AndroidThreeTen.init(getActivity());

        //textViewSlogan = view.findViewById(R.id.textViewSlogan);
        buttonStart = view.findViewById(R.id.buttonStart);

        mSignView = (SignView)view.findViewById(R.id.signView);
        mSignLinesView=(SignLinesView)view.findViewById(R.id.signLineView);

        //根据数据库中日期进行签到标签的修改
        SQLiteDatabase sqLiteDatabase = CharacterJsonReader.DBManage(getContext());
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

        words = getWords();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("fan_data", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("words", words);
        editor.commit();

        return view;
    }

    private String getWords() {
        // 这里是返回的数据的样子
        // return "鄭郑0飛飞1電电2雞鸡2醫医2鄧邓1貓猫1親亲2蘭兰0藥药2華华2腳脚2聲声1聖圣0羅罗2竊窃1稱称2盡尽1癢痒2";
        //添加了读取数据时一并将学习日期更改的功能
        StringBuilder fileContent = new StringBuilder();
        SQLiteDatabase sqLiteDatabase = CharacterJsonReader.DBManage(getContext());
        String table = "words";
        Cursor cursor=sqLiteDatabase.query(table,new String[]{"ID","learnTimes","traditional","simplified","tradJson","simpJson","learnDate"},"learnTimes < ? ",new String[]{"3"},null,null,null,"20");
        ContentValues values=new ContentValues();
        LocalDate localDate=LocalDate.now();
        values.put("learnDate", localDate.toString());
        while(cursor.moveToNext()) {
            String content = cursor.getString(cursor.getColumnIndex("traditional")) + cursor.getString(cursor.getColumnIndex("simplified")) + cursor.getString(cursor.getColumnIndex("learnTimes"));
            if(cursor.getString(cursor.getColumnIndex("learnDate"))==null) {
                int flag = sqLiteDatabase.update(table, values, "traditional=?", new String[]{cursor.getString(cursor.getColumnIndex("traditional"))});
            }
            fileContent.append(content);
        }
        cursor.close();
        sqLiteDatabase.close();
        return fileContent.toString();
    }
}
