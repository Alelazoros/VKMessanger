package ua.nure.vkmessanger.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ua.nure.vkmessanger.R;
import ua.nure.vkmessanger.model.Attachment;
import ua.nure.vkmessanger.model.Audio;
import ua.nure.vkmessanger.model.Link;
import ua.nure.vkmessanger.model.Message;
import ua.nure.vkmessanger.model.Photo;
import ua.nure.vkmessanger.model.WallPost;

/**
 * Адаптер для списка сообщений в выбранном диалоге.
 */
public class SelectedDialogAdapter extends RecyclerView.Adapter<SelectedDialogAdapter.SelectedDialogViewHolder> {

    private static final int MESSAGE_FROM_USER_TYPE = R.layout.dialog_message_from_user_layout;
    private static final int MESSAGE_TO_USER_TYPE = R.layout.dialog_message_to_user_layout;

    private static final int WALL_POST_FROM_USER_TYPE = R.layout.dialog_message_wall_post_from_user;
    private static final int WALL_POST_TO_USER_TYPE = R.layout.dialog_message_wall_post_to_user;

    private static final int TYPE_LINK_FROM_USER = R.layout.dialog_message_link_from_user;
    private static final int TYPE_LINK_TO_USER = R.layout.dialog_message_link_to_user;

    private static final int TYPE_AUDIO_FROM_USER = R.layout.audio_list_container_from_user_layout;
    private static final int TYPE_AUDIO_TO_USER = R.layout.audio_list_container_to_user_layout;

    private Context mContext;

    private LayoutInflater mInflater;

    private List<Message> mMessageList;

    private OnDialogEndListener mDialogEndListener;

    private OnMessageClickListener mClickListener;


    public SelectedDialogAdapter(Context context, List<Message> messageList, OnMessageClickListener listener) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mMessageList = messageList;
        mDialogEndListener = (OnDialogEndListener) context;
        mClickListener = listener;
        setHasStableIds(true);
    }

    public void changeMessagesList(List<Message> messages) {
        this.mMessageList = messages;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessageList.get(position);
        Attachment[] attachments = message.getAttachments();

        if (attachments == null || attachments[0] == null || (!attachments[0].isWallPost() && !attachments[0].isLink() && !attachments[0].isAudio())) {
            return message.isFromMe() ? MESSAGE_FROM_USER_TYPE : MESSAGE_TO_USER_TYPE;
        }

        boolean isWallPost = attachments[0].isWallPost();
        boolean isLink = attachments[0].isLink();
        boolean isAudio = attachments[0].isAudio();

        if (message.isFromMe()) {
            if (isWallPost) {
                return WALL_POST_FROM_USER_TYPE;
            } else if (isLink) {
                return TYPE_LINK_FROM_USER;
            } else if (isAudio) {
                return TYPE_AUDIO_FROM_USER;
            }
        } else {
            if (isWallPost) {
                return WALL_POST_FROM_USER_TYPE;
            } else if (isLink) {
                return TYPE_LINK_FROM_USER;
            } else if (isAudio) {
                return TYPE_AUDIO_TO_USER;
            }
        }
        return 0;
    }

    @Override
    public SelectedDialogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(viewType, parent, false);
        return new SelectedDialogViewHolder(mContext, view, viewType, mClickListener);
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

        private LayoutInflater mInflater;

        private OnMessageClickListener mClickListener;

        //-------Message text-----//

        private TextView messageTV;

        //---------Wall post------//

        private TextView attachmentWallPostTitleTV;

        //-----------Link---------//

        private TextView mLinkTitleTV;

        private TextView mLinkDescriptionTV;

        //----------Audio--------//

        private ViewGroup mAudioItemsContainer;


        public SelectedDialogViewHolder(Context context, View itemView, int viewType, OnMessageClickListener listener) {
            super(itemView);
            mInflater = LayoutInflater.from(context);
            mClickListener = listener;

            //Message text.
            messageTV = (TextView) itemView.findViewById(R.id.messageTextView);

            //Wall post.
            if (viewType == WALL_POST_FROM_USER_TYPE || viewType == WALL_POST_TO_USER_TYPE) {
                attachmentWallPostTitleTV = (TextView) itemView.findViewById(R.id.messageAttachmentWallPostTV);
            }
            //Link.
            if (viewType == TYPE_LINK_FROM_USER || viewType == TYPE_LINK_TO_USER) {
                mLinkTitleTV = (TextView) itemView.findViewById(R.id.linkTitleTV);
                mLinkDescriptionTV = (TextView) itemView.findViewById(R.id.linkDescriptionTV);
            }
            if (viewType == TYPE_AUDIO_FROM_USER || viewType == TYPE_AUDIO_TO_USER) {
                mAudioItemsContainer = (ViewGroup) itemView.findViewById(R.id.audioItemsContainer);
            }
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(Message message) {
            messageTV.setVisibility(message.getMessageBody().equals("") ? View.GONE : View.VISIBLE);
            messageTV.setText(message.getMessageBody());
            Attachment[] attachments = message.getAttachments();

            //TODO: пока что обрабатываю только записи на стене и ссылки (Link).
            if (attachments == null || attachments[0] == null) {
                return;
            }
            if (attachments[0].isWallPost()) {
                bindWallPost((WallPost) attachments[0].getBody());
            } else if (attachments[0].isLink()) {
                bindLink((Link) attachments[0].getBody());
            } else if (attachments[0].isAudio()) {
                List<Audio> audios = new ArrayList<>();
                for (Attachment a : attachments) {
                    if (a.isAudio()) {
                        audios.add((Audio) a.getBody());
                    }
                }
                bindAudios(audios);
            }
        }

        private void bindAudios(List<Audio> audios) {
            mAudioItemsContainer.removeAllViews();
            for (Audio audio : audios) {
                View audioItemView = mInflater.inflate(R.layout.audio_item_layout, null);

                TextView artistNameTV = (TextView) audioItemView.findViewById(R.id.artistNameTV);
                artistNameTV.setText(audio.getArtist());

                TextView audioNameTV = (TextView) audioItemView.findViewById(R.id.audioNameTV);
                audioNameTV.setText(audio.getTitle());

                mAudioItemsContainer.addView(audioItemView);
            }
        }

        private void bindWallPost(WallPost post) {
            attachmentWallPostTitleTV.setText(post.isRepost() ? post.getRepostedWallPost().getText() : post.getText());
        }

        private void bindLink(final Link link) {
            mLinkTitleTV.setText(link.getTitle());
            mLinkDescriptionTV.setText(link.getDescription());
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