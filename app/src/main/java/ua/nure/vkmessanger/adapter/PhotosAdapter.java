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
 * Адаптер для списка фотографий.
 */
public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder> {

    private static final int ATTACHMENT_PHOTO_LAYOUT = R.layout.attachment_photo;

    private Context mContext;

    private LayoutInflater mLayoutInflater;

    private List<Photo> mPhotos;

    /**
     * Карта используется для того, чтобы сохранить загруженные изображения, не загружая каждый раз
     * одно и тоже изображение в методе onBind().
     */
    private Map<Integer, Bitmap> mBitmapsMap;


    public PhotosAdapter(Context context, List<Photo> photos) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mPhotos = photos;
        mBitmapsMap = new HashMap<>();
        setHasStableIds(true);
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(ATTACHMENT_PHOTO_LAYOUT, parent, false);
        return new PhotoViewHolder(mContext, view);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        if (mPhotos != null) {
            holder.bind(position, mPhotos, mBitmapsMap);
        }
    }

    @Override
    public int getItemCount() {
        return mPhotos == null ? 0 : mPhotos.size();
    }

    @Override
    public long getItemId(int position) {
        return mPhotos == null ? 0 : mPhotos.get(position).getId();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {

        private Picasso mPicasso;

        private ImageView mPhotoImageView;

        public PhotoViewHolder(Context context, View itemView) {
            super(itemView);
            mPicasso = Picasso.with(context);
            mPhotoImageView = (ImageView) itemView.findViewById(R.id.attachmentPhotoImageView);
        }

        public void bind(final int position, List<Photo> photos, final Map<Integer, Bitmap> bitmapsMap) {

            if (bitmapsMap.containsKey(position)) {
                mPhotoImageView.setImageBitmap(bitmapsMap.get(position));
                return;
            }
            mPicasso.load(photos.get(position).getNormalSizePhotoURL()).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Log.d("LOAD_IMAGE", "SUCCESS");
                    mPhotoImageView.setImageBitmap(bitmap);
                    bitmapsMap.put(position, bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Log.d("LOAD_IMAGE", "FAILED");
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    Log.d("LOAD_IMAGE", "PREPARE_LOAD");
                }
            });
        }
    }

}
