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


    public SelectedDialogRecyclerAdapter(Context context, List<Message> messageList) {
        this.mInflater = LayoutInflater.from(context);
        this.mMessageList = messageList;
        this.mDialogEndListener = (OnDialogEndListener) context;
        setHasStableIds(true);
    }

    public void changeMessagesList(List<Message> messages) {
        this.mMessageList = messages;
    }

    @Override
    public int getItemViewType(int position) {
        if (mMessageList.get(position).isFromMe()){
            return mMessageList.get(position).getAttachments() == null ? MESSAGE_FROM_USER_TYPE : WALL_POST_FROM_USER_TYPE;
        }else {
            return mMessageList.get(position).getAttachments() == null ? MESSAGE_TO_USER_TYPE : WALL_POST_TO_USER_TYPE;
        }
    }

    @Override
    public SelectedDialogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(viewType, parent, false);
        return new SelectedDialogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectedDialogViewHolder holder, int position) {
        if (mMessageList != null) {
            holder.messageTV.setText(mMessageList.get(position).getMessageBody());

            Attachment[] attachments = mMessageList.get(position).getAttachments();

            //TODO: пока что обрабатываю только записи на стене.
            if (attachments != null && attachments[0] != null && attachments[0].getType().equals(Attachment.TYPE_WALL_POST)){
                holder.attachmentWallPostTitleTV.setText(((WallPost)attachments[0].getBody()).getText());
            }

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


    static class SelectedDialogViewHolder extends RecyclerView.ViewHolder {

        private TextView messageTV;

        private TextView attachmentWallPostTitleTV;

        public SelectedDialogViewHolder(View itemView) {
            super(itemView);
            messageTV = (TextView) itemView.findViewById(R.id.messageTextView);
            attachmentWallPostTitleTV = (TextView) itemView.findViewById(R.id.messageAttachmentWallPostTV);
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
}