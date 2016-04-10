package ua.nure.vkmessanger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ua.nure.vkmessanger.R;
import ua.nure.vkmessanger.adapter.SelectedDialogRecyclerAdapter;
import ua.nure.vkmessanger.http.RESTInterface;
import ua.nure.vkmessanger.http.model.CustomResponse;
import ua.nure.vkmessanger.http.model.RequestResult;
import ua.nure.vkmessanger.http.model.loader.BaseLoader;
import ua.nure.vkmessanger.http.retrofit.RESTRetrofitManager;
import ua.nure.vkmessanger.model.Message;

public class SelectedDialogActivity extends AppCompatActivity
        implements SelectedDialogRecyclerAdapter.OnDialogEndListener, LoaderManager.LoaderCallbacks<CustomResponse> {

    public static final String EXTRA_SELECTED_DIALOG_ID = "EXTRA_SELECTED_DIALOG_ID";

    /**
     * Константа, передаваемая в Bundle Loader-а при подгрузке истории сообщений.
     */
    public static final String OFFSET_LOADER_BUNDLE_ARGUMENT = "OFFSET_LOADER_BUNDLE_ARGUMENT";
    /**
     * Константа, передаваемая в Bundle Loader-а при отправке сообщения.
     */
    public static final String MESSAGE_LOADER_BUNDLE_ARGUMENT = "MESSAGE_LOADER_BUNDLE_ARGUMENT";

    /**
     * LOAD_FIRST_MESSAGES, LOAD_MORE_MESSAGES, SEND_MESSAGE - константы, используемые в LoaderCallbacks для идентификации Loader-ов.
     */
    public static final int LOAD_FIRST_MESSAGES = 1;

    public static final int LOAD_MORE_MESSAGES = 2;

    public static final int SEND_MESSAGE = 3;

    private RESTInterface restInterface = new RESTRetrofitManager(this);

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
        initSendMessageButton();
        getSupportLoaderManager().initLoader(LOAD_FIRST_MESSAGES, null, this);
    }

    private void getDataFromIntent(Intent intent) {
        dialogId = intent.getIntExtra(EXTRA_SELECTED_DIALOG_ID, -1);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initRecyclerView() {
        adapter = new SelectedDialogRecyclerAdapter(this, messages);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewSelectedDialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        recyclerView.setAdapter(adapter);
    }

    private void initSendMessageButton() {
        Button sendButton = (Button) findViewById(R.id.btSendMessage);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }


    /**
     * Метод определен в интерфейсе SelectedDialogRecyclerAdapter.OnDialogEndListener и
     * обеспечивает подгрузку большего количества сообщений.
     */
    @Override
    public void requestMoreMessages(int offsetCount) {
        Bundle bundle = new Bundle();
        bundle.putInt(OFFSET_LOADER_BUNDLE_ARGUMENT, offsetCount);

        getSupportLoaderManager().restartLoader(LOAD_MORE_MESSAGES, bundle, this);
    }

    public void sendMessage() {
        EditText editText = (EditText) findViewById(R.id.editTextSendMessage);
        String messageText = editText.getText().toString();
        if (messageText != null) {
            Bundle args = new Bundle();
            args.putString(MESSAGE_LOADER_BUNDLE_ARGUMENT, messageText);
            editText.setText("");
            getSupportLoaderManager().restartLoader(SEND_MESSAGE, args, this);
        }
    }

    //---------------- Реализация LoaderManager.LoaderCallbacks<CustomResponse> ------------//

    @Override
    public Loader<CustomResponse> onCreateLoader(final int id, final Bundle args) {
        return new BaseLoader(this) {
            @Override
            public CustomResponse apiCall() throws IOException {
                switch (id) {
                    case LOAD_FIRST_MESSAGES:
                        return restInterface.loadSelectedDialogById(dialogId, 0);
                    case LOAD_MORE_MESSAGES:
                        int offset = args.getInt(OFFSET_LOADER_BUNDLE_ARGUMENT);
                        return restInterface.loadSelectedDialogById(dialogId, offset);
                    case SEND_MESSAGE:
                        String Message = args.getString(MESSAGE_LOADER_BUNDLE_ARGUMENT);
                        return restInterface.sendMessageTo(Message, dialogId);
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
                case LOAD_FIRST_MESSAGES:
                    messages.clear();
                    messages.addAll(data.<List<Message>>getTypedAnswer());
                    adapter.notifyDataSetChanged();
                    break;
                case LOAD_MORE_MESSAGES:
                    messages.addAll(data.<List<Message>>getTypedAnswer());
                    adapter.notifyDataSetChanged();
                    break;
                case SEND_MESSAGE:
                    messages.add(0, data.<Message>getTypedAnswer());
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<CustomResponse> loader) { }
}