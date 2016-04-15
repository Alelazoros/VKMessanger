package ua.nure.vkmessanger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.List;

import ua.nure.vkmessanger.R;
import ua.nure.vkmessanger.http.RESTInterface;
import ua.nure.vkmessanger.http.model.CustomResponse;
import ua.nure.vkmessanger.http.model.RequestResult;
import ua.nure.vkmessanger.http.model.loader.BaseLoader;
import ua.nure.vkmessanger.http.retrofit.RESTRetrofitManager;
import ua.nure.vkmessanger.model.Group;
import ua.nure.vkmessanger.model.WallPost;

public class WallPostActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<CustomResponse> {

    public static final String EXTRA_WALL_POST = "EXTRA_WALL_POST";

    private static final String GROUPS_LOADER_BUNDLE_ARGUMENT = "GROUPS_LOADER_BUNDLE_ARGUMENT";

    private static final int LOAD_GROUPS = 1;

    private RESTInterface mRESTInterface = new RESTRetrofitManager(this);

    private WallPost mWallPost;

    private List<Group> mGroups;

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
        Log.d("CLICKED WALL POST", mWallPost.toString());

        loadGroupsInfo(mWallPost);
    }

    private void loadGroupsInfo(WallPost wallPost) {
        Bundle args = new Bundle();
        //TODO: получить все группы.
        String[] groups = {String.valueOf(Math.abs(wallPost.getWallAuthorId()))};
        args.putStringArray(GROUPS_LOADER_BUNDLE_ARGUMENT, groups);

        getSupportLoaderManager().initLoader(LOAD_GROUPS, args, this);
    }


    //---------------------------------------------------//

    @Override
    public Loader<CustomResponse> onCreateLoader(final int id, final Bundle args) {
        return new BaseLoader(this) {
            @Override
            public CustomResponse apiCall() throws IOException {
                switch (id) {
                    case LOAD_GROUPS:
                        return mRESTInterface.getGroupsInfoByIds(args.getStringArray(GROUPS_LOADER_BUNDLE_ARGUMENT));
                    default:
                        return new CustomResponse();
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<CustomResponse> loader, CustomResponse data) {
        if (data.getRequestResult() == RequestResult.SUCCESS){
            this.mGroups = data.getTypedAnswer();
            Log.d("GROUP DATE LOADED", mGroups.get(0).toString());
        }
    }

    @Override
    public void onLoaderReset(Loader<CustomResponse> loader) { }
}
