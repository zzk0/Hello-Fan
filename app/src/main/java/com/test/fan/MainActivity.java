package com.test.fan;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.android.material.navigation.NavigationView;
import com.roger.catloadinglibrary.CatLoadingView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.test.model.dto.StudyPlan;
import com.test.util.ActivityCollectorUtil;
import com.test.util.OkHttpRequest;
import com.test.util.SQLdm;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.test.util.Constant.SERVER_URL;
import static com.test.util.Constant.SEVER_PORT;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private List<Fragment> fragments;
    private int prePos;
    private long exitTime=0;
    // Constant
    private static final String[] TAGS = {"home", "reading", "history"};
    private static final String PRE = "PREPOS";
    private static final int HOME = 0;
    private static final int READING = 1;
    private static final int HISTORY = 2;
    private static final int READ_WRITE_PERM = 2333;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 设置ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragments = new ArrayList<>();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        ImageView iconImageView = headerView.findViewById(R.id.iconImageView);
        iconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                startActivity(intent);
            }
        });

        //判断是否已经登录过
        SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        boolean loginStatus = sp.getBoolean("loginStatus", false);
        if(!loginStatus)
        {
            goToLoginActivity();
        }

        if (!(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) ||
                !(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    READ_WRITE_PERM);
        }
        else {
            // Initialize the fragments
            if(savedInstanceState == null) {
                prePos = 0;
                fragments = new ArrayList<>();
                fragments.add(new HomeFragment());
                fragments.add(new ReadingFragment());
                fragments.add(new HistoryFragment());
            }
            else {
                prePos = savedInstanceState.getInt(PRE);
                fragments = new ArrayList<>();
                HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(TAGS[HOME]);
                ReadingFragment readingFragment = (ReadingFragment) getSupportFragmentManager().findFragmentByTag(TAGS[READING]);
                HistoryFragment historyFragment = (HistoryFragment) getSupportFragmentManager().findFragmentByTag(TAGS[HISTORY]);
                fragments.add(homeFragment);
                fragments.add(readingFragment);
                fragments.add(historyFragment);
            }
            setDefaultFragment(prePos);
        }
        updateDrawerInfo();
        ActivityCollectorUtil.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollectorUtil.removeActivity(this);
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final CatLoadingView catLoadingView = new CatLoadingView();
        catLoadingView.show(getSupportFragmentManager(), "");
        catLoadingView.setCancelable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                pullData();
                catLoadingView.setCancelable(true);
                catLoadingView.dismiss();
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_WRITE_PERM) {
            boolean granted = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }
            if (granted) {
                fragments = new ArrayList<>();
                fragments.add(new HomeFragment());
                fragments.add(new ReadingFragment());
                fragments.add(new HistoryFragment());
                setDefaultFragment(HOME);
            }
            else {
                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Tips")
                        .setMessage("没有给予一定的权限，程序将会终结")
                        .setNegativeButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if(System.currentTimeMillis()-exitTime>2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出你好繁",Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }
            else
            {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            switchFragment(HOME);
        }
        else if (id == R.id.nav_reading) {
            switchFragment(READING);
        }
        else if (id == R.id.nav_history) {
            switchFragment(HISTORY);
        }
        else if (id == R.id.nav_setting) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PRE, prePos);
    }

    private void setDefaultFragment(int pos){
        Fragment fragment = fragments.get(pos);
        if(fragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().show(fragment).commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, fragments.get(prePos), TAGS[pos]).commit();
        }
    }

    private void switchFragment(int pos) {
        Fragment currentFragment = fragments.get(pos);
        Fragment previousFragment = fragments.get(prePos);
        getSupportFragmentManager().beginTransaction().hide(previousFragment).commit();
        if(currentFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().show(currentFragment).commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, currentFragment, TAGS[pos]).commit();
        }
        prePos = pos;
    }

    private void updateDrawerInfo() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        ImageView iconImageView = headerView.findViewById(R.id.iconImageView);
        iconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                startActivity(intent);
            }
        });
        TextView nicknameTextView = headerView.findViewById(R.id.nicknameTextView);
        Picasso.get().load("https://avatars3.githubusercontent.com/u/30856589?s=460&v=4").transform(new CircleTransform()).into(iconImageView);
        nicknameTextView.setText("你好繁");
    }

    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }

    private void pullData() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo", 0);
        String username = sharedPreferences.getString("userName", "");
        String requestUrl = SERVER_URL + ":" + SEVER_PORT + "/studyPlan/getAllPlan?userName=" + username;
        try {
            String json = OkHttpRequest.get(requestUrl);
            JSONArray array = JSON.parseArray(json);
            SQLiteDatabase database = new SQLdm().openDataBase(this);
            if (array != null) {
                for (int i = 0; i < array.size(); i++) {
                    StudyPlan plan = array.getObject(i, StudyPlan.class);
                    String sql = "update words set " +
                            "learnTimes = " + plan.getLearnTimes() + ", " +
                            "learnDate = \"" + plan.getLearnDate() + "\", " +
                            "repeatTimes = " + plan.getRepeatTimes() + ", " +
                            "eFactor = " + plan.getEfactor() + ", " +
                            "nextDate = \"" + plan.getNextDate() + "\" " +
                            "where traditional = \"" + plan.getTradictional() + "\"";
                    database.execSQL(sql);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        requestUrl = SERVER_URL + ":" + SEVER_PORT + "/user/getSharedPreferences?userName=" + username;
        try {
            String json = OkHttpRequest.get(requestUrl);
            JSONObject obj = JSONObject.parseObject(json);
            int currentWord = obj.getInteger("currentWord");
            int wordsPerDay = obj.getInteger("wordsPerday");
            String lastLearnDate = obj.getString("lastLearnDate");
            String todayWords = obj.getString("todayWords");
            SharedPreferences fanData = getSharedPreferences("fan_data", 0);
            SharedPreferences.Editor editor = fanData.edit();
            editor.putInt("current_word", currentWord);
            if (wordsPerDay != 0) {
                editor.putInt("wordsPerDay", wordsPerDay);
            }
            else {
                editor.putInt("wordsPerDay", 20);
            }
            editor.putString("last_learn_date", lastLearnDate);
            editor.putString("today_words", todayWords);
            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-mm-dd hh:mm:ss");
            String updateTime = sdf.format(new Date());
            editor.putString("updateTime", updateTime);
            editor.apply();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}

//                    SharedPreferences sharedPreferences = getSharedPreferences("fan_data", 0);
//                    SimpleDateFormat sdf = new SimpleDateFormat("YYYY-mm-dd hh:mm:ss");
//
//                    SharedPreferences sp = getSharedPreferences("loginInfo", 0);
//                    String username = sp.getString("userName", "");
//                    String requestUrl = SERVER_URL + ":" + SEVER_PORT + "/user/getUpdateTime?userName=" + username;
//
//                    boolean needUpdate = false;
//                    try {
//                        String response = OkHttpRequest.get(requestUrl);
//                        Date updateTime = sdf.parse(response);
//                        Date lastUpdateTime = sdf.parse(sharedPreferences.getString("updateTime", ""));
//                        if (lastUpdateTime.before(updateTime)) {
//                            needUpdate = true;
//                        }
//                    }
//                    catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    if (needUpdate) {
//                        return;
//                    }
// LoadingView