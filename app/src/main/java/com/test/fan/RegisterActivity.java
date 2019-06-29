package com.test.fan;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.test.model.User;
import com.test.util.OkHttpRequest;
import com.test.util.StringUtil;

import java.io.IOException;
import java.util.regex.Pattern;

import static com.test.util.Constant.PHONE_REGEX;
import static com.test.util.Constant.PORT;
import static com.test.util.Constant.SERVER_URL;

public class RegisterActivity extends AppCompatActivity {
    private Handler handler;
    private Button btn_send_code;
    //用户名，密码，再次输入的密码的控件
    private EditText et_user_name, et_psw, et_psw_again, et_phone_num, et_verify_code;
    //用户名，密码，再次输入的密码的控件的获取值
    private String userName, password, pswAgain, code;
    private RadioGroup Sex;
    private String phoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //设置此界面为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
    }

    private void init() {
        boolean hasUser = true;
        Button btn_register = (Button) findViewById(R.id.btn_register);
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
                    genVerifyCode(phoneNum);
                    handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (!msg.obj.equals("true")) {
                                Toast.makeText(RegisterActivity.this, "发送验证码失败", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "发送验证码成功", Toast.LENGTH_LONG).show();
                            }
                        }
                    };
                } else
                    Toast.makeText(RegisterActivity.this, "手机号码输入有误", Toast.LENGTH_LONG).show();
            }
        });
        //注册按钮
        btn_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //获取输入在相应控件中的字符串
                userName = et_user_name.getText().toString().trim();
                password = et_psw.getText().toString().trim();
                pswAgain = et_psw_again.getText().toString().trim();
                phoneNum = et_phone_num.getText().toString().trim();
                code = et_verify_code.getText().toString().trim();
                //判断输入框内容
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
                    //验证用户名和用户手机号是否已经存在，还要判断验证码是否有误
                    verifyIsExist(userName, phoneNum, code);
                    handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.obj.equals("userName")) {
                                Toast.makeText(RegisterActivity.this, "用户名已存在", Toast.LENGTH_LONG).show();
                            } else if (msg.obj.equals("phoneNum")) {
                                Toast.makeText(RegisterActivity.this, "手机号已存在", Toast.LENGTH_LONG).show();
                            } else if (msg.obj.equals("code")) {
                                Toast.makeText(RegisterActivity.this, "验证码有误", Toast.LENGTH_LONG).show();
                            } else if (msg.obj.equals("true")) {
                                handleUserInfo();
                            } else if (msg.obj.equals("false")) {
                                Toast.makeText(RegisterActivity.this, "服务器错误", Toast.LENGTH_LONG).show();
                            }
                        }
                    };
                }
            }
        });
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
                String requestUrl = SERVER_URL + ":" + PORT + "/user/genCode?phoneNum=" + phoneNum;
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
    private void verifyIsExist(final String userName, final String phoneNum, final String code) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                String requestUrl = SERVER_URL + ":" + PORT + "/user/isNameRepeat?userName=" + userName + "&phoneNum=" + phoneNum + "&code=" + code;
                try {
                    message.obj = OkHttpRequest.get(requestUrl);
                } catch (IOException e) {
                    message.obj = "false";
                }
                handler.sendMessage(message);
            }
        }).start();

    }

    /**
     * 处理用户信息
     */
    public void handleUserInfo() {
        saveUserInfo();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (!msg.obj.equals("true")) {
                    Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    Intent data = new Intent();
                    data.putExtra("userName", userName);
                    setResult(RESULT_OK, data);
                    //RESULT_OK为Activity系统常量，状态码为-1，
                    // 表示此页面下的内容操作成功将data返回到上一页面，如果是用back返回过去的则不存在用setResult传递data值
                    RegisterActivity.this.finish();
                }
            }
        };
    }

    /**
     * 保存账号和密码到服务器
     */
    private void saveUserInfo() {
        final Message message = new Message();
        new Thread(new Runnable() {
            @Override
            public void run() {
                User user = new User();
                user.setPassword(password);
                user.setUserName(userName);
                user.setPhoneNum(phoneNum);
                String json = JSON.toJSONString(user);
                System.out.println(json);
                String requestUrl = SERVER_URL + ":" + PORT + "/user/register";
                try {
                    message.obj = OkHttpRequest.post(requestUrl, json);
                    System.out.println(message.obj.toString());
                } catch (IOException e) {
                    message.obj = "false";
                }
                handler.sendMessage(message);
            }
        }).start();

    }
}
