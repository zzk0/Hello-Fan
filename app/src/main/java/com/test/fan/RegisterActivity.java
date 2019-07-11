package com.test.fan;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.test.model.dto.UserDTO;
import com.test.model.entity.User;
import com.test.util.OkHttpRequest;
import com.test.util.StringUtil;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import static com.test.util.Constant.PHONE_REGEX;
import static com.test.util.Constant.SEVER_PORT;
import static com.test.util.Constant.SERVER_URL;

public class RegisterActivity extends AppCompatActivity {
    private Handler handler;
    private Button btn_send_code;
    private EditText et_user_name, et_psw, et_psw_again, et_phone_num, et_verify_code;
    private String userName, password, pswAgain, code;
    private String phoneNum;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //设置此界面为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    /**
     * 初始化
     */
    private void init() {
        boolean hasUser = true;
        final Button btn_register = (Button) findViewById(R.id.btn_register);
        et_user_name = (EditText) findViewById(R.id.et_user_name);
        et_psw = (EditText) findViewById(R.id.et_psw);
        et_psw_again = (EditText) findViewById(R.id.et_psw_again);
        et_phone_num = (EditText) findViewById(R.id.et_phone_num);
        et_verify_code = (EditText) findViewById(R.id.et_verify_code);
        btn_send_code = (Button) findViewById(R.id.btn_send_code);
        btn_send_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNum = et_phone_num.getText().toString().trim();
                if (!StringUtil.isEmpty(phoneNum) && Pattern.matches(PHONE_REGEX, phoneNum)) {
                    handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (!msg.obj.equals("true")) {
                                if(msg.obj.equals("phoneNumRepeat"))
                                {
                                    Toast.makeText(RegisterActivity.this, "该手机号已经存在", Toast.LENGTH_LONG).show();
                                }
                                else
                                Toast.makeText(RegisterActivity.this, "服务器错误,发送验证码失败", Toast.LENGTH_LONG).show();
                            } else {
                                startTimer();
                                Toast.makeText(RegisterActivity.this, "发送验证码成功", Toast.LENGTH_LONG).show();
                            }
                        }
                    };
                    genVerifyCode(phoneNum);
                } else
                    Toast.makeText(RegisterActivity.this, "手机号码输入有误", Toast.LENGTH_LONG).show();
            }
        });
        //注册按钮
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = et_user_name.getText().toString().trim();
                password = et_psw.getText().toString().trim();
                pswAgain = et_psw_again.getText().toString().trim();
                phoneNum = et_phone_num.getText().toString().trim();
                code = et_verify_code.getText().toString().trim();
                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(RegisterActivity.this, "请输入用户名", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "请输入密码", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(pswAgain)) {
                    Toast.makeText(RegisterActivity.this, "请再次输入密码", Toast.LENGTH_LONG).show();
                } else if (!password.equals(pswAgain)) {
                    Toast.makeText(RegisterActivity.this, "输入两次的密码不一样", Toast.LENGTH_LONG).show();
                } else if (!Pattern.matches(PHONE_REGEX, phoneNum)) {
                    Toast.makeText(RegisterActivity.this, "手机号码输入错误", Toast.LENGTH_LONG).show();
                } else {
                    //验证用户名和用户手机号是否已经存在
                    User user =new User();
                    UserDTO userDTO=new UserDTO();
                    user.setPassword(password);
                    user.setPhoneNum(phoneNum);
                    user.setUserName(userName);
                    userDTO.setUser(user);
                    userDTO.setCode(code);
                    saveUserInfo(userDTO);
                    handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if(msg.obj==null) return;
                            System.out.println(msg.obj.toString()+222222);
                            if (msg.obj.toString().equals("userNameRepeat")) {
                                Toast.makeText(RegisterActivity.this, "该用户名已存在", Toast.LENGTH_LONG).show();
                            }  else if (msg.obj.toString().equals("codeError")) {
                                Toast.makeText(RegisterActivity.this, "验证码有误", Toast.LENGTH_LONG).show();
                            } else if (msg.obj.toString().equals("true")) {
                                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.putExtra("userName", userName);
                                setResult(RESULT_OK, intent);
                                //RESULT_OK为Activity系统常量，状态码为-1，
                                // 表示此页面下的内容操作成功将data返回到上一页面，如果是用back返回过去的则不存在用setResult传递data值
                                RegisterActivity.this.finish();

                            } else {
                                Toast.makeText(RegisterActivity.this, "服务器错误,注册失败", Toast.LENGTH_LONG).show();
                            }
                        }
                    };

                }
            }
        });
    }

    private void startTimer() {
        btn_send_code.setClickable(false);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    btn_send_code.setClickable(true);
                    btn_send_code.setText("重新发送");
                } else {
                    btn_send_code.setText("重新发送("+msg.what + "s)");
                }
            }
        };
        //设置计时120s
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            int time = 60;

            @Override
            public void run() {
                Message message = new Message();
                if (time == 0)
                    cancel();
                message.what = time--;
                handler.sendMessage(message);
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    /**
     * 从后台获取验证码
     */
    private void genVerifyCode(final String phoneNum) {
        System.out.println("发送验证码");
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("手机号为" + phoneNum);
                String requestUrl = SERVER_URL + ":" + SEVER_PORT + "/user/genCode?phoneNum=" + phoneNum+"&flag=register";
                System.out.println("请求Url" + requestUrl);
                Message message = new Message();
                try {
                    message.obj = OkHttpRequest.get(requestUrl);
                    System.out.println(message.obj.toString() + 222);
                } catch (IOException e) {
                    message.obj = "false";
                    System.out.println(message.obj.toString() + 333);
                }
                handler.sendMessage(message);
            }
        }).start();
    }


    /**
     * 从后台中验证用户输入信息是否有误
     */
    private void saveUserInfo(final UserDTO userDTO) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                String requestUrl = SERVER_URL + ":" + SEVER_PORT + "/user/register";
                String json=JSON.toJSONString(userDTO);
                try {
                    message.obj = OkHttpRequest.post(requestUrl,json);
                } catch (IOException e) {
                    message.obj = "false";
                }
                handler.sendMessage(message);
            }
        }).start();

    }

}
