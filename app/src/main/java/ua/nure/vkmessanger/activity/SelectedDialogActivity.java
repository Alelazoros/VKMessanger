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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiGetMessagesResponse;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ua.nure.vkmessanger.R;
import ua.nure.vkmessanger.model.Message;

public class SelectedDialogActivity extends AppCompatActivity {

    public static final String SELECTED_DIALOG_USER_ID = "SELECTED_DIALOG_USER_ID";

    private static final String LOG_TAG = "LOG_TAG_DIALOGS";

    private static final int DIALOG_MESSAGES_REQUEST_COUNT_BY_DEFAULT = 50;
    private static final int MESSAGE_WAS_READ = 1;

    private List<Message> messages = new ArrayList<>();

    private ArrayAdapter<Message> adapter;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_dialog);

        getDataFromIntent(getIntent());
        initToolbar();
        initFAB();
        initListView();
        loadDialogWithSelectedUser(userId);
    }

    private void getDataFromIntent(Intent intent) {
        userId = intent.getIntExtra(SELECTED_DIALOG_USER_ID, -1);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initListView() {
        adapter = new ArrayAdapter<Message>(this, android.R.layout.simple_list_item_1, android.R.id.text1, messages) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final Message message = getItem(position);
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext())
                            .inflate(android.R.layout.simple_list_item_1, null);
                }
                ((TextView) convertView.findViewById(android.R.id.text1)).setText(message.getMessageBody());

                return convertView;
            }
        };
        ListView listView = (ListView) findViewById(R.id.listViewDialog);
        listView.setAdapter(adapter);
    }


    private void loadDialogWithSelectedUser(int userId){
        VKRequest currentRequest = new VKRequest("messages.getHistory",
                VKParameters.from(VKApiConst.USER_ID, userId, VKApiConst.COUNT, DIALOG_MESSAGES_REQUEST_COUNT_BY_DEFAULT));
        currentRequest.attempts = 10;

        currentRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                Log.d(LOG_TAG, "onComplete " + response);

                messages.clear();
                try {
                    JSONArray jsonArray = (JSONArray)response.json.getJSONObject("response").get("items");

                    Log.d(LOG_TAG, " json array length = " + jsonArray.length());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);

                        int messageId = object.getInt("id");
                        boolean isMessageFromMe = object.getInt("from_id") != object.getInt("user_id");
                        boolean isRead = object.getInt("read_state") == MESSAGE_WAS_READ;
                        String messageBody = object.getString("body");

                        messages.add(new Message(messageId, isMessageFromMe, isRead, messageBody));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(LOG_TAG, String.format("Messages loaded count == %d", messages.size()));
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