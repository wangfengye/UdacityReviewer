package com.ascend.wangfeng.udacityreviewer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by fengye on 2017/12/27.
 * email 1040441325@qq.com
 */

public class MyApplication extends Application{
    private static SharedPreferences mSp;
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext =this;
        mSp = PreferenceManager.getDefaultSharedPreferences(this);
    }
    public static SharedPreferences getSp(){
        return mSp;
    }
    public static Context getContext(){
        return mContext;
    }
}
