package com.ascend.wangfeng.udacityreviewer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences mSp;
    private TextView tvCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvCounter = findViewById(R.id.tv_counter);
        startService(new Intent(this,WorkService.class));
        mSp = MyApplication.getSp();
        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCount(0);
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
                Intent intent = new Intent(MainActivity.this,WorkService.class);
                intent.setAction("WorkService");
                stopService(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        int count = mSp.getInt("count", 0);
        tvCounter.setText(String.valueOf(count));
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
}
