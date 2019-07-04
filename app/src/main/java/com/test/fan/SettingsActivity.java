package com.test.fan;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.test.util.SQLdm;

public class SettingsActivity extends AppCompatActivity {

    private SettingsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        fragment = new SettingsFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, fragment)
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("fan_data", 0);

            // 设置每天学习字数
            Preference wordsPerDay = findPreference("wordsPerDay");
            wordsPerDay.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final int wordsSetting = sharedPreferences.getInt("wordsPerDay", 20);
                    final String[] items = {"10", "15", "20", "25", "30"};
                    int pos = 0;
                    for (int i = 0; i < items.length; i++) {
                        if (String.valueOf(wordsSetting).equals(items[i])) {
                            break;
                        }
                        pos = pos + 1;
                    }
                    final int defaultPos = pos;

                    Dialog dialog;
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    final int[] selectedPos = { defaultPos };
                    builder.setTitle("每天学习字数 : ");
                    builder.setSingleChoiceItems(items, selectedPos[0],
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    selectedPos[0] = which;
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    sharedPreferences.edit().putInt("wordsPerDay", Integer.valueOf(items[selectedPos[0]])).commit();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    dialog = builder.create();
                    dialog.show();
                    return false;
                }
            });

            // 清除所有数据
            Preference cleanData = findPreference("cleanData");
            cleanData.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("警告");
                    builder.setMessage("是否确定清除所有数据?");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Context context = getActivity().getApplicationContext();
                            SQLdm.renewDatabase(context);
                            SharedPreferences sharedPreferences = context.getSharedPreferences("fan_data", 0);
                            sharedPreferences.edit().clear().apply();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return false;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}