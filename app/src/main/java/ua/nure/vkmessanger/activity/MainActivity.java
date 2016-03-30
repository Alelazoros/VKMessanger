package ua.nure.vkmessanger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ua.nure.vkmessanger.R;
import ua.nure.vkmessanger.model.UserDialog;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "APP_LOG_TAG";

    private static final int USER_DIALOGS_REQUEST_COUNT = 100;

    private final List<UserDialog> dialogs = new ArrayList<>();

    private ArrayAdapter<UserDialog> adapter;

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
                Intent intent = new Intent(MainActivity.this, SelectedDialogActivity.class);

                //Проверка на статус диалога (групповая беседа, или ЛС).
                UserDialog dialog = adapter.getItem(position);

                intent.putExtra(SelectedDialogActivity.EXTRA_SELECTED_DIALOG_ID, getDialogId(dialog));
                startActivity(intent);
                //TODO: сделать обработку возврата из SelectedDialogActivity.
            }

            private int getDialogId(UserDialog selectedDialog){
                int chatId = selectedDialog.getChatId();
                return chatId > 0 ? UserDialog.CHAT_PREFIX + chatId : selectedDialog.getUserId();
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
        VKRequest currentRequest = VKApi.messages().getDialogs(VKParameters.from(VKApiConst.COUNT, USER_DIALOGS_REQUEST_COUNT));
        currentRequest.attempts = 10;

        currentRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                Log.d(LOG_TAG, "onComplete " + response);

                dialogs.clear();
                try {
                    JSONArray responseMessagesArrayJSON = response.json.getJSONObject("response").getJSONArray("items");

                    for (int i = 0; i < responseMessagesArrayJSON.length(); i++) {
                        JSONObject messageJSON = responseMessagesArrayJSON.getJSONObject(i).getJSONObject("message");
                        dialogs.add(new UserDialog(
                                messageJSON.optInt("chat_id"),
                                messageJSON.getInt("user_id"),
                                messageJSON.getString("body")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(LOG_TAG, String.format("Dialogs count == %d", dialogs.size()));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
                Log.d(LOG_TAG, "attemptFailed " + request + " " + attemptNumber + " " + totalAttempts);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                Log.d(LOG_TAG, "onError: " + error);
            }

            @Override
            public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                super.onProgress(progressType, bytesLoaded, bytesTotal);
                Log.d(LOG_TAG, "onProgress " + progressType + " " + bytesLoaded + " " + bytesTotal);
            }
        });
    }
}