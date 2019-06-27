package com.test.fan;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.test.Bean.DictBean;
import com.test.Bean.WordBean;
import com.test.adapter.HistoryAdapter;
import com.test.adapter.HistoryItemAdapter;
import com.test.util.SQLdm;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private ListView listView;

    SQLdm sqldm;
    SQLiteDatabase sqLiteDatabase;

    HistoryAdapter historyAdapter;

    public HistoryFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, null);
        listView=(ListView)view.findViewById(R.id.lv_all);

        sqldm=new SQLdm();
        sqLiteDatabase=sqldm.openDataBase(getActivity());
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT traditional, learnDate FROM words",null);
        List<String> date=new ArrayList<>();
        //搜索所有日期存入list中
        while (cursor.moveToNext()) {
            String temp=cursor.getString(cursor.getColumnIndex("learnDate"));
            String traditional = cursor.getString(cursor.getColumnIndex("traditional"));
            if(temp!=null &&temp.length()!=0 && !temp.equals("") && !temp.equals("null")) {
                date.add(traditional);
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
