package ua.nure.vkmessanger.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

import ua.nure.vkmessanger.R;
import ua.nure.vkmessanger.model.Chat;
import ua.nure.vkmessanger.model.User;
import ua.nure.vkmessanger.model.UserDialog;
import ua.nure.vkmessanger.util.PicassoUtils;

/**
 * Created by Antony on 5/27/2016.
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.DialogHolder> {

    private static final int LAYOUT = R.layout.dialog_item_layout;

    private Context mContext;

    private LayoutInflater mInflater;

    @Nullable
    private List<UserDialog> mDialogs;

    public MainAdapter(Context context, @Nullable List<UserDialog> dialogs) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDialogs = dialogs;
    }

    public void setDialogs(List<UserDialog> dialogs){
        mDialogs = dialogs;
    }

    @Override
    public DialogHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(LAYOUT, parent, false);
        return new DialogHolder(mContext, view);
    }

    @Override
    public void onBindViewHolder(DialogHolder holder, int position) {
        if (mDialogs != null) {
            holder.bind(mDialogs.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mDialogs != null ? mDialogs.size() : 0;
    }

    static class DialogHolder extends RecyclerView.ViewHolder {

        private Picasso picasso;

        private Transformation circleImageTransformation;

        private ImageView dialogAvatarIV;

//        private ImageView lastMessageAuthorAvatarIV;

        private TextView dialogTitleTV;

        private TextView lastMessageTV;


        public DialogHolder(Context context, View itemView) {
            super(itemView);

            picasso = Picasso.with(context);
            circleImageTransformation = PicassoUtils.getCircleTransformation();

            dialogAvatarIV = (ImageView) itemView.findViewById(R.id.dialogAvatarImageView);
//            lastMessageAuthorAvatarIV = (ImageView) itemView.findViewById(R.id.dialogLastMessageAuthorAvatarImageView);
            dialogTitleTV = (TextView) itemView.findViewById(R.id.dialogTitleTV);
            lastMessageTV = (TextView) itemView.findViewById(R.id.dialogLastMessageTV);
        }

        public void bind(UserDialog dialog) {
            Object dialogBody = dialog.getBody();
            String avatarUrl;
            String title;

            if (dialog.isSingle()) {
                User user = (User) dialogBody;
                avatarUrl = user.getAvatar200Url();
                title = String.format("%s %s", user.getFirstName(), user.getLastName());
            } else {
                Chat chat = (Chat) dialogBody;
                avatarUrl = chat.getChatAvatar200Url() != null ? chat.getChatAvatar200Url() : chat.getChatAvatar100Url();
                title = chat.getChatName();
            }

            picasso.load(avatarUrl)
                    .transform(circleImageTransformation)
                    .into(dialogAvatarIV);
            dialogTitleTV.setText(title);
            lastMessageTV.setText(dialog.getLastMessage());
        }
    }

}