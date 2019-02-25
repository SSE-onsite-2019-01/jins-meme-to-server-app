package com.jins_meme.visualizing_blinks_and_6axis;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import sse.JinsMemePreferenceFragment;

public class SettingActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new JinsMemePreferenceFragment())
                .commit();
    }
}
