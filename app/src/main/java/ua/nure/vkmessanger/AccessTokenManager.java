package ua.nure.vkmessanger;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Класс обертка над accessToken.
 */
public class AccessTokenManager {

    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";

    public static void setAccessToken(Context context, String token){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(ACCESS_TOKEN, token);
        editor.apply();
    }

    public static String getAccessToken(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(ACCESS_TOKEN, null);
    }
}
