package ua.nure.vkmessanger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import ua.nure.vkmessanger.R;
import ua.nure.vkmessanger.model.WallPost;

public class WallPostActivity extends AppCompatActivity {

    public static final String EXTRA_WALL_POST = "EXTRA_WALL_POST";

    private WallPost mWallPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall_post);

        initToolbar();
        getDataFromIntent(getIntent());
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.wall_post);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getDataFromIntent(Intent intent) {
        mWallPost = (WallPost) intent.getExtras().get(EXTRA_WALL_POST);
    }

}
