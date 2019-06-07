/*
说明：一简对多繁Fragment
*/
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

import static android.content.Context.MODE_PRIVATE;


public class S2TFragment extends Fragment  {


    private static Button jum_bt;
    private int flag=0;

    public S2TFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_s2t, null);


        jum_bt=(Button)view.findViewById(R.id.jumps2t);

        jum_bt.setOnClickListener(new LocationCheckedListener());

        return view;
    }

    class LocationCheckedListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {


            Intent intent = new Intent();
            intent.setClass(getActivity(), LearnS2TActivity.class);
            //从前者跳到后者，特别注意的是，在fragment中，用getActivity()来获取当前的activity
            getActivity().startActivity(intent);


        }
    }
}
