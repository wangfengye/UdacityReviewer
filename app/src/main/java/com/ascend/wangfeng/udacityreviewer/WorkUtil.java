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
    private static String[] mAllowedProjectIds = MyApplication.getSp().getString("projectIds", "project").split("，");

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
                            Toast.makeText(MyApplication.getContext(), MyApplication.getContext().getString(R.string.api_test_str)+ s, Toast.LENGTH_LONG).show();
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
    public static String getTestGetReviewsData(){
        return "[{\"id\":33363,\"grader_id\":157054,\"project_id\":288,\"trainings_count\":0,\"active\":true,\"status\":\"applied\",\"notes\":null,\"allowed_to_audit\":false,\"created_at\":\"2017-09-30T05:30:26.172Z\",\"updated_at\":\"2017-09-30T05:30:26.172Z\",\"certified_at\":null,\"project\":{\"id\":288,\"name\":\"Make A Baking App\",\"nanodegree_key\":\"nd801\",\"udacity_key\":\"5de85c17-8470-4db1-bc53-9ec4c16c2679\",\"visible\":true,\"is_cert_project\":false,\"required_skills\":\"Understands proper usage of MediaPlayer/Exoplayer APIs, Java proficiency, Basic UI elements, Intents, Content Provider, Async Task, Sync Adapters, Android Studio, Widgets, Localization, Accessibility [Related Course: Advanced Android App Development](https://www.udacity.com/course/advanced-android-app-development--ud855).\",\"audit_project_id\":null,\"hashtag\":null,\"audit_rubric_id\":1144,\"awaiting_review_count\":0,\"awaiting_review_count_by_language\":{},\"awaiting_audit_count\":2,\"awaiting_quality_audit_count\":0,\"awaiting_training_audit_count\":2,\"created_at\":\"2017-03-15T04:29:36.961Z\",\"updated_at\":\"2018-01-17T01:11:28.531Z\",\"price\":\"27.21\",\"audit_price\":\"25.0\"}},{\"id\":33291,\"grader_id\":157054,\"project_id\":243,\"trainings_count\":0,\"active\":true,\"status\":\"applied\",\"notes\":null,\"allowed_to_audit\":false,\"created_at\":\"2017-09-29T06:59:43.487Z\",\"updated_at\":\"2017-09-29T06:59:43.487Z\",\"certified_at\":null,\"project\":{\"id\":243,\"name\":\"热门电影 第2阶段\",\"nanodegree_key\":\"nd801-cn-advanced\",\"udacity_key\":\"8514c916-aac3-4092-8c74-41b5a5d43ce1\",\"visible\":true,\"is_cert_project\":false,\"required_skills\":\"\",\"audit_project_id\":null,\"hashtag\":null,\"audit_rubric_id\":1144,\"awaiting_review_count\":0,\"awaiting_review_count_by_language\":{},\"awaiting_audit_count\":0,\"awaiting_quality_audit_count\":0,\"awaiting_training_audit_count\":0,\"created_at\":\"2016-10-28T06:01:58.004Z\",\"updated_at\":\"2018-01-14T15:11:13.930Z\",\"price\":\"20.41\",\"audit_price\":\"19.0\"}},{\"id\":33267,\"grader_id\":157054,\"project_id\":63,\"trainings_count\":0,\"active\":true,\"status\":\"applied\",\"notes\":null,\"allowed_to_audit\":false,\"created_at\":\"2017-09-28T07:45:10.538Z\",\"updated_at\":\"2017-09-28T07:45:10.538Z\",\"certified_at\":null,\"project\":{\"id\":63,\"name\":\"Make Your App Material\",\"nanodegree_key\":\"nd801\",\"udacity_key\":\"4035898751\",\"visible\":true,\"is_cert_project\":false,\"required_skills\":\"[Material Design for Android Developers](https://www.udacity.com/course/material-design-for-android-developers--ud862) - Material Design Guidelines, Java proficiency, Android Studio, Basic application components covered in [Android Fundamentals](https://www.udacity.com/course/developing-android-apps--ud853)\",\"audit_project_id\":72,\"hashtag\":\"nanodegree,androiddevelopment\",\"audit_rubric_id\":1144,\"awaiting_review_count\":0,\"awaiting_review_count_by_language\":{},\"awaiting_audit_count\":8,\"awaiting_quality_audit_count\":1,\"awaiting_training_audit_count\":7,\"created_at\":\"2015-05-27T20:17:51.782Z\",\"updated_at\":\"2018-01-17T01:34:37.184Z\",\"price\":\"16.49\",\"audit_price\":\"15.0\"}},{\"id\":33144,\"grader_id\":157054,\"project_id\":164,\"trainings_count\":0,\"active\":true,\"status\":\"applied\",\"notes\":null,\"allowed_to_audit\":false,\"created_at\":\"2017-09-25T06:49:32.623Z\",\"updated_at\":\"2017-09-25T06:49:32.623Z\",\"certified_at\":null,\"project\":{\"id\":164,\"name\":\"Book Listing\",\"nanodegree_key\":\"nd803\",\"udacity_key\":\"7821318759\",\"visible\":true,\"is_cert_project\":false,\"required_skills\":\"Java, Android, XML\",\"audit_project_id\":72,\"hashtag\":\"nanodegree, BeginningAndroid\",\"audit_rubric_id\":1144,\"awaiting_review_count\":1,\"awaiting_review_count_by_language\":{\"zh-cn\":1},\"awaiting_audit_count\":4,\"awaiting_quality_audit_count\":0,\"awaiting_training_audit_count\":4,\"created_at\":\"2016-04-20T18:17:56.947Z\",\"updated_at\":\"2018-01-17T00:57:07.817Z\",\"price\":\"10.72\",\"audit_price\":\"10.0\"}},{\"id\":33142,\"grader_id\":157054,\"project_id\":158,\"trainings_count\":0,\"active\":true,\"status\":\"applied\",\"notes\":null,\"allowed_to_audit\":false,\"created_at\":\"2017-09-25T03:39:49.175Z\",\"updated_at\":\"2017-09-25T03:39:49.175Z\",\"certified_at\":null,\"project\":{\"id\":158,\"name\":\"Quiz App\",\"nanodegree_key\":\"nd803\",\"udacity_key\":\"6767261086\",\"visible\":true,\"is_cert_project\":false,\"required_skills\":\"Java, Android, XML\",\"audit_project_id\":72,\"hashtag\":\"nanodegree, BeginningAndroid\",\"audit_rubric_id\":1144,\"awaiting_review_count\":0,\"awaiting_review_count_by_language\":{},\"awaiting_audit_count\":0,\"awaiting_quality_audit_count\":0,\"awaiting_training_audit_count\":0,\"created_at\":\"2016-04-20T18:05:51.663Z\",\"updated_at\":\"2018-01-17T00:52:19.927Z\",\"price\":\"10.72\",\"audit_price\":\"10.0\"}},{\"id\":33141,\"grader_id\":157054,\"project_id\":157,\"trainings_count\":0,\"active\":true,\"status\":\"applied\",\"notes\":null,\"allowed_to_audit\":false,\"created_at\":\"2017-09-25T02:23:20.233Z\",\"updated_at\":\"2017-09-25T02:23:20.233Z\",\"certified_at\":null,\"project\":{\"id\":157,\"name\":\"Score Keeper\",\"nanodegree_key\":\"nd803\",\"udacity_key\":\"7830628637\",\"visible\":true,\"is_cert_project\":false,\"required_skills\":\"Java, Android, XML\",\"audit_project_id\":72,\"hashtag\":\"nanodegree, BeginningAndroid\",\"audit_rubric_id\":1144,\"awaiting_review_count\":0,\"awaiting_review_count_by_language\":{},\"awaiting_audit_count\":3,\"awaiting_quality_audit_count\":0,\"awaiting_training_audit_count\":3,\"created_at\":\"2016-04-20T18:03:56.593Z\",\"updated_at\":\"2018-01-16T20:00:24.429Z\",\"price\":\"10.72\",\"audit_price\":\"10.0\"}},{\"id\":32767,\"grader_id\":157054,\"project_id\":66,\"trainings_count\":0,\"active\":true,\"status\":\"applied\",\"notes\":null,\"allowed_to_audit\":false,\"created_at\":\"2017-09-17T09:10:46.138Z\",\"updated_at\":\"2017-09-17T09:10:46.138Z\",\"certified_at\":null,\"project\":{\"id\":66,\"name\":\"Popular Movies, Stage 1\",\"nanodegree_key\":\"nd801\",\"udacity_key\":\"4256658707\",\"visible\":true,\"is_cert_project\":false,\"required_skills\":\"[Developing Android Apps -Android Fundamentals](https://www.udacity.com/course/developing-android-apps--ud853): Java Proficiency, Base UI Elements, Intents, Sync Adapters, Content Providers, Android Studio.\",\"audit_project_id\":72,\"hashtag\":\"nanodegree,androiddevelopment\",\"audit_rubric_id\":1144,\"awaiting_review_count\":0,\"awaiting_review_count_by_language\":{},\"awaiting_audit_count\":5,\"awaiting_quality_audit_count\":1,\"awaiting_training_audit_count\":4,\"created_at\":\"2015-06-06T06:48:55.588Z\",\"updated_at\":\"2018-01-17T00:25:03.805Z\",\"price\":\"20.41\",\"audit_price\":\"19.0\"}},{\"id\":31225,\"grader_id\":157054,\"project_id\":161,\"trainings_count\":1,\"active\":true,\"status\":\"certified\",\"notes\":\"\",\"allowed_to_audit\":false,\"created_at\":\"2017-07-18T15:33:57.199Z\",\"updated_at\":\"2017-07-24T13:52:44.899Z\",\"certified_at\":\"2017-07-24T13:52:44.897Z\",\"project\":{\"id\":161,\"name\":\"Tour guide app\",\"nanodegree_key\":\"nd803\",\"udacity_key\":\"6745394898\",\"visible\":true,\"is_cert_project\":false,\"required_skills\":\"Java, Android, XML\",\"audit_project_id\":72,\"hashtag\":\"nanodegree, BeginningAndroid\",\"audit_rubric_id\":1144,\"awaiting_review_count\":0,\"awaiting_review_count_by_language\":{},\"awaiting_audit_count\":1,\"awaiting_quality_audit_count\":0,\"awaiting_training_audit_count\":1,\"created_at\":\"2016-04-20T18:13:17.162Z\",\"updated_at\":\"2018-01-17T01:22:13.921Z\",\"price\":\"10.61\",\"audit_price\":\"10.0\"}},{\"id\":31224,\"grader_id\":157054,\"project_id\":159,\"trainings_count\":1,\"active\":true,\"status\":\"certified\",\"notes\":\"\",\"allowed_to_audit\":false,\"created_at\":\"2017-07-18T15:27:59.412Z\",\"updated_at\":\"2017-07-24T14:45:12.406Z\",\"certified_at\":\"2017-07-24T14:45:12.402Z\",\"project\":{\"id\":159,\"name\":\"Musical Structure\",\"nanodegree_key\":\"nd803\",\"udacity_key\":\"7827728716\",\"visible\":true,\"is_cert_project\":false,\"required_skills\":\"Java, Android , XML\",\"audit_project_id\":72,\"hashtag\":\"nanodegree, BeginningAndroid\",\"audit_rubric_id\":1144,\"awaiting_review_count\":0,\"awaiting_review_count_by_language\":{},\"awaiting_audit_count\":0,\"awaiting_quality_audit_count\":0,\"awaiting_training_audit_count\":0,\"created_at\":\"2016-04-20T18:07:18.939Z\",\"updated_at\":\"2018-01-16T22:30:06.968Z\",\"price\":\"12.25\",\"audit_price\":\"11.0\"}},{\"id\":31221,\"grader_id\":157054,\"project_id\":133,\"trainings_count\":1,\"active\":true,\"status\":\"training\",\"notes\":\"\",\"allowed_to_audit\":false,\"created_at\":\"2017-07-18T09:11:45.426Z\",\"updated_at\":\"2017-07-19T08:00:42.889Z\",\"certified_at\":null,\"project\":{\"id\":133,\"name\":\"Your First App\",\"nanodegree_key\":\"nd803\",\"udacity_key\":\"6774550984\",\"visible\":true,\"is_cert_project\":false,\"required_skills\":\"Android studio use, XML code understanding\",\"audit_project_id\":72,\"hashtag\":\"nanodegree,androiddevelopment\",\"audit_rubric_id\":1144,\"awaiting_review_count\":0,\"awaiting_review_count_by_language\":{},\"awaiting_audit_count\":0,\"awaiting_quality_audit_count\":0,\"awaiting_training_audit_count\":0,\"created_at\":\"2016-02-05T00:33:21.341Z\",\"updated_at\":\"2018-01-16T21:35:39.883Z\",\"price\":\"7.42\",\"audit_price\":\"7.0\"}},{\"id\":31213,\"grader_id\":157054,\"project_id\":162,\"trainings_count\":1,\"active\":true,\"status\":\"certified\",\"notes\":\"\",\"allowed_to_audit\":false,\"created_at\":\"2017-07-18T07:49:01.552Z\",\"updated_at\":\"2017-08-22T03:52:35.925Z\",\"certified_at\":\"2017-08-22T03:52:35.923Z\",\"project\":{\"id\":162,\"name\":\"Habit Tracker\",\"nanodegree_key\":\"nd803\",\"udacity_key\":\"7822168727\",\"visible\":true,\"is_cert_project\":false,\"required_skills\":\"Java, Android, XML, SQL\",\"audit_project_id\":72,\"hashtag\":\"nanodegree, BeginningAndroid\",\"audit_rubric_id\":1144,\"awaiting_review_count\":0,\"awaiting_review_count_by_language\":{},\"awaiting_audit_count\":8,\"awaiting_quality_audit_count\":6,\"awaiting_training_audit_count\":2,\"created_at\":\"2016-04-20T18:14:45.276Z\",\"updated_at\":\"2018-01-16T05:19:15.602Z\",\"price\":\"10.61\",\"audit_price\":\"10.0\"}},{\"id\":31074,\"grader_id\":157054,\"project_id\":165,\"trainings_count\":1,\"active\":true,\"status\":\"certified\",\"notes\":\"\",\"allowed_to_audit\":false,\"created_at\":\"2017-07-10T12:09:44.168Z\",\"updated_at\":\"2017-07-14T07:52:25.143Z\",\"certified_at\":\"2017-07-14T07:52:25.140Z\",\"project\":{\"id\":165,\"name\":\"News App\",\"nanodegree_key\":\"nd803\",\"udacity_key\":\"6752095343\",\"visible\":true,\"is_cert_project\":false,\"required_skills\":\"Java, Android, XML\",\"audit_project_id\":72,\"hashtag\":\"nanodegree, BeginningAndroid\",\"audit_rubric_id\":1144,\"awaiting_review_count\":1,\"awaiting_review_count_by_language\":{\"zh-cn\":1},\"awaiting_audit_count\":1,\"awaiting_quality_audit_count\":1,\"awaiting_training_audit_count\":0,\"created_at\":\"2016-04-20T18:19:12.384Z\",\"updated_at\":\"2018-01-16T23:49:48.160Z\",\"price\":\"22.27\",\"audit_price\":\"20.0\"}},{\"id\":31073,\"grader_id\":157054,\"project_id\":218,\"trainings_count\":1,\"active\":true,\"status\":\"training\",\"notes\":\"\",\"allowed_to_audit\":false,\"created_at\":\"2017-07-10T12:09:44.049Z\",\"updated_at\":\"2017-07-20T04:53:36.185Z\",\"certified_at\":null,\"project\":{\"id\":218,\"name\":\"滴滴出行项目：最新闻\",\"nanodegree_key\":\"nd801\",\"udacity_key\":null,\"visible\":true,\"is_cert_project\":false,\"required_skills\":\"[Gradle for Android and Java](https://www.udacity.com/course/gradle-for-android-and-java--ud867) and [Material Design for Android Developers](https://www.udacity.com/course/material-design-for-android-developers--ud862) - Material Design Guidelines, Java proficiency, Android Studio, Basic application components covered in [Android Fundamentals](https://www.udacity.com/course/developing-android-apps--ud853)\",\"audit_project_id\":null,\"hashtag\":null,\"audit_rubric_id\":1144,\"awaiting_review_count\":0,\"awaiting_review_count_by_language\":{},\"awaiting_audit_count\":1,\"awaiting_quality_audit_count\":0,\"awaiting_training_audit_count\":1,\"created_at\":\"2016-09-12T01:44:18.588Z\",\"updated_at\":\"2017-09-18T18:29:40.280Z\",\"price\":\"41.23\",\"audit_price\":\"38.0\"}}]";
    }
}
