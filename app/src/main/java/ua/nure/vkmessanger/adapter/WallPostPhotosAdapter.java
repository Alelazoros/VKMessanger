package ua.nure.vkmessanger.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.nure.vkmessanger.R;
import ua.nure.vkmessanger.model.Photo;

/**
 * Адаптер для списка фотографий, которые приклеплены к записи на стене.
 * Адаптер также обеспечивает работу с Header-ом - заголовком, который
 * отображает владельца стены, на которой была размещена запись.
 */
public class WallPostPhotosAdapter extends RecyclerView.Adapter<WallPostPhotosAdapter.PhotoViewHolder> {

    private static final int ATTACHMENT_PHOTO_LAYOUT = R.layout.attachment_photo;

    private static final int TYPE_HEADER_LAYOUT = 1;
    private static final int TYPE_PHOTO_LAYOUT = 2;

    private Context mContext;

    private LayoutInflater mLayoutInflater;

    /**
     * Заголовок, представляющий собой владельца стены, на которой была размещена запись.
     */
    private View mHeader;

    private List<Photo> mPhotos;


    public WallPostPhotosAdapter(Context context, View header, List<Photo> photos) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mHeader = header;
        mPhotos = photos;
    }

    public boolean isHeader(int position) {
        return position == 0;
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? TYPE_HEADER_LAYOUT : TYPE_PHOTO_LAYOUT;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER_LAYOUT){
            return new PhotoViewHolder(mHeader, mContext);
        }
        View view = mLayoutInflater.inflate(ATTACHMENT_PHOTO_LAYOUT, parent, false);
        return new PhotoViewHolder(view, mContext);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        if (mPhotos != null) {
            if (isHeader(position)) {
                return; //because logic is in Activity.
            }
            holder.bindPhoto(position - 1, mPhotos);//position - 1, т.к. учитываю header.
        }
    }

    @Override
    public int getItemCount() {
        return mPhotos == null ? 1 : mPhotos.size() + 1;//Учитываю header.
    }


    static class PhotoViewHolder extends RecyclerView.ViewHolder {

        private Picasso mPicasso;

        private ImageView mPhotoImageView;

        public PhotoViewHolder(View itemView, Context context) {
            super(itemView);
            mPicasso = Picasso.with(context);
            mPhotoImageView = (ImageView) itemView.findViewById(R.id.attachmentPhotoImageView);
        }

        public void bindPhoto(final int position, List<Photo> photos) {
            String photoURL = photos.get(position).getNormalSizePhotoURL();
            mPicasso.load(photoURL).into(mPhotoImageView);
        }
    }
}
