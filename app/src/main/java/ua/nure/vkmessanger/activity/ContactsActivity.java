package ua.nure.vkmessanger.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import ua.nure.vkmessanger.R;

public class ContactsActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_about_us;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        initToolbar();
    }

    public static void newIntent(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, ContactsActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.navigation_item_abous_devs);

        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}