package ua.nure.vkmessanger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ua.nure.vkmessanger.R;
import ua.nure.vkmessanger.adapter.MainAdapter;
import ua.nure.vkmessanger.http.RESTInterface;
import ua.nure.vkmessanger.http.model.CustomResponse;
import ua.nure.vkmessanger.http.model.loader.BaseLoader;
import ua.nure.vkmessanger.http.retrofit.RESTRetrofitManager;
import ua.nure.vkmessanger.model.Chat;
import ua.nure.vkmessanger.model.User;
import ua.nure.vkmessanger.model.UserDialog;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<CustomResponse> {

    private final RESTInterface restInterface = new RESTRetrofitManager(this);

    private final List<UserDialog> dialogs = new ArrayList<>();

    private final List<User> users = new ArrayList<>();

    private final List<Chat> chats = new ArrayList<>();

    private MainAdapter adapter;

    /**
     * Константа, используемая в LoaderCallbacks для идентификации Loader-а.
     */
    private static final int LOAD_USER_DIALOGS = 1;

    private static final int LOAD_USERS = 2;

    private static final int LOAD_CHATS = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initFAB();
        initListView();
        login();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }
    }

    private void initListView() {
        adapter = new MainAdapter(this, null, new MainAdapter.OnDialogClickListener() {
            @Override
            public void onDialogClick(int position) {
                UserDialog dialog = dialogs.get(position);
                SelectedDialogActivity.newIntent(MainActivity.this, dialog.getDialogId());
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.dialogRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void login() {
        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VKSdk.login(MainActivity.this, VKScope.FRIENDS, VKScope.MESSAGES);
            }
        });
        if (VKSdk.wakeUpSession(this)) {
            loadUserDialogs();
        } else {
            loginButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                //Успешная авторизация.
                Button loginButton = (Button) findViewById(R.id.login_button);
                loginButton.setVisibility(View.GONE);

                loadUserDialogs();
            }

            @Override
            public void onError(VKError error) {
                //Ошибка авторизации.
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void loadUserDialogs() {
        getSupportLoaderManager().restartLoader(LOAD_USER_DIALOGS, null, this);
    }

    private void loadUsers() {
        getSupportLoaderManager().restartLoader(LOAD_USERS, null, this);
    }

    private void loadChats() {
        getSupportLoaderManager().restartLoader(LOAD_CHATS, null, this);
    }

    private void mergeUsersWithChats() {
        int indexChat = 0;
        int indexUser = 0;
        for (UserDialog dialog : dialogs) {

            if (dialog.isSingle()) {
                dialog.setBody(users.get(indexUser++));
            } else {
                dialog.setBody(chats.get(indexChat++));
            }
        }
        adapter.setDialogs(dialogs);
        adapter.notifyDataSetChanged();
    }

    //---------------- Реализация LoaderManager.LoaderCallbacks<CustomResponse> ------------//

    @Override
    public Loader<CustomResponse> onCreateLoader(final int id, Bundle args) {
        return new BaseLoader(this) {
            @Override
            public CustomResponse apiCall() throws IOException {
                switch (id) {
                    case LOAD_USER_DIALOGS:
                        return restInterface.loadUserDialogs();
                    case LOAD_USERS:
                        return restInterface.loadUsers(dialogs);
                    case LOAD_CHATS:
                        return restInterface.loadChats(dialogs);
                    default:
                        return null;
                }
            }
        };
    }

    private boolean usersOrChatsLoaded = false;

    @Override
    public void onLoadFinished(Loader<CustomResponse> loader, CustomResponse data) {
        switch (loader.getId()) {
            case LOAD_USER_DIALOGS:
                dialogs.clear();
                dialogs.addAll(data.<List<UserDialog>>getTypedAnswer());
                loadUsers();
                loadChats();
                break;
            case LOAD_USERS:
                users.clear();
                users.addAll(data.<List<User>>getTypedAnswer());
                if (usersOrChatsLoaded) {
                    mergeUsersWithChats();
                    usersOrChatsLoaded = false;
                } else {
                    usersOrChatsLoaded = true;
                }
                break;
            case LOAD_CHATS:
                chats.clear();
                chats.addAll(data.<List<Chat>>getTypedAnswer());
                if (usersOrChatsLoaded) {
                    mergeUsersWithChats();
                    usersOrChatsLoaded = false;
                } else {
                    usersOrChatsLoaded = true;
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<CustomResponse> loader) { }
}