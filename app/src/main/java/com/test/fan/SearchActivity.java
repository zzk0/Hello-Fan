/*
说明：点击了搜索的按钮后，弹出来的activity
如果需要别的启动方式，比如需要返回数据之类的，请修改MainActivity中的onOptionsItemSelected函数
*/

package com.test.fan;

import android.Manifest;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.test.Bean.DictBean;
import com.test.util.DBHelper;
import com.test.util.SQLdm;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class SearchActivity extends Activity {

    //控件：搜索栏、搜索结果
    FloatingSearchView searchView;
    boolean isHistory=true;

    //数据库
    private SQLiteDatabase db;
    //存放查询结果的list
    List<DictBean> list;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //获得数据库

        try {
            db = new SQLdm().openDataBase(getApplicationContext());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        searchView=(FloatingSearchView)findViewById(R.id.floating_search_view);
        searchView.setSearchFocusable(true);

        //第一次聚焦到搜索框的时候，显示搜索历史
        setOnFocusChangeListener();

        //设置查询查询监听器，每次输入框有变化则查询
        setOnQueryChangeListener();

        //左侧返回按钮点击跳转主界面
        setOnHomeActionClickListener();

        //搜索结果
        setOnBindSuggestionCallback();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (db != null) {
            db.close();
            db = null;
        }
    }

    /*
    *将查询结果显示的回调事件
     */
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

//                //如果是展示搜索历史的话，左边添加一个人图标
//                if( itemPosition!=0 && isHistory ){
//                    leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
//                            R.drawable.ic_history_black_24dp, null));
//                    leftIcon.setAlpha(.36f);
//                }
                if(isHistory && itemPosition==0) {
                    //为清除历史纪录按钮单独绑定事件
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    suggestionView.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            searchView.swapSuggestions(new ArrayList());
                        }
                    });
                }
                else {
                    //为每个搜索结果绑定点击事件，点击查看详情
                    suggestionView.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            final DictBean now=list.get(itemPosition);
                            insertHistory(now);
                            String text = "<font size=\"18\"   color=\"#000000\">" + now.getWords() + "</font><br />"+
                                    "<i><font size=\"12\" color=\"#F08080\">" + now.getSpell() + "</font></i><br />"+
                                    "<i><font size=\"18\" color=\"#000000\">"+now.getExpress()+"</font></i><br />";
                            final NiftyDialogBuilder dialogBuilder=NiftyDialogBuilder.getInstance(SearchActivity.this);

                            dialogBuilder
                                    .withTitleColor("#000000")                                   //def
                                    .withMessage(Html.fromHtml(text))                                 //def  | withMessageColor(int resid)
                                    .withDialogColor("#FFFFFF")
                                    .withEffect(Effectstype.RotateBottom)
                                    .withButton1Text("练字")
                                    .withButton2Text("Cancel")
                                    .setButton1Click(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialogBuilder.dismiss();
                                            // Toast.makeText(v.getContext(), "i'm btn1", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SearchActivity.this, LearnOneWordActivity.class);
                                            intent.putExtra("phrase", now.getWords());
                                            startActivity(intent);
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

                        }
                    });
                }

            }

        });
    }

    /*
    *点击左侧返回按钮回到主界面事件
     */
    private void setOnHomeActionClickListener() {
        searchView.setOnHomeActionClickListener(
                new FloatingSearchView.OnHomeActionClickListener() {
                    @Override
                    public void onHomeClicked() {
                        finish();
                    }
                });
    }

    /*
    *查询结果
     */
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
                    showSearchResult("where words like '"+transToTrad(newQuery)+"%' ","dict","length(words)");
                }
            }
        });
    }

    /*
    *展示查询结果
    * @Param query 查询条件
     */
    public void showSearchResult(String query,String dbname,String sort){
        list = new ArrayList<>();
        String sql = "select * from "+dbname+" "+query+" order by "+sort;

        Cursor cursor=db.rawQuery(
                sql, null);
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
        searchView.swapSuggestions(list);
    }

    /*
    *添加一条历史纪录
     */
    public void insertHistory(DictBean dictBean) {
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
        }
    }
    /*
    *第一次点击搜索框的，显示搜索历史
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


    /**
     * 将一个字符串（不管里面是繁体字，简体字还是繁体简体混杂一起的，全部转为繁体）
     * @param simp
     * @return
     */
    private String transToTrad(String simp) {
        String sql = "select traditional from words where simplified=?";
        String traditional=new String();
        for(int i=0;i<simp.length();i++){
            Cursor cursor=db.rawQuery(
                    sql, new String[]{simp.charAt(i)+""});
            if (cursor.moveToNext()) {
                String now= cursor.getString(cursor.getColumnIndex("traditional"));
                // System.out.println("搜索变繁体：cursor.now"+now);
                traditional+=now;
            }
            else
                traditional+=simp.charAt(i);
            cursor.close();
        }
        // System.out.println("搜索变繁体："+traditional);
        return traditional;
    }

}
