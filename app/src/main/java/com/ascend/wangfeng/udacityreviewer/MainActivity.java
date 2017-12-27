package com.ascend.wangfeng.udacityreviewer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "mian";
    private static int mRate = 60 * 1000;
    private static String[] mAllowedProjectIds;
    private SharedPreferences sp;
    private ArrayList<String> mReviews = new ArrayList<>();
    private TextView tvCounter;
    private Boolean mIsTest = false;
    private int mCounter;//记录请求次数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvCounter = findViewById(R.id.tv_counter);
        sp = MyApplication.getSp();
        initProjects();
        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("count", 0);
                editor.commit();
            }
        });
        findViewById(R.id.test_api).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsTest = true;
                getData();
            }
        });
        //定时任务
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getData();
                handler.postDelayed(this, mRate);
            }
        };
        handler.post(runnable);

        mCounter = sp.getInt("count", 0);

    }

    private void initProjects() {
        String dataStr = sp.getString("projectIds", "project");
        mAllowedProjectIds = dataStr.split("，");
        String rateStr = sp.getString("rate", "60");
        mRate = Integer.parseInt(rateStr) * 1000;
    }

    @Override
    protected void onResume() {
        tvCounter.setText(String.valueOf(mCounter));
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt("count", mCounter);
        edit.commit();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt("count", mCounter);
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

    public void getData() {
        Log.i(TAG, "getData: " + "start");
        // 清空审阅
        mReviews.clear();
        Client.getInstance().getReviews()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: " + e.getMessage());
                        if (mIsTest) {
                            mIsTest = false;
                        }
                        Toast.makeText(MainActivity.this, R.string.api_error, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(String s) {
                        Log.i(TAG, "onNext: " + s);
                        mCounter++;
                        if (mIsTest) {
                            mIsTest = false;
                            Toast.makeText(MainActivity.this, getString(R.string.api_test_str) + s, Toast.LENGTH_LONG).show();
                        }
                        getReviewsCount(s);
                        if (mReviews.size() > 0) {
                            for (int i = 0; i < mReviews.size(); i++) {
                                applyReview(mReviews.get(i));
                            }
                            ring();
                        }
                    }
                });
    }

    // 申请review
    private void applyReview(String projectId) {
        Client.getInstance().applyReview(projectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        Toast.makeText(MainActivity.this, R.string.apply_success, Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onNext: " + s);
                    }
                });
    }

    /**
     * 响铃
     */
    private void ring() {
        BellandShake.open(20000, 0, this);
    }

    /**
     * add reviews;
     *
     * @param data 请求返回的json数组
     */
    private void getReviewsCount(String data) {
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                JSONObject o2 = object.getJSONObject("project");
                String projectId = o2.getString("id");
                Log.i(TAG, "getReviewsCount: ");
                if (hasPermissionOfProject(projectId)) {
                    int num = o2.getInt("awaiting_review_count");
                    if (num > 0) {
                        JSONObject o3 = o2.getJSONObject("awaiting_review_count_by_language");
                        if (o3.has("zh-cn")) {
                            mReviews.add(projectId);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param id project id
     * @return has permission of the project
     */
    private boolean hasPermissionOfProject(String id) {

        for (int i = 0; i < mAllowedProjectIds.length; i++) {
            if (id.equals(mAllowedProjectIds[i])) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt("count", mCounter);
        edit.commit();
        super.onDestroy();

    }
}
