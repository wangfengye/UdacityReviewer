package com.ascend.wangfeng.udacityreviewer;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;

import java.util.ArrayList;

public class WorkService extends Service {

    public static final int SERVICE_ID = 9084;
    public static final int CODE = 1;
    public static final int FLAGS = 1;
    public static final String UDACITY_ACTION = "com.ascend.wangfeng.udacityreviewer";
    private static Handler mHandler;
    private static Runnable mRunnable;
    private ArrayList<String> mReviews = new ArrayList<>();
    private SharedPreferences mSp;
    private int mRate;

    public WorkService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 任务
        initData();
        //workHandler();
        workALarm();
        // 通知
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext());
        Intent nfIntent = new Intent(this,MainActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(this,0,nfIntent,0))
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_logo))
                .setContentTitle("Udacity")
                .setSmallIcon(R.drawable.ic_logo)
                .setContentText("working")
                .setWhen(System.currentTimeMillis());//设置通知时间
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND;// 设置默认声音
        startForeground(SERVICE_ID,notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * AlarmManager定时任务
     */
    private void workALarm() {
        // 注册广播接受
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UDACITY_ACTION);
        WorkBroadcastReceiver receiver = new WorkBroadcastReceiver();
        registerReceiver(receiver,intentFilter);
        //开启定时任务
        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        long time = System.currentTimeMillis();
        Intent intent = new Intent();
        intent.setAction(UDACITY_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), CODE,intent,0);
        // 正常使用,api>=19时会定时不准(cause: 系统优化省电)
        // am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,time,mRate,pendingIntent);
        // api>=19:可以准确定时,api>=23时,低电耗模式,无法工作
        //am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,time,pendingIntent);
        // api>=23,正常使用
        //am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,time,pendingIntent);
        // 小米5测试,()每一分钟执行一次
        /**测试手机 小米5,MIUI9.1
         *  intervalMillis, 实际执行间隔
         *  1000          , 1min
         *  2000          , 1min
         *  2000 000      , 30min左右
         *  测试手机 nexus6,android7.1.2
         *  2000 000      , 30min左右
         */
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP,time,1*1000,pendingIntent);
    }

    private void initData() {
        mSp = MyApplication.getSp();
        String rateStr = mSp.getString("rate", "60");
        mRate = Integer.parseInt(rateStr) * 1000;
    }

    /**
     * handler 定时任务
     */
    private void workHandler() {
        //定时任务
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                WorkUtil.getReviews();
                mHandler.postDelayed(this, mRate);
            }
        };
        mHandler.post(mRunnable);
    }




    @Override
    public void onDestroy() {
        stopForeground(true);
        NotificationManager a = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        a.cancel(SERVICE_ID);
        if (mHandler!=null)mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }
}
