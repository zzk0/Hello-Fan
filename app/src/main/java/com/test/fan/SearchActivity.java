/*
说明：点击了搜索的按钮后，弹出来的activity
如果需要别的启动方式，比如需要返回数据之类的，请修改MainActivity中的onOptionsItemSelected函数
*/

package com.test.fan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }
}
