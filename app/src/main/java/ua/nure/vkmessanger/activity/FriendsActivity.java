package ua.nure.vkmessanger.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ua.nure.vkmessanger.R;
import ua.nure.vkmessanger.adapter.FriendsAdapter;
import ua.nure.vkmessanger.http.RESTInterface;
import ua.nure.vkmessanger.http.model.CustomResponse;
import ua.nure.vkmessanger.http.model.RequestResult;
import ua.nure.vkmessanger.http.model.loader.BaseLoader;
import ua.nure.vkmessanger.http.retrofit.RESTRetrofitManager;
import ua.nure.vkmessanger.model.User;
import ua.nure.vkmessanger.model.UserDialog;

public class FriendsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<CustomResponse> {

    @IntDef({GET_FRIENDS, OPEN_DIALOG})
    public @interface OpenMode{
    }

    public static final int GET_FRIENDS = 1;

    public static final int OPEN_DIALOG = 2;

    public static final String EXTRA_OPEN_MODE = "EXTRA_OPEN_MODE";

    private static final int LAYOUT = R.layout.activity_friends;

    private static final int LOADER_FRIENDS = 1;

    private final RESTInterface restInterface = new RESTRetrofitManager(this);

    private final List<User> friends = new ArrayList<>();

    private FriendsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        initToolbar();
        initFriendsList();
        loadFriends();
    }

    public static void newIntent(Activity activity, int requestCode, @OpenMode int openMode) {
        Intent intent = new Intent(activity, FriendsActivity.class);
        intent.putExtra(EXTRA_OPEN_MODE, openMode);
        activity.startActivityForResult(intent, requestCode);
    }

    private void initToolbar() {
        String title = getIntent().getIntExtra(EXTRA_OPEN_MODE, GET_FRIENDS) == GET_FRIENDS ?
                getString(R.string.friends) : getString(R.string.choose_user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initFriendsList() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.friendsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FriendsAdapter(this, friends, new FriendsAdapter.OnFriendClickListener() {
            @Override
            public void onClick(int position) {
                User friend = adapter.getItem(position);
                UserDialog dialog = new UserDialog(0, friend.getId(), "", false, false);
                dialog.setBody(friend);

                //По клику на друга открывается диалог с ним.
                SelectedDialogActivity.newIntent(FriendsActivity.this, dialog);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadFriends() {
        getSupportLoaderManager().initLoader(LOADER_FRIENDS, null, this);
    }


    //--------------------------------------------------//


    @Override
    public Loader<CustomResponse> onCreateLoader(final int id, Bundle args) {
        return new BaseLoader(this) {
            @Override
            public CustomResponse apiCall() throws IOException {
                switch (id) {
                    case LOADER_FRIENDS:
                        return restInterface.loadFriends();
                    default:
                        return new CustomResponse();
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<CustomResponse> loader, CustomResponse data) {
        if (data.getRequestResult() == RequestResult.SUCCESS) {
            switch (loader.getId()) {
                case LOADER_FRIENDS:
                    friends.clear();
                    friends.addAll(data.<List<User>>getTypedAnswer());
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<CustomResponse> loader) { }
}