package com.test.fan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class HomeFragment extends Fragment {

    private TextView textViewSlogan;
    private Button buttonStart;

    private String words;

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        textViewSlogan = view.findViewById(R.id.textViewSlogan);
        buttonStart = view.findViewById(R.id.buttonStart);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LearnWritingActivity.class);
                startActivity(intent);
            }
        });

        // 在这里获取数据或许不太好。因为如果查询太慢的话，会阻塞UI线程。
        words = getWords();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("fan_data", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("words", words);
        editor.commit();
        return view;
    }

    // 查询数据库，返回今天要练习的汉字。
    // 偶数位是繁体字,奇数位是简体字。下标从零开始。
    private String getWords() {
        // 这里是返回的数据的样子。仅作为例子。
        return "飛飞電电雞鸡醫医鄧邓鄭郑貓猫親亲蘭兰藥药華华腳脚聲声聖圣羅罗竊窃稱称盡尽癢痒";
    }
}
