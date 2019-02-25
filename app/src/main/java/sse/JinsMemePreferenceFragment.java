package sse;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.jins_meme.visualizing_blinks_and_6axis.R;

public class JinsMemePreferenceFragment extends PreferenceFragment{

    public JinsMemePreferenceFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }

}
