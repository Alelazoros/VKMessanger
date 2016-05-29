package ua.nure.vkmessanger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.mikepenz.materialdrawer.Drawer;
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
import ua.nure.vkmessanger.http.model.RequestResult;
import ua.nure.vkmessanger.http.model.loader.BaseLoader;
import ua.nure.vkmessanger.http.retrofit.RESTRetrofitManager;
import ua.nure.vkmessanger.model.UserDialog;
import ua.nure.vkmessanger.util.CollectionsUtils;
import ua.nure.vkmessanger.util.SharedPreferencesUtils;
import ua.nure.vkmessanger.view.NavigationDrawer;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<CustomResponse> {

    /**
     * Интервал запросов обновлений диалогов в миллисекундах.
     */
    private static final int UPDATE_INTERVAL_MILLIS = 5000;

    private final RESTInterface restInterface = new RESTRetrofitManager(this);

    private final List<UserDialog> dialogs = new ArrayList<>();

    private MainAdapter adapter;

    private Handler handler = new Handler();

    /**
     * Константа, используемая в LoaderCallbacks для идентификации Loader-а.
     */
    private static final int LOAD_USER_DIALOGS = 1;
    private static final int SET_USER_OFFLINE = 2;
    private static final int SET_USER_ONLINE = 3;

    /**
     * Константа, передаваемая как requestCode для перехода на FriendsActivity.
     */
    private static final int OPEN_DIALOG_BY_FAB = 1;

    private Drawer drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initFAB();
        initDialogsList();
        login();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = new NavigationDrawer(this, toolbar).getMaterialDrawer();
    }

    private void initFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FriendsActivity.newIntent(MainActivity.this, OPEN_DIALOG_BY_FAB, FriendsActivity.OPEN_DIALOG);
                }
            });
        }
    }

    private void initDialogsList() {
        adapter = new MainAdapter(this, null, new MainAdapter.OnDialogClickListener() {
            @Override
            public void onDialogClick(int position) {
                UserDialog dialog = adapter.getItem(position);
                SelectedDialogActivity.newIntent(MainActivity.this, dialog);
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
            //Проверка режима-невидимки.
            if (!SharedPreferencesUtils.isInvisibleModeOn(this)) {
                setUserOnline();
            }
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
            drawer.setSelection(NavigationDrawer.ITEM_DIALOGS);
        }
    }

    private void loadUserDialogs() {
        getSupportLoaderManager().restartLoader(LOAD_USER_DIALOGS, null, this);
    }

    private void loadDialogsWithTimeout() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadUserDialogs();
            }
        }, UPDATE_INTERVAL_MILLIS);
    }

    private void setUserOnline() {
        getSupportLoaderManager().restartLoader(SET_USER_ONLINE, null, this);
    }

    private void setUserOffline() {
        getSupportLoaderManager().restartLoader(SET_USER_OFFLINE, null, this);
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
                    case SET_USER_OFFLINE:
                        return restInterface.setOffline();
                    case SET_USER_ONLINE:
                        return restInterface.setOnline(false);
                    default:
                        return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<CustomResponse> loader, CustomResponse data) {

        if (data.getRequestResult() == RequestResult.SUCCESS) {

            switch (loader.getId()) {
                case LOAD_USER_DIALOGS:
                    dialogs.clear();
                    dialogs.addAll(data.<List<UserDialog>>getTypedAnswer());

                    //В адаптер передаю поверхностную копию списка диалогов.
                    List<UserDialog> copyDialogs = CollectionsUtils.copyOf(dialogs);
                    adapter.setDialogs(copyDialogs);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
        //Затем запускаю обновление диалога с определенным интервалом.
        loadDialogsWithTimeout();
    }

    @Override
    public void onLoaderReset(Loader<CustomResponse> loader) { }
}