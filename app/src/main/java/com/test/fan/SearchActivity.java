/*
说明：点击了搜索的按钮后，弹出来的activity
如果需要别的启动方式，比如需要返回数据之类的，请修改MainActivity中的onOptionsItemSelected函数
*/

package com.test.fan;

import android.Manifest;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class SearchActivity extends AppCompatActivity {

    //创建数据库
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //申请文件读权限
        requestPower();


            dbHelper=new DBHelper(this);
            SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
//        //2. 查询数据
////            String id,express;
////            // 调用SQLiteDatabase对象的query方法进行查询
////            // 返回一个Cursor对象：由数据库查询返回的结果集对象
////            Cursor cursor = sqliteDatabase.query("dict", new String[] { "id","express"}, "id=?", new String[] { "2" }, null, null, null);
////            while (cursor.moveToNext()) {
////                id = cursor.getString(cursor.getColumnIndex("id"));
////                express = cursor.getString(cursor.getColumnIndex("name"));
////                Toast.makeText(getApplicationContext(), "读sql内容："+id+","+express,Toast.LENGTH_SHORT).show();
////            }
            sqliteDatabase.close();

//        }catch(Exception e){
//            Toast.makeText(this, "" + "读取sql文件" +  "失败", Toast.LENGTH_SHORT).show();
//        }

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
