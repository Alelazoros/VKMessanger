package ua.nure.vkmessanger.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.nure.vkmessanger.R;
import ua.nure.vkmessanger.model.Link;
import ua.nure.vkmessanger.model.Photo;

/**
 * Адаптер для списка фотографий, которые приклеплены к записи на стене.
 * Адаптер также обеспечивает работу с Header-ом - заголовком, который
 * отображает владельца стены, на которой была размещена запись.
 */
public class WallPostPhotosAdapter extends RecyclerView.Adapter<WallPostPhotosAdapter.PhotoViewHolder> {

    private static final int TYPE_HEADER_LAYOUT = 1;
    private static final int TYPE_PHOTO_LAYOUT = R.layout.attachment_photo;
    private static final int TYPE_LINK_LAYOUT = R.layout.link_layout;

    private Context mContext;

    private LayoutInflater mLayoutInflater;

    /**
     * Заголовок, представляющий собой владельца стены, на которой была размещена запись.
     */
    private View mHeader;

    @Nullable
    private List<Photo> mPhotos;

    /**
     * Запись на стене также может содержать ссылку ('link').
     */
    @Nullable
    private Link mLink;


    public WallPostPhotosAdapter(Context context, View header, @Nullable List<Photo> photos, @Nullable Link link) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mHeader = header;
        mPhotos = photos;
        mLink = link;
    }

    public boolean isHeader(int position) {
        return position == 0;
    }

    public boolean isLink(int position) {
        //Учитываю mHeader и mPhotos при проверке позиции.
        return mLink != null && position == mPhotos.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return TYPE_HEADER_LAYOUT;
        } else if (isLink(position)) {
            return TYPE_LINK_LAYOUT;
        }
        return TYPE_PHOTO_LAYOUT;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER_LAYOUT) {
            return new PhotoViewHolder(mHeader, mContext);
        }
        View view = mLayoutInflater.inflate(viewType, parent, false);
        return new PhotoViewHolder(view, mContext);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        if (isHeader(position)) {
            //because header logic is in Activity.
            return;
        }
        if (!isLink(position) && mPhotos != null) {
            //position - 1, т.к. учитываю header.
            holder.bindPhoto(position - 1, mPhotos);
        } else if (isLink(position)) {
            holder.bindLink(mLink);
        }
    }

    @Override
    public int getItemCount() {
        //Учитываю mHeader и mLink.
        int hasLink = mLink == null ? 0 : 1;
        return mPhotos == null ?
                hasLink + 1 :
                mPhotos.size() + hasLink + 1;
    }


    static class PhotoViewHolder extends RecyclerView.ViewHolder {

        private Picasso mPicasso;

        //----------Photo----------//

        private ImageView mPhotoImageView;

        //-----------Link---------//

        private ImageView mLinkImageView;

        private TextView mLinkTitleTV;

        private TextView mLinkDescriptionTV;


        public PhotoViewHolder(View itemView, Context context) {
            super(itemView);
            mPicasso = Picasso.with(context);

            //Photo.
            mPhotoImageView = (ImageView) itemView.findViewById(R.id.attachmentPhotoImageView);

            //Link.
            mLinkImageView = (ImageView) itemView.findViewById(R.id.linkImageView);
            mLinkTitleTV = (TextView) itemView.findViewById(R.id.linkTitleTV);
            mLinkDescriptionTV = (TextView) itemView.findViewById(R.id.linkDescriptionTV);
        }

        public void bindPhoto(int position, List<Photo> photos) {
            String photoURL = photos.get(position).getNormalSizePhotoURL();
            mPicasso.load(photoURL).into(mPhotoImageView);
        }

        public void bindLink(Link link) {
            Photo linkPhoto = link.getPhoto();
            if (linkPhoto != null) {
                mPicasso.load(linkPhoto.getMaxSizePhotoURL()).into(mLinkImageView);
            }
            mLinkTitleTV.setText(link.getTitle());
            mLinkDescriptionTV.setText(link.getDescription());
        }
    }
}