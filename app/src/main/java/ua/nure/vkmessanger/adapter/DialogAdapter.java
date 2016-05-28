package ua.nure.vkmessanger.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ua.nure.vkmessanger.R;
import ua.nure.vkmessanger.model.Attachment;
import ua.nure.vkmessanger.model.Audio;
import ua.nure.vkmessanger.model.Chat;
import ua.nure.vkmessanger.model.Document;
import ua.nure.vkmessanger.model.Link;
import ua.nure.vkmessanger.model.Message;
import ua.nure.vkmessanger.model.Photo;
import ua.nure.vkmessanger.model.User;
import ua.nure.vkmessanger.model.UserDialog;
import ua.nure.vkmessanger.model.Video;
import ua.nure.vkmessanger.model.WallPost;
import ua.nure.vkmessanger.util.PicassoUtils;

/**
 * Адаптер для диалога.
 */
public class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.MessageViewHolder> {

    /**
     * TYPE_SIMPLE_MESSAGE_FROM_USER, TYPE_SIMPLE_MESSAGE_TO_USER - сообщения без вложений (attachments).
     */
    private static final int TYPE_SIMPLE_MESSAGE_FROM_USER = R.layout.dialog_message_from_user_layout;

    private static final int TYPE_SIMPLE_MESSAGE_TO_USER = R.layout.dialog_message_to_user_layout;

    /**
     * TYPE_COMPLEX_MESSAGE_FROM_USER, TYPE_COMPLEX_MESSAGE_TO_USER - сообщения с вложениями (attachments).
     */
    private static final int TYPE_COMPLEX_MESSAGE_FROM_USER = R.layout.dialog_message_complex_from_user_layout;

    private static final int TYPE_COMPLEX_MESSAGE_TO_USER = R.layout.dialog_message_complex_to_user_layout;


    private Context mContext;

    private LayoutInflater mInflater;

    @Nullable
    private List<Message> mMessages;

    @Nullable
    private OnDialogEndListener mDialogEndListener;

    @Nullable
    private OnMessageClickListener mClickListener;

    /**
     * Объект текущего диалога.
     */
    private UserDialog mDialog;


    public DialogAdapter(Context context, UserDialog dialog, @Nullable List<Message> messages,
                         @Nullable OnMessageClickListener listener, @Nullable OnDialogEndListener dialogEndListener) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mMessages = messages;
        mDialogEndListener = dialogEndListener;
        mClickListener = listener;
        mDialog = dialog;
        setHasStableIds(true);
    }

    public void setMessages(List<Message> messages){
        mMessages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        Message current = mMessages.get(position);
        if (!current.hasAttachments()) {
            return current.isFromMe() ? TYPE_SIMPLE_MESSAGE_FROM_USER : TYPE_SIMPLE_MESSAGE_TO_USER;
        } else {
            return current.isFromMe() ? TYPE_COMPLEX_MESSAGE_FROM_USER : TYPE_COMPLEX_MESSAGE_TO_USER;
        }
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(viewType, parent, false);
        return new MessageViewHolder(mContext, view, viewType, mClickListener, mDialog);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        if (mMessages == null) {
            return;
        } else if (position == getItemCount() - 1) {
            //Если пользователь доскроллил до конца RecyclerView, то надо подгрузить еще сообщения.
            mDialogEndListener.requestMoreMessages(getItemCount());
        }
        holder.bindMessage(mMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return mMessages != null ? mMessages.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return mMessages != null ? mMessages.get(position).getMessageId() : 0;
    }


    static class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private Context mContext;

        private UserDialog mDialog;

        private LayoutInflater mInflater;

        private OnMessageClickListener mClickListener;

        //------Chat user avatar-----//

        private ImageView mChatUserAvatarImageView;

        //--------Message text-------//

        private TextView mMessageTV;

        //---Attachment containers---//

        /**
         * Составной контейнер, который должен хранить фото и видео.
         */
        private ViewGroup mPhotoVideoContainer;

        /**
         * Простые контейнеры.
         */
        private ViewGroup mAudioContainer;

        private ViewGroup mDocsContainer;

        private ViewGroup mLinkContainer;

        private ViewGroup mWallPostContainer;

        //Счечик количества добавленных View фото или видео в их общий контейнер.
        private int mPhotosAndVideosCounter = 0;


        public MessageViewHolder(Context context, View itemView, int viewType, OnMessageClickListener clickListener,
                                 UserDialog dialog) {
            super(itemView);
            mContext = context;
            mInflater = LayoutInflater.from(context);
            mClickListener = clickListener;

            this.mDialog = dialog;
            mChatUserAvatarImageView = (ImageView) itemView.findViewById(R.id.chatUserAvatarImageView);
            if (mDialog.isChat() && mChatUserAvatarImageView != null) {
                mChatUserAvatarImageView.setVisibility(View.VISIBLE);
            }


            //Message text.
            mMessageTV = (TextView) itemView.findViewById(R.id.messageTextView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            if (viewType == TYPE_SIMPLE_MESSAGE_FROM_USER || viewType == TYPE_SIMPLE_MESSAGE_TO_USER) {
                return;
            }
            //Attachment containers.
            mPhotoVideoContainer = (ViewGroup) itemView.findViewById(R.id.messageAttachmentsPhotoVideoContainer);
            mAudioContainer = (ViewGroup) itemView.findViewById(R.id.messageAttachmentsAudioContainer);
            mDocsContainer = (ViewGroup) itemView.findViewById(R.id.messageAttachmentsDocContainer);
            mLinkContainer = (ViewGroup) itemView.findViewById(R.id.messageAttachmentsLinkContainer);
            mWallPostContainer = (ViewGroup) itemView.findViewById(R.id.messageAttachmentsWallPostContainer);
        }

        public void bindMessage(Message message) {
            mMessageTV.setVisibility(message.getMessageBody().equals("") ? View.GONE : View.VISIBLE);
            mMessageTV.setText(message.getMessageBody());

            //Если это чат и это не мое сообщение, то получаю аватарку юзера.
            if (mDialog.isChat() && !message.isFromMe()) {
                Chat chat = (Chat) mDialog.getBody();
                bindChatUserAvatar(message, chat);
            }

            if (message.hasAttachments()) {
                bindMessageAttachments(message.getAttachments());
            }
        }

        private void bindChatUserAvatar(Message message, Chat chat) {
            List<User> chatUsers = chat.getUsersList();

            String avatar = null;
            for (User user : chatUsers) {
                if (user.getId() == message.getUserId()) {
                    avatar = user.getAvatar100Url();
                }
            }
            if (avatar != null) {
                Picasso.with(mContext)
                        .load(avatar)
                        .transform(PicassoUtils.getCircleTransformation())
                        .into(mChatUserAvatarImageView);
            } else {
                mChatUserAvatarImageView.setImageResource(R.drawable.default_avatar_camera_100);
            }
        }

        private void bindMessageAttachments(Attachment[] attachments) {
            clearAttachmentContainers();
            for (Attachment attach : attachments) {
                if (attach == null) {
                    continue;
                }
                switch (attach.getType()) {
                    case Attachment.TYPE_PHOTO:
                    case Attachment.TYPE_VIDEO:
                        bindPhotoVideo(attach);
                        break;
                    case Attachment.TYPE_AUDIO:
                        Audio audio = (Audio) attach.getBody();
                        bindAudio(audio);
                        break;
                    case Attachment.TYPE_DOC:
                        Document document = (Document) attach.getBody();
                        bindDocument(document);
                        break;
                    case Attachment.TYPE_LINK:
                        Link link = (Link) attach.getBody();
                        bindLink(link);
                        break;
                    case Attachment.TYPE_WALL_POST:
                        WallPost post = (WallPost) attach.getBody();
                        bindWallPost(post);
                        break;
                }
            }
        }

        private void clearAttachmentContainers() {
            mPhotosAndVideosCounter = 0;
            mPhotoVideoContainer.removeAllViews();
            mAudioContainer.removeAllViews();
            mDocsContainer.removeAllViews();
            mLinkContainer.removeAllViews();
            mWallPostContainer.removeAllViews();
        }

        private void bindPhotoVideo(Attachment attachment) {
            ViewGroup smallContainer = (ViewGroup) mPhotoVideoContainer.findViewById(R.id.smallAudioVideoContainer);
            ViewGroup container = smallContainer != null ? smallContainer : mPhotoVideoContainer;

            if (mPhotosAndVideosCounter >= 2 && smallContainer == null) {
                smallContainer = createSmallHorizontalPhotoVideoContainer();
                //Добавляю layout.
                container.addView(smallContainer);
                //И назвачаю его контейнером, в который идет дальнейщее добавление элементов View.
                container = smallContainer;
            }

            boolean isNeedSmallImage = smallContainer != null;
            if (attachment.isPhoto()) {
                Photo photo = (Photo) attachment.getBody();
                addPhotoToContainer(container, photo, isNeedSmallImage);
            } else {
                Video video = (Video) attachment.getBody();
                addVideoToContainer(container, video, isNeedSmallImage);
            }
            mPhotosAndVideosCounter++;
        }

        private ViewGroup createSmallHorizontalPhotoVideoContainer() {

            LinearLayout resultContainer = new LinearLayout(mContext);
            //id хранится в ids.xml.
            resultContainer.setId(R.id.smallAudioVideoContainer);
            resultContainer.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            resultContainer.setLayoutParams(layoutParams);

            return resultContainer;
        }

        private void addPhotoToContainer(ViewGroup container, Photo photo, boolean isNeedSmallImage) {
            View attachmentPhotoView = mInflater.inflate(R.layout.attachment_item_photo, null);

            ImageView photoImageView = (ImageView) attachmentPhotoView.findViewById(R.id.attachmentPhotoImageView);
            Picasso.with(mContext)
                    .load(isNeedSmallImage ? photo.getSmallSizePhotoURL() : photo.getHDPhotoURL())
                    .into(photoImageView);

            container.addView(attachmentPhotoView);
        }

        private void addVideoToContainer(ViewGroup container, Video video, boolean isNeedSmallImage) {
            View attachmentVideoView = mInflater.inflate(R.layout.attachment_item_video, null);

            ImageView photoImageView = (ImageView) attachmentVideoView.findViewById(R.id.attachmentVideoImageView);
            Picasso.with(mContext)
                    .load(isNeedSmallImage ? video.getSmallSizePhotoURL() : video.getBigPhotoURL())
                    .into(photoImageView);

            //Длительность видео.
            TextView durationTV = (TextView) attachmentVideoView.findViewById(R.id.attachmentVideoDurationTV);
            durationTV.setText(video.getDurationStringValue());

            container.addView(attachmentVideoView);
        }

        private void bindAudio(Audio audio) {
            View audioItemView = mInflater.inflate(R.layout.attachment_item_audio, null);

            TextView artistNameTV = (TextView) audioItemView.findViewById(R.id.artistNameTV);
            TextView audioNameTV = (TextView) audioItemView.findViewById(R.id.audioNameTV);

            artistNameTV.setText(audio.getArtist());
            audioNameTV.setText(audio.getTitle());

            mAudioContainer.addView(audioItemView);
        }

        private void bindDocument(Document document) {
            View documentAttachmentView = mInflater.inflate(R.layout.attachment_item_document, null);

            TextView documentNameTV = (TextView) documentAttachmentView.findViewById(R.id.documentNameTV);
            TextView documentSizeTV = (TextView) documentAttachmentView.findViewById(R.id.documentSizeTV);

            documentNameTV.setText(document.getTitle());
            documentSizeTV.setText(document.getSizeStringValue(mContext));

            mDocsContainer.addView(documentAttachmentView);
        }

        private void bindLink(Link link) {
            View linkAttachmentView = mInflater.inflate(R.layout.attachment_item_link_for_selected_dialog, null);

            TextView linkTitleTV = (TextView) linkAttachmentView.findViewById(R.id.linkTitleTV);
            TextView linkDescriptionTV = (TextView) linkAttachmentView.findViewById(R.id.linkDescriptionTV);

            linkTitleTV.setText(link.getTitle());
            linkDescriptionTV.setText(link.getDescription());

            mLinkContainer.addView(linkAttachmentView);
        }

        private void bindWallPost(WallPost post) {
            View wallPostAttachmentView = mInflater.inflate(R.layout.attachment_item_wall_post, null);

            String postTitle = post.isRepost() ? post.getRepostedWallPost().getText() : post.getText();

            TextView wallPostTitleTV = (TextView) wallPostAttachmentView.findViewById(R.id.messageAttachmentWallPostTitleTV);
            wallPostTitleTV.setText(postTitle);

            mWallPostContainer.addView(wallPostAttachmentView);
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