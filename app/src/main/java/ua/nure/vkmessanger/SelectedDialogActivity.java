package ua.nure.vkmessanger;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ua.nure.vkmessanger.model.Message;
import ua.nure.vkmessanger.model.UserDialog;

public class SelectedDialogActivity extends AppCompatActivity {

    public static final String SELECTED_DIALOG_USER_ID = "SELECTED_DIALOG_USER_ID";

    private static final int DIALOG_MESSAGES_REQUEST_COUNT = 50;

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
        //TODO: реализовать загрузку.
    }
}
