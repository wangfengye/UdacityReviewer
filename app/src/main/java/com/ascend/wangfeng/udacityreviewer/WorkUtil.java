package com.ascend.wangfeng.udacityreviewer;

import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by fengye on 2018/1/11.
 * email 1040441325@qq.com
 */

public class WorkUtil {
    private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    public static final String TAG = WorkUtil.class.getSimpleName();
    private static String[] mAllowedProjectIds = MyApplication.getSp().getString("projectIds", "project").split(",");

    public static void getReviews() {
        getReviews(false);
    }

    public static void getReviews(final boolean show) {
        if (!show) count();
        saveLog();
        final String timeStart = formatter.format(new Date());
        Client.getInstance().getReviews()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        saveApiState(timeStart,"failed");
                        Toast.makeText(MyApplication.getContext(), R.string.api_error, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(String s) {
                        saveApiState(timeStart,"success");
                        Log.i(TAG, "onNext: " + s);
                        if (show) {
                            Toast.makeText(MyApplication.getContext(), R.string.api_test_str + s, Toast.LENGTH_LONG).show();
                        }
                        ArrayList<String> reviews = processReviews(s);
                        if (reviews.size() > 0) {
                            for (int i = 0; i < reviews.size(); i++) {
                                applyReview(reviews.get(i));
                            }
                            BellandShake.open(20000, 0, MyApplication.getContext());
                        }
                    }
                });
    }

    private static void saveLog() {
        String time = formatter.format(new Date());
        writeToSd(time,"udacity.txt");
    }
    private static void saveApiState(String timeStart,String state){
        String time = formatter.format(new Date());
        String data= timeStart+"---"+time+": "+ state;
        writeToSd(data,"udacityApi.txt");

    }

    private static void writeToSd(String s,String fileName) {
        String data = s + "\n";
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String sdPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
                        + "/AscendLog";
                File dir = new File(sdPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, fileName);
                FileOutputStream fos = new FileOutputStream(file, true);
                fos.write(data.toString().getBytes());
                fos.flush();
                fos.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
    }

    // 申请review
    private static void applyReview(String projectId) {
        Client.getInstance().applyReview(projectId, "zh-cn")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MyApplication.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(String s) {
                        Toast.makeText(MyApplication.getContext(), R.string.apply_success, Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onNext: " + s);
                    }
                });
    }

    /**
     * @param data 请求返回的json数组
     * @return 可审阅的项目id集合
     */
    private static ArrayList<String> processReviews(String data) {
        ArrayList<String> result = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                JSONObject o2 = object.getJSONObject("project");
                String projectId = o2.getString("id");
                if (hasPermissionOfProject(projectId)) {
                    int num = o2.getInt("awaiting_review_count");
                    if (num > 0) {
                        JSONObject o3 = o2.getJSONObject("awaiting_review_count_by_language");
                        if (o3.has("zh-cn")) {
                            result.add(projectId);
                        }
                    }
                }
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return result;
        }
    }

    /**
     * @param id 项目id
     * @return 是否有审阅权限
     */
    private static boolean hasPermissionOfProject(String id) {

        for (int i = 0; i < mAllowedProjectIds.length; i++) {
            if (id.equals(mAllowedProjectIds[i])) {
                return true;
            }
        }
        return false;
    }

    private static void count() {
        int count = MyApplication.getSp().getInt("count", 0) + 1;
        SharedPreferences.Editor edit = MyApplication.getSp().edit();
        edit.putInt("count", count);
        edit.commit();
    }
}
