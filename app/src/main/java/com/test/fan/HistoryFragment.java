package com.test.fan;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.test.adapter.HistoryAdapter;
import com.test.util.SQLdm;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private ListView listView;

    SQLiteDatabase sqLiteDatabase;

    HistoryAdapter historyAdapter;

    public HistoryFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, null);
        listView=(ListView)view.findViewById(R.id.lv_all);


        sqLiteDatabase=new SQLdm().openDataBase(getActivity());
        //第一步先取所有日期，之后adapter才根据日期获得当日所有学习纪录(之前忘记加distinct了)
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT DISTINCT learnDate FROM words",null);
        List<String> date=new ArrayList<>();
        //搜索所有日期存入list中
        while (cursor.moveToNext()) {
            String temp=cursor.getString(cursor.getColumnIndex("learnDate"));
            //String traditional = cursor.getString(cursor.getColumnIndex("traditional"));
            if(temp!=null &&temp.length()!=0  && !temp.equals("null")) {
                date.add(temp);
                System.out.println("date:"+temp);
            }
        }
        cursor.close();
        sqLiteDatabase.close();

//        for(int i=1;i<100;i++){
//            list.add(""+i);
//        }

        historyAdapter=new HistoryAdapter(getActivity(), date);

        listView.setAdapter(historyAdapter);

        return view;
    }

}
