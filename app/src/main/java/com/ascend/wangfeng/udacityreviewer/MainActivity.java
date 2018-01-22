package com.ascend.wangfeng.udacityreviewer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences mSp;
    private TextView tvCounter;
    private TextView tvStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvCounter = findViewById(R.id.tv_counter);
        tvStart = findViewById(R.id.tv_start);
        startService(new Intent(this, WorkService.class));
        mSp = MyApplication.getSp();
        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCount(0);
                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                SharedPreferences.Editor edit = mSp.edit();
                edit.putString("start", format.format(date));
                edit.commit();
            }
        });
        findViewById(R.id.test_api).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WorkUtil.getReviews(true);
            }
        });
        findViewById(R.id.btn_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WorkService.class);
                intent.setAction("WorkService");
                stopService(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        int count = mSp.getInt("count", 0);
        tvCounter.setText("运行时间: " + minToStringFormat(count));
        String start = mSp.getString("start","未知");
        tvStart.setText("开始时间: " + start);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    public void saveCount(int count) {
        SharedPreferences.Editor edit = mSp.edit();
        edit.putInt("count", count);
        edit.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mian, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                startActivity(new Intent(MainActivity.this, SetActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private String minToStringFormat(int min) {
        String result = "";
        int minutes = min % 60;
        result = minutes == 0 ? "" : (minutes + "m") + result;
        if (min >= 60) {
            min = min / 60;
            int hours = min % 24;
            result = hours == 0 ? "" : (hours + "h") + result;
        }
        if (min >= 24) {
            int days = min / 24;
            result = days == 0 ? "" : (days + "d") + result;
        }
        if (result == "") {
            result = "0m";
        }
        return result;
    }
}
