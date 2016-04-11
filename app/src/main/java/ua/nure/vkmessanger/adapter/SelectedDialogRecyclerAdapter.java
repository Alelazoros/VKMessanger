package ua.nure.vkmessanger.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ua.nure.vkmessanger.R;
import ua.nure.vkmessanger.model.Attachment;
import ua.nure.vkmessanger.model.Message;
import ua.nure.vkmessanger.model.WallPost;

/**
 * Адаптер для списка сообщений в выбранном диалоге.
 */
public class SelectedDialogRecyclerAdapter extends RecyclerView.Adapter<SelectedDialogRecyclerAdapter.SelectedDialogViewHolder> {

    private static final int MESSAGE_FROM_USER_TYPE = R.layout.dialog_message_from_user_layout;
    private static final int MESSAGE_TO_USER_TYPE = R.layout.dialog_message_to_user_layout;

    private static final int WALL_POST_FROM_USER_TYPE = R.layout.dialog_message_wall_post_from_user;
    private static final int WALL_POST_TO_USER_TYPE = R.layout.dialog_message_wall_post_to_user;

    private LayoutInflater mInflater;

    private List<Message> mMessageList;

    private OnDialogEndListener mDialogEndListener;

    private OnMessageClickListener mClickListener;


    public SelectedDialogRecyclerAdapter(Context context, List<Message> messageList, OnMessageClickListener listener) {
        this.mInflater = LayoutInflater.from(context);
        this.mMessageList = messageList;
        this.mDialogEndListener = (OnDialogEndListener) context;
        this.mClickListener = listener;
        setHasStableIds(true);
    }

    public void changeMessagesList(List<Message> messages) {
        this.mMessageList = messages;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessageList.get(position);
        Attachment[] attachments = message.getAttachments();

        boolean isWallPost = false;
        if (attachments != null && attachments[0] != null && attachments[0].getType().equals(Attachment.TYPE_WALL_POST)) {
            isWallPost = true;
        }

        if (message.isFromMe()) {
            return attachments != null && isWallPost ? WALL_POST_FROM_USER_TYPE : MESSAGE_FROM_USER_TYPE;
        } else {
            return attachments != null && isWallPost ? WALL_POST_TO_USER_TYPE : MESSAGE_TO_USER_TYPE;
        }
    }

    @Override
    public SelectedDialogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(viewType, parent, false);
        return new SelectedDialogViewHolder(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(SelectedDialogViewHolder holder, int position) {
        if (mMessageList != null) {
            holder.bind(mMessageList.get(position));

            if (position == getItemCount() - 1) {
                //Если пользователь доскроллил до конца RecyclerView, то надо подгрузить еще сообщения.
                mDialogEndListener.requestMoreMessages(getItemCount());
            }
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


    static class SelectedDialogViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private TextView messageTV;

        private TextView attachmentWallPostTitleTV;

        private OnMessageClickListener mClickListener;

        public SelectedDialogViewHolder(View itemView, OnMessageClickListener listener) {
            super(itemView);
            messageTV = (TextView) itemView.findViewById(R.id.messageTextView);
            attachmentWallPostTitleTV = (TextView) itemView.findViewById(R.id.messageAttachmentWallPostTV);
            mClickListener = listener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(Message message) {
            messageTV.setVisibility(message.getMessageBody().equals("") ? View.GONE : View.VISIBLE);
            messageTV.setText(message.getMessageBody());
            Attachment[] attachments = message.getAttachments();

            //TODO: пока что обрабатываю только записи на стене.
            if (attachments != null && attachments[0] != null && attachments[0].getType().equals(Attachment.TYPE_WALL_POST)) {
                attachmentWallPostTitleTV.setText(((WallPost) attachments[0].getBody()).getText());
            }
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.onItemClick(getLayoutPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return mClickListener != null && mClickListener.onItemLongClick(getLayoutPosition());
        }
    }


    /**
     * Интерфейс для получения новых сообщений, когда пользователь
     * проскроллил все загруженные на данный момент сообщения.
     */
    public interface OnDialogEndListener {
        /**
         * @param offsetCount равен количеству элементов, которые в данный момент
         *                    уже есть в RecyclerView.
         */
        void requestMoreMessages(int offsetCount);
    }

    public interface OnMessageClickListener {

        void onItemClick(int position);

        /**
         * Длительный клик должен использоваться для того, чтобы переслать или удалить сообщения.
         */
        boolean onItemLongClick(int position);
    }
}