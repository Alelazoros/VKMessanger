package ua.nure.vkmessanger.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ua.nure.vkmessanger.activity.SettingsActivity;

/**
 * Created by Antony on 5/29/2016.
 */
public class SharedPreferencesUtils {

    public static boolean isInvisibleModeOn(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(SettingsActivity.KEY_INVISIBLE_MODE, false);
    }

}
