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

    SQLiteDatabase sqLiteDatabase;

    //显示日期的textview
    TextView textView_date;
    ListView ListView;

    /**
     * @param context  context
     * @param date 一个储存所有非null日期的list,之后listview的每一个item根据对应的list.get(i)获取这个日期下所有学习纪录
     */
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
        //先显示日期，下面的listview再显示详细当天学习纪录
        textView_date.setText(date.get(position));

        sqLiteDatabase=new SQLdm().openDataBase(context);

        Cursor cursor=sqLiteDatabase.rawQuery("select * from words where learndate= ?",new String[]{date.get(position)});
        List<WordBean> list=new ArrayList<>();
        //搜索结果对象化为WordBean，存入list
        while (cursor.moveToNext()) {
            WordBean dictBean=new WordBean(
                    cursor.getString(cursor.getColumnIndex("simplified")),
                    cursor.getString(cursor.getColumnIndex("traditional")));
            list.add(dictBean);
        }

        HistoryItemAdapter historyItemAdapter=new HistoryItemAdapter(context,list);

        ListView=(ListView)view.findViewById(R.id.lv_today_study);
        ListView.setAdapter(historyItemAdapter);

        return view;
    }
}
