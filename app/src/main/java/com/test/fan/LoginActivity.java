package com.test.fan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.test.model.entity.User;
import com.test.util.OkHttpRequest;

import java.io.IOException;

import static com.test.util.ActivityCollectorUtil.finishAllActivity;
import static com.test.util.Constant.SEVER_PORT;
import static com.test.util.Constant.SERVER_URL;

public class LoginActivity extends AppCompatActivity {
    private Handler handler;
    private String userName, password;//获取的用户名，密码
    private EditText et_user_name, et_psw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //设置此界面为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
    }

    @Override
    public void onBackPressed()
    {
        SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        if(!sp.getBoolean("loginStatus",false))
        {
            finishAllActivity();
        }
        this.finish();
    }
    private void init() {
        TextView tv_register = (TextView) findViewById(R.id.tv_register);
        TextView tv_find_psw = (TextView) findViewById(R.id.tv_find_psw);
        Button btn_login = (Button) findViewById(R.id.btn_login);
        et_user_name = (EditText) findViewById(R.id.et_user_name);
        et_psw = (EditText) findViewById(R.id.et_psw);
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        tv_find_psw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, FindLostActivity.class));
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = et_user_name.getText().toString().trim();
                password = et_psw.getText().toString().trim();
                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                } else {
                    verifyUser(userName, password);
                    handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.obj == null) return;
                            if (msg.obj.equals(userName)) {
                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                //在本地保存登录信息
                                SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putBoolean("loginStatus", true);
                                editor.putString("userName", userName);
                                editor.apply();
                                Intent intent = new Intent();
                                setResult(1);
                                LoginActivity.this.finish();
                            } else if (msg.obj.equals("false")) {
                                Toast.makeText(LoginActivity.this, "用户名或者密码有误", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "请确认网络是否已连接", Toast.LENGTH_SHORT).show();
                            }
                        }
                    };

                }
            }
        });
    }

    /**
     * 用户名和密码验证
     */
    private void verifyUser(final String userName, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                User user = new User();
                user.setUserName(userName);
                user.setPassword(password);
                String json = JSON.toJSONString(user);
                String requestUrl = SERVER_URL + ":" + SEVER_PORT + "/user/login";
                try {
                    message.obj = OkHttpRequest.post(requestUrl, json);
                } catch (IOException e) {
                    message.obj = "exception";
                }
                handler.sendMessage(message);
            }
        }).start();

    }


    /**
     * 注册成功的数据返回至此
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        数据
     */
    @Override
    //显示数据， onActivityResult
    //startActivityForResult(intent, 1); 从注册界面中获取数据
    //int requestCode , int resultCode , Intent data
    // LoginActivity -> startActivityForResult -> onActivityResult();
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            //是获取注册界面回传过来的用户名
            // getExtra().getString("***");
            String userName = data.getStringExtra("userName");
            if (!TextUtils.isEmpty(userName)) {
                //设置用户名到 et_user_name 控件
                et_user_name.setText(userName);
                //et_user_name控件的setSelection()方法来设置光标位置
                et_user_name.setSelection(userName.length());
            }
        }
    }
}
