package com.ascend.wangfeng.udacityreviewer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by fengye on 2018/1/11.
 * email 1040441325@qq.com
 */

public class WorkBroadcastReceiver extends BroadcastReceiver{
    public static final int CODE = 1;

    public static int mRate =  Integer.parseInt(MyApplication.getSp().getString("rate", "60")) * 1000;
    @Override
    public void onReceive(Context context, Intent intent) {
        WorkUtil.getReviews();
      /*  AlarmManager am = (AlarmManager) MyApplication.getContext().getSystemService(Context.ALARM_SERVICE);
        long time = System.currentTimeMillis();
        Intent intent1 = new Intent();
        intent.setAction(WorkService.UDACITY_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MyApplication.getContext(),CODE,intent1,0);
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,time,pendingIntent);*/
    }
}
