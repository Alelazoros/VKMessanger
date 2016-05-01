package ua.nure.vkmessanger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import ua.nure.vkmessanger.http.RESTInterface;
import ua.nure.vkmessanger.http.model.CustomResponse;
import ua.nure.vkmessanger.http.model.loader.BaseLoader;
import ua.nure.vkmessanger.http.retrofit.RESTRetrofitManager;
import ua.nure.vkmessanger.model.UserDialog;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<CustomResponse>{

    private RESTInterface restInterface = new RESTRetrofitManager(this);

    private final List<UserDialog> dialogs = new ArrayList<>();

    private ArrayAdapter<UserDialog> adapter;

    /**
     * Константа, используемая в LoaderCallbacks для идентификации Loader-а.
     */
    private static final int LOAD_USER_DIALOGS = 1;

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
        adapter = new ArrayAdapter<UserDialog>(this, android.R.layout.simple_list_item_1, android.R.id.text1, dialogs) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final UserDialog dialog = getItem(position);
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext())
                            .inflate(android.R.layout.simple_list_item_1, null);
                }
                ((TextView) convertView.findViewById(android.R.id.text1))
                        .setText(String.format("user: %d \nmessage:%s", dialog.getUserId(), dialog.getLastMessage()));

                return convertView;
            }
        };
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Проверка на статус диалога (групповая беседа, или ЛС) в методе dialog.getDialogId().
                UserDialog dialog = adapter.getItem(position);

                SelectedDialogActivity.newIntent(MainActivity.this, dialog.getDialogId());

                //TODO: сделать обработку возврата из SelectedDialogActivity.
            }
        });
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
                loginButton.setVisibility(View.INVISIBLE);

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



    //---------------- Реализация LoaderManager.LoaderCallbacks<CustomResponse> ------------//

    @Override
    public Loader<CustomResponse> onCreateLoader(final int id, Bundle args) {
        return new BaseLoader(this) {
            @Override
            public CustomResponse apiCall() throws IOException {
                switch (id){
                    case LOAD_USER_DIALOGS:
                        return restInterface.loadUserDialogs();
                    default:
                        return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<CustomResponse> loader, CustomResponse data) {
        switch (loader.getId()){
            case LOAD_USER_DIALOGS:
                dialogs.clear();
                dialogs.addAll(data.<List<UserDialog>>getTypedAnswer());
                adapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<CustomResponse> loader) { }
}