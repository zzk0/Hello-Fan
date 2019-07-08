package com.test.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.test.Bean.WordBean;
import com.test.fan.LearnOneWordActivity;
import com.test.fan.R;
import com.test.fan.SearchActivity;

import java.util.List;
/*
* 这个Adapter打杂的，HistoryAdapter把某个日期下所有学习纪录传到这个Adapter的list里，然后显示出来就行了
* */
public class HistoryItemAdapter extends BaseAdapter {

    Context context;
    List<WordBean> list;

    boolean isShelterNow=true;

    public HistoryItemAdapter(Context context, List<WordBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view=View.inflate(context, R.layout.item_history_date,null);
        //下面一堆负责显示
        TextView textView=(TextView)view.findViewById(R.id.tv_tradiction);
        textView.setText(list.get(position).getTraditional());
        TextView textView2=(TextView)view.findViewById(R.id.tv_sim);
        textView2.setText(list.get(position).getSimplified());

        final TextView textView3=(TextView)view.findViewById(R.id.tv_shelter);
        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShelterNow) {
                    textView3.setBackgroundColor(Color.argb(00, 0, 0, 0));
                    isShelterNow=false;
                }
                else {
                    isShelterNow=true;
                    textView3.setBackgroundColor(Color.GRAY);
                }

            }
        });
        //显示完了
        Button button=(Button)view.findViewById(R.id.button_study);
        //学习按钮的点击事件，点击跳转，
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LearnOneWordActivity.class);
                intent.putExtra("phrase", list.get(position).getTraditional());
                context.startActivity(intent);
            }
        });

        return view;
    }
}
