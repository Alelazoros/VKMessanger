package ua.nure.vkmessanger.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ua.nure.vkmessanger.R;
import ua.nure.vkmessanger.adapter.DialogAdapter;
import ua.nure.vkmessanger.http.RESTInterface;
import ua.nure.vkmessanger.http.model.CustomResponse;
import ua.nure.vkmessanger.http.model.RequestResult;
import ua.nure.vkmessanger.http.model.loader.BaseLoader;
import ua.nure.vkmessanger.http.retrofit.RESTRetrofitManager;
import ua.nure.vkmessanger.model.Attachment;
import ua.nure.vkmessanger.model.Chat;
import ua.nure.vkmessanger.model.Link;
import ua.nure.vkmessanger.model.Message;
import ua.nure.vkmessanger.model.User;
import ua.nure.vkmessanger.model.UserDialog;
import ua.nure.vkmessanger.model.WallPost;
import ua.nure.vkmessanger.util.CollectionsUtils;
import ua.nure.vkmessanger.util.SharedPreferencesUtils;

public class SelectedDialogActivity extends AppCompatActivity
        implements DialogAdapter.OnDialogEndListener, LoaderManager.LoaderCallbacks<CustomResponse> {

    /**
     * Интервал, с которым производится обновление диалога.
     */
    private static final int UPDATE_MESSAGES_TIMEOUT_MILLISECONDS = 5000;


    private static final String EXTRA_SELECTED_DIALOG = "EXTRA_SELECTED_DIALOG";

    /**
     * Константа, передаваемая в Bundle Loader-а при подгрузке истории сообщений.
     */
    public static final String OFFSET_LOADER_BUNDLE_ARGUMENT = "OFFSET_LOADER_BUNDLE_ARGUMENT";
    /**
     * Константа, передаваемая в Bundle Loader-а при отправке сообщения.
     */
    private static final String MESSAGE_LOADER_BUNDLE_ARGUMENT = "MESSAGE_LOADER_BUNDLE_ARGUMENT";
    /**
     * Константа, передаваемая в Bundle Loader-а при чтении сообщений.
     */
    private static final String MARK_READ_LOADER_BUNDLE_ARGUMENT = "MARK_READ_LOADER_BUNDLE_ARGUMENT";


    /**
     * FIRST_MESSAGES_LOADER, MORE_MESSAGES_LOADER, SEND_MESSAGE_LOADER, UPDATE_DIALOG_MESSAGES_LOADER, MARK_AS_READ_LOADER - константы,
     * используемые в LoaderCallbacks для идентификации Loader-ов.
     */
    private static final int FIRST_MESSAGES_LOADER = 1;

    private static final int MORE_MESSAGES_LOADER = 2;

    private static final int SEND_MESSAGE_LOADER = 3;

    private static final int UPDATE_DIALOG_MESSAGES_LOADER = 4;

    private static final int MARK_AS_READ_LOADER = 5;


    private RESTInterface restInterface = new RESTRetrofitManager(this);

    private List<Message> messages = new ArrayList<>();

    private DialogAdapter adapter;

    private UserDialog dialog;

    private Handler handler = new Handler();

    private boolean isInvisibleMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_dialog);

        isInvisibleMode = SharedPreferencesUtils.isInvisibleModeOn(this);
        getDataFromIntent(getIntent());
        initToolbar();
        initRecyclerView();
        initSendMessageButton();
        loadFirstMessages();
    }

    public static void newIntent(Context context, UserDialog dialog) {
        Intent intent = new Intent(context, SelectedDialogActivity.class);
        intent.putExtra(SelectedDialogActivity.EXTRA_SELECTED_DIALOG, dialog);
        context.startActivity(intent);
    }

    private void getDataFromIntent(Intent intent) {
        dialog = (UserDialog) intent.getExtras().get(EXTRA_SELECTED_DIALOG);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getDialogTitle(dialog));
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String getDialogTitle(UserDialog userDialog) {
        String title;
        if (userDialog.isSingle()) {
            User user = (User) userDialog.getBody();
            title = String.format("%s %s", user.getFirstName(), user.getLastName());
        } else {
            Chat chat = (Chat) userDialog.getBody();
            title = chat.getChatName();
        }
        return title;
    }

    private void initRecyclerView() {
        adapter = new DialogAdapter(this, dialog, null, mMessageClickListener, this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewSelectedDialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        recyclerView.setAdapter(adapter);
    }

    private DialogAdapter.OnMessageClickListener mMessageClickListener =
            new DialogAdapter.OnMessageClickListener() {

                @Override
                public void onItemClick(int position) {
                    Message clickedMessage = messages.get(position);
                    Attachment[] attachments = clickedMessage.getAttachments();

                    if (attachments == null || attachments[0] == null) {
                        return;
                    }
                    if (attachments[0].isWallPost()) {
                        WallPost clickedPost = (WallPost) attachments[0].getBody();
                        WallPostActivity.newIntent(SelectedDialogActivity.this, clickedPost);

                    } else if (attachments[0].isLink()) {
                        Link clickedLink = (Link) attachments[0].getBody();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(clickedLink.getURL()));
                        startActivity(intent);
                    }
                }

                @Override
                public boolean onItemLongClick(int position) {
                    return true;
                }
            };

    private void initSendMessageButton() {
        ImageButton sendButton = (ImageButton) findViewById(R.id.btSendMessage);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void loadFirstMessages() {
        getSupportLoaderManager().initLoader(FIRST_MESSAGES_LOADER, null, this);
    }


    /**
     * Метод определен в интерфейсе DialogAdapter.OnDialogEndListener и
     * обеспечивает подгрузку большего количества сообщений.
     */
    @Override
    public void requestMoreMessages(int offsetCount) {
        Bundle bundle = new Bundle();
        bundle.putInt(OFFSET_LOADER_BUNDLE_ARGUMENT, offsetCount);

        getSupportLoaderManager().restartLoader(MORE_MESSAGES_LOADER, bundle, this);
    }

    public void sendMessage() {
        EditText editText = (EditText) findViewById(R.id.editTextSendMessage);
        String messageText = editText.getText().toString();
        if (messageText != null) {
            Bundle args = new Bundle();
            args.putString(MESSAGE_LOADER_BUNDLE_ARGUMENT, messageText);
            editText.setText("");
            getSupportLoaderManager().initLoader(SEND_MESSAGE_LOADER, args, this);
        }
    }

    //--------------------------------Update dialog messages------------------------------//

    private void updateMessages() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getSupportLoaderManager().restartLoader(UPDATE_DIALOG_MESSAGES_LOADER, null, SelectedDialogActivity.this);
            }
        }, UPDATE_MESSAGES_TIMEOUT_MILLISECONDS);
    }

    private void markMessagesAsRead(int peerId) {
        Bundle bundle = new Bundle();
        bundle.putInt(MARK_READ_LOADER_BUNDLE_ARGUMENT, peerId);
        getSupportLoaderManager().restartLoader(MARK_AS_READ_LOADER, bundle, this);
    }

    //---------------- Реализация LoaderManager.LoaderCallbacks<CustomResponse> ------------//

    private void destroyLoader(int loaderId) {
        getSupportLoaderManager().destroyLoader(loaderId);
    }

    @Override
    public Loader<CustomResponse> onCreateLoader(final int id, final Bundle args) {
        return new BaseLoader(this) {
            @Override
            public CustomResponse apiCall() throws IOException {
                switch (id) {
                    case FIRST_MESSAGES_LOADER:
                        return restInterface.loadSelectedDialogById(dialog.getDialogId(), 0);
                    case MORE_MESSAGES_LOADER:
                        int offset = args.getInt(OFFSET_LOADER_BUNDLE_ARGUMENT);
                        return restInterface.loadSelectedDialogById(dialog.getDialogId(), offset);
                    case SEND_MESSAGE_LOADER:
                        String Message = args.getString(MESSAGE_LOADER_BUNDLE_ARGUMENT);
                        return restInterface.sendMessageTo(Message, dialog.getDialogId());
                    case UPDATE_DIALOG_MESSAGES_LOADER:
                        //По умолчанию я так получаю первые 50 сообщений.
                        return restInterface.loadSelectedDialogById(dialog.getDialogId(), 0);
                    case MARK_AS_READ_LOADER:
                        int peerId = args.getInt(MARK_READ_LOADER_BUNDLE_ARGUMENT);
                        return restInterface.markMessagesAsRead(peerId);
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
                case FIRST_MESSAGES_LOADER:
                    messages.clear();
                    messages.addAll(data.<List<Message>>getTypedAnswer());
                    updateMessages();
                    break;
                case MORE_MESSAGES_LOADER:
                    messages.addAll(data.<List<Message>>getTypedAnswer());
                    destroyLoader(loader.getId());
                    break;
                case SEND_MESSAGE_LOADER:
                    messages.add(0, data.<Message>getTypedAnswer());
                    destroyLoader(loader.getId());
                    break;
                case UPDATE_DIALOG_MESSAGES_LOADER:
                    List<Message> updatedMessages = data.getTypedAnswer();
                    mergeUpdatedMessages(messages, updatedMessages);

                    //Посылаю запрос, чтоб диалог считался прочитанным, если отключен режим невидимки.
                    if (!isInvisibleMode) {
                        markMessagesAsRead(dialog.getDialogId());
                    }
                    //Снова запускаю лоадер для обновления сообщений диалога.
                    updateMessages();
                    break;
            }

            //В адаптер передаю только поверхностную копию списка сообщений.
            List<Message> copy = CollectionsUtils.copyOf(messages);
            adapter.setMessages(copy);
            adapter.notifyDataSetChanged();
        }
    }

    private void mergeUpdatedMessages(List<Message> oldMessages, List<Message> updatedMessages) {
        if (oldMessages.size() == 0) {
            return;
        }

        //Индекс и Id последнего сообщения, которое было самыс свежим для обновления.
        int lastOldMessageId = oldMessages.get(0).getMessageId();
        int lastOldMessageIndex = 0;

        for (int i = 0; i < oldMessages.size(); i++) {
            if (updatedMessages.get(i).getMessageId() == lastOldMessageId) {
                lastOldMessageIndex = i;
                break;
            }
        }
        for (int i = 0; i < lastOldMessageIndex; i++) {
            oldMessages.add(i, updatedMessages.get(i));
        }
        for (int i = lastOldMessageId; i < oldMessages.size(); i++) {
            oldMessages.set(i, updatedMessages.get(i));
        }
    }

    @Override
    public void onLoaderReset(Loader<CustomResponse> loader) { }
}