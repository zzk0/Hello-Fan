/*
说明：点击了搜索的按钮后，弹出来的activity
如果需要别的启动方式，比如需要返回数据之类的，请修改MainActivity中的onOptionsItemSelected函数
*/

package com.test.fan;

import android.Manifest;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.test.fan.Adapter.SearchItemAdapter;
import com.test.fan.Bean.DictBean;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class SearchActivity extends AppCompatActivity {

    //控件：搜索栏、搜索结果
    FloatingSearchView searchView;
    boolean isHistory=true;

    //数据库
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    //存放查询结果的list
    List<DictBean> list;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView=(FloatingSearchView)findViewById(R.id.floating_search_view);
        searchView.setSearchFocusable(true);
        dbHelper=new DBHelper(this);

        //第一次聚焦到搜索框的时候，显示搜索历史
        setOnFocusChangeListener();

        //设置查询查询监听器，每次输入框有变化则查询
        setOnQueryChangeListener();

        //左侧返回按钮点击跳转主界面
        setOnHomeActionClickListener();

        //搜索结果
        setOnBindSuggestionCallback();

    }

    private void setOnBindSuggestionCallback() {
        searchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon,
                                         TextView textView, final SearchSuggestion item,final int itemPosition) {
                //展示查询结果的样式
                textView.setTextColor(Color.parseColor("#000000"));
                String text = item.getBody()
                        .replaceFirst(searchView.getQuery(),
                                "<font color=\"#787878\">" + searchView.getQuery() + "</font>");
                textView.setText(Html.fromHtml(text));

                //如果是展示搜索历史的话，左边添加一个人图标
                if( itemPosition!=0 && isHistory ){
                    leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.ic_history_black_24dp, null));
                    leftIcon.setAlpha(.36f);
                }
                if(isHistory && itemPosition==0) {
                    //为清除历史纪录按钮单独绑定事件
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    suggestionView.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            db=dbHelper.getWritableDatabase();
                            db.execSQL("delete from DictHistory");
                            db.close();
                            searchView.swapSuggestions(new ArrayList());
                        }
                    });
                }
                else {
                    //为每个搜索结果绑定点击事件，点击查看详情
                    suggestionView.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            DictBean now=list.get(itemPosition);
                            insertHistory(now);
                            View view = View.inflate(SearchActivity.this, R.layout.item_search,null);
                            TextView textView1=(TextView) view.findViewById(R.id.item_word);
                            TextView textView2=(TextView) view.findViewById(R.id.item_spell);
                            TextView textView3=(TextView) view.findViewById(R.id.item_express);
                            //textView1.setText();
                            textView2.setText(now.getSpell());
                            textView3.setText(now.getExpress());
                            String text = "<font size=\"18\"   color=\"#000000\">" + now.getWords() + "</font><br />"+
                                    "<i><font size=\"12\" color=\"#F08080\">" + now.getSpell() + "</font></i><br />"+
                                    "<i><font size=\"18\" color=\"#000000\">"+now.getExpress()+"</font></i><br />";
                            final NiftyDialogBuilder dialogBuilder=NiftyDialogBuilder.getInstance(SearchActivity.this);

                            dialogBuilder
                                    .withTitleColor("#000000")                                   //def
                                    .withMessage(Html.fromHtml(text))                                 //def  | withMessageColor(int resid)
                                    .withDialogColor("#FFFFFF")
                                    .withEffect(Effectstype.RotateBottom)
                                    .withButton1Text("OK")
                                    .withButton2Text("Cancel")
                                    .setButton1Click(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialogBuilder.dismiss();
                                            Toast.makeText(v.getContext(), "i'm btn1", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .setButton2Click(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialogBuilder.dismiss();
                                            Toast.makeText(v.getContext(),"i'm btn2",Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .show();

                           // MaterialDialog materialDialog=new MaterialDialog(SearchActivity.this,);
//                            AlertDialog.Builder normalDialog =
//                                    new AlertDialog.Builder(SearchActivity.this);
//                            SearchItemAdapter adapter=new SearchItemAdapter(SearchActivity.this,now);
//                            //normalDialog.setAdapter(adapter);
////                            normalDialog.setTitle(now.getWords()).setMessage(now.getExpress());
//                            normalDialog.setPositiveButton("收藏",
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            // ...To-do
//                                        }
//                                    });
//                            normalDialog.setNeutralButton("返回",
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            // ...To-do
//                                        }
//                                    });
//                            // 创建实例并显示
//                            normalDialog.show();
                        }
                    });
                }

            }

        });
    }

    private void setOnHomeActionClickListener() {
        searchView.setOnHomeActionClickListener(
                new FloatingSearchView.OnHomeActionClickListener() {
                    @Override
                    public void onHomeClicked() {
                        Intent intent = new Intent(SearchActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                });
    }

    private void setOnQueryChangeListener() {
        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                if(newQuery.length()==0) {
                    isHistory=true;
                    showSearchResult(newQuery,"DictHistory","ID desc");
                }
                else {
                    isHistory=false;
                    showSearchResult("where words like '"+newQuery+"%' ","dict","length(words)");
                }
            }
        });
    }

    /*
    *展示查询结果
    * @Param query 查询条件
     */
    public void showSearchResult(String query,String dbname,String sort){
        list=new ArrayList<>();
        db=dbHelper.getReadableDatabase();

        Cursor cursor=db.rawQuery(
                "select * from "+dbname+" "+query+" order by "+sort, null);
        //搜索结果对象化为DictBean，存入list
        while (cursor.moveToNext()) {
            DictBean dictBean=new DictBean(
                    cursor.getString(cursor.getColumnIndex("words")),
                    cursor.getString(cursor.getColumnIndex("spell")),
                    cursor.getString(cursor.getColumnIndex("express")));
            list.add(dictBean);
        }
        //如果是show查询历史的话，添加一个清除历史的按钮
        if(query.length()==0 && list.size()!=0)
        list.add(new DictBean("点击清除历史",null,null));
        //关闭查询
        cursor.close();
        db.close();
        searchView.swapSuggestions(list);
    }

    /*
    *添加一条历史纪录
     */
    public void insertHistory(DictBean dictBean) {
        db=dbHelper.getWritableDatabase();
               try{
            db.execSQL("delete from DictHistory where words='"+dictBean.getWords()+"'");
        }
        catch(Exception e){

        }finally {
            ContentValues contentValues=new ContentValues();
            contentValues.put("words",dictBean.getWords());
            contentValues.put("spell",dictBean.getSpell());
            contentValues.put("express",dictBean.getExpress());
            long id=db.insert("DictHistory",null,contentValues);
            db.close();
        }
    }
    /*
    *
     */
    public void setOnFocusChangeListener() {
        searchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener(){
            @Override
            public void onFocus() {
                if(searchView.getQuery().length()==0)
                    showSearchResult(searchView.getQuery(),"DictHistory","ID desc");
            }
            @Override
            public void onFocusCleared() { }
        });
    }

    public void requestPower() {
        //判断是否已经赋予权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PERMISSION_GRANTED) {
            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {//这里可以写个对话框之类的项向用户解释为什么要申请权限，并在对话框的确认键后续再次申请权限.它在用户选择"不再询问"的情况下返回false
            } else {
                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {
                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请成功", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
