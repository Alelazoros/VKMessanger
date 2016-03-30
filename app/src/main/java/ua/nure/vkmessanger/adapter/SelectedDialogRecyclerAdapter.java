package ua.nure.vkmessanger.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ua.nure.vkmessanger.R;
import ua.nure.vkmessanger.model.Message;

/**
 * Адаптер для списка сообщений в выбранном диалоге.
 */
public class SelectedDialogRecyclerAdapter extends RecyclerView.Adapter<SelectedDialogRecyclerAdapter.SelectedDialogViewHolder> {

    private static final int MESSAGE_FROM_USER_TYPE = 1;
    private static final int MESSAGE_TO_USER_TYPE = 2;

    private LayoutInflater mInflater;

    private List<Message> mMessageList;


    public SelectedDialogRecyclerAdapter(Context context, List<Message> messageList) {
        this.mInflater = LayoutInflater.from(context);
        this.mMessageList = messageList;
        setHasStableIds(true);
    }

    public void changeMessagesList(List<Message> messages) {
        this.mMessageList = messages;
    }

    @Override
    public int getItemViewType(int position) {
        return mMessageList.get(position).isFromMe() ? MESSAGE_FROM_USER_TYPE : MESSAGE_TO_USER_TYPE;
    }

    @Override
    public SelectedDialogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = viewType == MESSAGE_FROM_USER_TYPE ?
                R.layout.dialog_message_from_user_layout : R.layout.dialog_message_to_user_layout;

        View view = mInflater.inflate(layout, parent, false);
        return new SelectedDialogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectedDialogViewHolder holder, int position) {
        if (mMessageList != null) {
            holder.messageTextView.setText(mMessageList.get(position).getMessageBody());
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList == null ? 0 : mMessageList.size();
    }

    @Override
    public long getItemId(int position) {
        return mMessageList == null ? 0 : mMessageList.get(position).getMessageId();
    }

    static class SelectedDialogViewHolder extends RecyclerView.ViewHolder{

        private TextView messageTextView;

        public SelectedDialogViewHolder(View itemView) {
            super(itemView);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
        }
    }

}
