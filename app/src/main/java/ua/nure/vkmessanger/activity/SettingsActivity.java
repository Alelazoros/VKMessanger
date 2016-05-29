package ua.nure.vkmessanger.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import ua.nure.vkmessanger.R;
import ua.nure.vkmessanger.fragment.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_settings;

    public static final String KEY_INVISIBLE_MODE = "key_invisible_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        initToolbar();
        initPreferenceFragment();
    }

    public static void newIntent(Activity activity, int requestCode){
        Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_settings);

        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initPreferenceFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container_settings, new SettingsFragment())
                .commit();
    }
}
