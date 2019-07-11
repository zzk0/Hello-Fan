package com.test.fan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.test.model.dto.UserInfo;
import com.test.util.ACache;
import com.test.util.OkHttpRequest;

import java.io.IOException;

import static com.test.util.Constant.SERVER_URL;
import static com.test.util.Constant.SEVER_PORT;

public class EditUserInfoActivity extends AppCompatActivity {
    private Handler handler;
    private TextView et_nickname, et_sex, et_school, et_brief;
    private Button btn_save;
    private ACache aCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_userinfo);
        init();
    }

    private void init() {
        et_nickname = (EditText) findViewById(R.id.et_nickname);
        et_sex = (EditText) findViewById(R.id.et_sex);
        et_school = (EditText) findViewById(R.id.et_school);
        et_brief = (EditText) findViewById(R.id.et_brief);
        btn_save = (Button) findViewById(R.id.btn_save);
        aCache=ACache.get(this);
        btn_save.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!(et_sex.getText().toString().equals("")||
                                et_sex.getText().toString().equals("男")||
                                et_sex.getText().toString().equals("女")))
                        {
                            Toast.makeText(EditUserInfoActivity.this, "性别输入错误", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            final UserInfo userInfo = new UserInfo();
                            SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
                            String userName = sp.getString("userName", "");
                            userInfo.setUserName(userName);
                            userInfo.setNickName(et_nickname.getText().toString());
                            userInfo.setSex(et_sex.getText().toString());
                            userInfo.setSchool(et_school.getText().toString());
                            userInfo.setBrief(et_brief.getText().toString());
                            handler = new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if(msg.obj.equals("success"))
                                    {
                                        aCache.put("userInfo", userInfo);
                                        goBack();
                                    }
                                    else
                                    {
                                        Toast.makeText(EditUserInfoActivity.this, "保存失败", Toast.LENGTH_LONG).show();
                                    }
                                }
                            };
                            saveUserInfo(userInfo);

                        }
                    }
                }
        );
    }

    public void goBack()
    {
        this.finish();
    }
    private void saveUserInfo(final UserInfo userInfo)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message=new Message();
                String json = JSON.toJSONString(userInfo);
                String requestUrl = SERVER_URL + ":" + SEVER_PORT + "/user/saveUserInfo";
                try {
                    message.obj=OkHttpRequest.post(requestUrl, json);
                } catch (IOException e) {
                    e.printStackTrace();
                    message.obj="fail";
                }
                handler.sendMessage(message);
            }
        }).start();

    }
    @Override
    public void onBackPressed()
    {
        this.finish();
    }



}
