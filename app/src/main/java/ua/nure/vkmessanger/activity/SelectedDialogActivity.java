package ua.nure.vkmessanger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import ua.nure.vkmessanger.R;
import ua.nure.vkmessanger.adapter.SelectedDialogRecyclerAdapter;
import ua.nure.vkmessanger.http.RESTInterface;
import ua.nure.vkmessanger.http.RESTVkSdkManager;
import ua.nure.vkmessanger.http.ResponseCallback;
import ua.nure.vkmessanger.model.Message;

public class SelectedDialogActivity extends AppCompatActivity implements SelectedDialogRecyclerAdapter.OnDialogEndListener {

    public static final String EXTRA_SELECTED_DIALOG_ID = "EXTRA_SELECTED_DIALOG_ID";

    private RESTInterface restInterface = new RESTVkSdkManager();

    private List<Message> messages = new ArrayList<>();

    private SelectedDialogRecyclerAdapter adapter;

    private int dialogId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_dialog);

        getDataFromIntent(getIntent());
        initToolbar();
        initRecyclerView();
        loadDialogWithSelectedUser(dialogId);
    }

    private void getDataFromIntent(Intent intent) {
        dialogId = intent.getIntExtra(EXTRA_SELECTED_DIALOG_ID, -1);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initRecyclerView() {
        adapter = new SelectedDialogRecyclerAdapter(this, null);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewSelectedDialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        recyclerView.setAdapter(adapter);
    }


    private void loadDialogWithSelectedUser(int dialogId){
        restInterface.loadSelectedDialogById(dialogId, 0, new ResponseCallback<Message>() {
            @Override
            public void onResponse(List<Message> data) {
                messages.clear();
                messages.addAll(data);
                adapter.changeMessagesList(messages);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Метод определен в интерфейсе SelectedDialogRecyclerAdapter.OnDialogEndListener и
     * обеспечивает подгрузку большего количества сообщений.
     */
    @Override
    public void requestMoreMessages(int offsetCount) {
        restInterface.loadSelectedDialogById(dialogId, offsetCount, new ResponseCallback<Message>() {
            @Override
            public void onResponse(List<Message> data) {
                messages.addAll(data);
                adapter.notifyDataSetChanged();
            }
        });
    }
}