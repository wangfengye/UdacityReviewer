package com.ascend.wangfeng.udacityreviewer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;

public class SetActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private EditTextPreference mEpRate;
    private EditTextPreference mEpApi;
    private EditTextPreference mEpProjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        // 以下代码:可以使设置页显示所有设置
       /* initView();
        MyApplication.getSp().registerOnSharedPreferenceChangeListener(this);*/
    }

    private void initView() {
        mEpRate = (EditTextPreference) getPreferenceScreen().findPreference("rate");
        mEpApi = (EditTextPreference) getPreferenceScreen().findPreference("api");
        mEpProjects = (EditTextPreference) getPreferenceScreen().findPreference("projectIds");
        mEpRate.setSummary(MyApplication.getSp().getString("rate", ""));
        mEpApi.setSummary(MyApplication.getSp().getString("api", ""));
        mEpProjects.setSummary(MyApplication.getSp().getString("projectIds", ""));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String s) {
        switch (s) {
            case "rate":
                mEpRate.setSummary(preferences.getString("rate", ""));
                break;
            case "api":
                mEpApi.setSummary(preferences.getString("api", ""));
                break;
            case "projectIds":
                mEpProjects.setSummary(preferences.getString("projectIds",""));
                break;
            default:
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getSp().unregisterOnSharedPreferenceChangeListener(this);
    }
}
