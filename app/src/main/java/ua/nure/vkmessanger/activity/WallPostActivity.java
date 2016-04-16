package ua.nure.vkmessanger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ua.nure.vkmessanger.R;
import ua.nure.vkmessanger.adapter.PhotosAdapter;
import ua.nure.vkmessanger.http.RESTInterface;
import ua.nure.vkmessanger.http.model.CustomResponse;
import ua.nure.vkmessanger.http.model.RequestResult;
import ua.nure.vkmessanger.http.model.loader.BaseLoader;
import ua.nure.vkmessanger.http.retrofit.RESTRetrofitManager;
import ua.nure.vkmessanger.model.Attachment;
import ua.nure.vkmessanger.model.Group;
import ua.nure.vkmessanger.model.Photo;
import ua.nure.vkmessanger.model.WallPost;

public class WallPostActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<CustomResponse> {

    public static final String EXTRA_WALL_POST = "EXTRA_WALL_POST";

    private static final String GROUPS_LOADER_BUNDLE_ARGUMENT = "GROUPS_LOADER_BUNDLE_ARGUMENT";

    private static final int LOAD_GROUPS = 1;

    private RESTInterface mRESTInterface = new RESTRetrofitManager(this);

    private WallPost mWallPost;

    private List<Group> mGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall_post);

        initToolbar();
        getDataFromIntent(getIntent());
        initPhotosRecyclerView();
    }

    private void initPhotosRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.wallPostAttachmentsRecyclerView);

        //get list of photos.
        List<Photo> photos = new ArrayList<>();
        Attachment[] wallPostPhotosAttachments = mWallPost.getCopyHistory() == null ?
                mWallPost.getAttachments() : mWallPost.getCopyHistory()[0].getAttachments();

        for (Attachment attachment : wallPostPhotosAttachments) {
            if (attachment != null && attachment.isPhoto()) {
                photos.add((Photo) attachment.getBody());
            }
        }

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(photos.size() / 4 + 1, StaggeredGridLayoutManager.VERTICAL));
//        recyclerView.setLayoutManager(new GridLayoutManager(this, photos.size() / 4 + 1));
        recyclerView.setAdapter(new PhotosAdapter(this, photos));
        recyclerView.setHasFixedSize(true);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.wall_post);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getDataFromIntent(Intent intent) {
        mWallPost = (WallPost) intent.getExtras().get(EXTRA_WALL_POST);
        Log.d("CLICKED WALL POST", mWallPost.toString());

        loadGroupsInfo(mWallPost);
    }

    private void loadGroupsInfo(WallPost wallPost) {
        Bundle args = new Bundle();

        int countGroups = wallPost.getCopyHistory() == null ? 1 : 2;
        String[] groups = new String[countGroups];
        groups[0] = String.valueOf(Math.abs(wallPost.getWallOwnerId()));

        //Если запись является репостом.
        WallPost[] copyHistory = wallPost.getCopyHistory();
        for (int i = 1; i < groups.length; i++) {
            groups[i] = String.valueOf(Math.abs(copyHistory[i - 1].getWallOwnerId()));
        }

        args.putStringArray(GROUPS_LOADER_BUNDLE_ARGUMENT, groups);
        getSupportLoaderManager().initLoader(LOAD_GROUPS, args, this);
    }


    //---------------------------------------------------//

    @Override
    public Loader<CustomResponse> onCreateLoader(final int id, final Bundle args) {
        return new BaseLoader(this) {
            @Override
            public CustomResponse apiCall() throws IOException {
                switch (id) {
                    case LOAD_GROUPS:
                        return mRESTInterface.getGroupsInfoByIds(args.getStringArray(GROUPS_LOADER_BUNDLE_ARGUMENT));
                    default:
                        return new CustomResponse();
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<CustomResponse> loader, CustomResponse data) {
        if (data.getRequestResult() == RequestResult.SUCCESS) {
            switch (loader.getId()) {
                case LOAD_GROUPS:
                    this.mGroups = data.getTypedAnswer();
                    updateWallOwnersUIInfo(mGroups);
                    break;
            }
        }
    }

    private void updateWallOwnersUIInfo(List<Group> groups) {
        Group wallOwnerGroup = groups.get(0);

        TextView wallOwnerName = (TextView) findViewById(R.id.wallOwnerNameTV);
        wallOwnerName.setText(wallOwnerGroup.getName());

        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());

        TextView wallPostDate = (TextView) findViewById(R.id.wallPostDateTV);
        wallPostDate.setText(sdf.format(mWallPost.getDate()));

        final ImageView wallOwnerAvatar = (ImageView) findViewById(R.id.wallOwnerAvatar);
        Picasso.with(this).load(wallOwnerGroup.getPhotoURL()).into(wallOwnerAvatar);

        if (groups.size() != 1) {
            Group forwardedGroup = groups.get(1);
            TextView forwardWallOwnerName = (TextView) findViewById(R.id.forwardWallOwnerNameTV);
            forwardWallOwnerName.setText(forwardedGroup.getName());

            TextView forwardWallPostDate = (TextView) findViewById(R.id.forwardWallPostDateTV);
            forwardWallPostDate.setText(sdf.format(mWallPost.getDate()));

            ImageView forwardWallOwnerAvatar = (ImageView) findViewById(R.id.forwardWallOwnerAvatar);
            Picasso.with(this).load(forwardedGroup.getPhotoURL()).into(forwardWallOwnerAvatar);
        } else {
            //Убираю с екрана второй контейнер для владельца стены, если запись - не репост.
            findViewById(R.id.forwardWallOwnerInfoContainer).setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<CustomResponse> loader) { }
}
