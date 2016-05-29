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

import java.util.List;

import ua.nure.vkmessanger.R;
import ua.nure.vkmessanger.model.User;
import ua.nure.vkmessanger.util.PicassoUtils;

/**
 * Created by Antony on 5/29/2016.
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendHolder> {

    private static final int LAYOUT = R.layout.friend_item_layout;

    private Context mContext;

    private LayoutInflater mInflater;

    @Nullable
    private List<User> mFriends;

    @Nullable
    private OnFriendClickListener mClickListener;


    public FriendsAdapter(Context context, @Nullable List<User> friends, @Nullable OnFriendClickListener clickListener) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mFriends = friends;
        mClickListener = clickListener;
        setHasStableIds(true);
    }

    public User getItem(int position) {
        return mFriends != null ? mFriends.get(position) : null;
    }

    @Override
    public FriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(LAYOUT, parent, false);
        return new FriendHolder(mContext, view, mClickListener);
    }

    @Override
    public void onBindViewHolder(FriendHolder holder, int position) {
        if (mFriends != null) {
            holder.bind(mFriends.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mFriends != null ? mFriends.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return mFriends != null ? mFriends.get(position).getId() : 0;
    }


    static class FriendHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Nullable
        private OnFriendClickListener clickListener;

        private Context context;

        private ImageView avatar;

        private TextView friendName;


        public FriendHolder(Context context, View itemView, @Nullable OnFriendClickListener listener) {
            super(itemView);

            this.context = context;

            avatar = (ImageView) itemView.findViewById(R.id.friendAvatarImageView);
            friendName = (TextView) itemView.findViewById(R.id.friendNameTV);

            clickListener = listener;
            itemView.setOnClickListener(this);
        }

        public void bind(User friend) {
            Picasso.with(context)
                    .load(friend.getAvatar200Url() != null ? friend.getAvatar200Url() : friend.getAvatar100Url())
                    .transform(PicassoUtils.getCircleTransformation())
                    .placeholder(R.drawable.default_avatar_camera_100)
                    .into(avatar);

            friendName.setText(String.format("%s %s", friend.getFirstName(), friend.getLastName()));
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onClick(getLayoutPosition());
            }
        }
    }

    public interface OnFriendClickListener {
        void onClick(int position);
    }

}