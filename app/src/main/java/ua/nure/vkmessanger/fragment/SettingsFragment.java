package ua.nure.vkmessanger.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import ua.nure.vkmessanger.R;

/**
 * Created by Antony on 5/29/2016.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
