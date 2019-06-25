package com.test.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.test.Bean.WordBean;
import com.test.fan.R;
import com.test.util.SQLdm;

import java.util.ArrayList;
import java.util.List;
/*
* HistoryAdapter  负责显示某个日期下的一团
*
* */
public class HistoryAdapter extends BaseAdapter {

    Context context;

    List<String> date;

    SQLdm sqLdm;
    SQLiteDatabase sqLiteDatabase;

    //显示日期的textview
    TextView textView_date;
    ListView listView;

    public HistoryAdapter(Context context, List<String> date) {
        this.context = context;
        this.date=date;
    }

    @Override
    public int getCount() {
        return date.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=View.inflate(context, R.layout.item_history,null);
        textView_date=(TextView)view.findViewById(R.id.tv_date);
        textView_date.setText(date.get(position));

        sqLdm=new SQLdm();
        sqLiteDatabase=sqLdm.openDataBase(context);

        Cursor cursor=sqLiteDatabase.rawQuery("select * from bank where 学习日期= ?",new String[]{date.get(position)});
        List<WordBean> list=new ArrayList<>();
        //搜索结果对象化为WordBean，存入list
        while (cursor.moveToNext()) {
            WordBean dictBean=new WordBean(
                    cursor.getString(cursor.getColumnIndex("simplified")),
                    cursor.getString(cursor.getColumnIndex("traditional")));
            list.add(dictBean);
        }

        HistoryItemAdapter historyItemAdapter=new HistoryItemAdapter(context,list);

        listView=(ListView)view.findViewById(R.id.lv_today_study);
        listView.setAdapter(historyItemAdapter);

        return view;
    }
}
