package com.ascend.wangfeng.udacityreviewer;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SetActivity extends PreferenceActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }
}
