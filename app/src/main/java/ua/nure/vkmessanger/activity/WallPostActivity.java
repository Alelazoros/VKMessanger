package ua.nure.vkmessanger.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ua.nure.vkmessanger.R;
import ua.nure.vkmessanger.adapter.WallPostAdapter;
import ua.nure.vkmessanger.http.RESTInterface;
import ua.nure.vkmessanger.http.model.CustomResponse;
import ua.nure.vkmessanger.http.model.RequestResult;
import ua.nure.vkmessanger.http.model.loader.BaseLoader;
import ua.nure.vkmessanger.http.retrofit.RESTRetrofitManager;
import ua.nure.vkmessanger.model.Group;
import ua.nure.vkmessanger.model.Link;
import ua.nure.vkmessanger.model.Photo;
import ua.nure.vkmessanger.model.WallPost;
import ua.nure.vkmessanger.util.PicassoUtils;

public class WallPostActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<CustomResponse> {

    private static final String EXTRA_WALL_POST = "EXTRA_WALL_POST";

    private static final String GROUPS_LOADER_BUNDLE_ARGUMENT = "GROUPS_LOADER_BUNDLE_ARGUMENT";

    private static final int LOAD_GROUPS = 1;

    private RESTInterface mRESTInterface = new RESTRetrofitManager(this);

    private WallPost mWallPost;

    private List<Group> mGroups;

    private View mHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall_post);

        initToolbar();
        mWallPost = getDataFromIntent(getIntent());
        initPhotosRecyclerView();
        loadGroupsInfo(mWallPost);
        setWallPostContent(mHeader, mWallPost);
    }

    public static void newIntent(Context context, WallPost post) {
        Intent intent = new Intent(context, WallPostActivity.class);
        intent.putExtra(EXTRA_WALL_POST, post);
        context.startActivity(intent);
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

    private WallPost getDataFromIntent(Intent intent) {
        return (WallPost) intent.getExtras().get(EXTRA_WALL_POST);
    }

    private void initPhotosRecyclerView() {
        //At first - get list of photos from WallPost object.
        List<Photo> photos = mWallPost.getPhotosAttachments();
        //WallPost can also contains a Link.
        Link link = mWallPost.getLink();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.wallPostAttachmentsRecyclerView);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        //Является ли запись репостом.
        boolean isSingleHeader = !mWallPost.isRepost();
        mHeader = inflateWallOwnersHeader(recyclerView, isSingleHeader);

        final WallPostAdapter adapter = new WallPostAdapter(this, mHeader, photos, link);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.isHeader(position) ? layoutManager.getSpanCount() : 1;
            }
        });
    }


    /**
     * @param isSingleHeader true - если данная запись не была репостом с другой стены,
     *                       false - в противном случае.
     */
    private View inflateWallOwnersHeader(ViewGroup root, boolean isSingleHeader) {
        return isSingleHeader ?
                LayoutInflater.from(this).inflate(R.layout.wall_post_header_single, root, false) :
                LayoutInflater.from(this).inflate(R.layout.wall_post_header_double, root, false);
    }

    private void loadGroupsInfo(WallPost wallPost) {
        Bundle args = new Bundle();

        int countGroups = wallPost.isRepost() ? 2 : 1;
        String[] groupsIds = new String[countGroups];
        groupsIds[0] = String.valueOf(Math.abs(wallPost.getWallOwnerId()));

        //Если запись является репостом.
        WallPost[] copyHistory = wallPost.getCopyHistory();
        for (int i = 1; i < groupsIds.length; i++) {
            groupsIds[i] = String.valueOf(Math.abs(copyHistory[i - 1].getWallOwnerId()));
        }

        args.putStringArray(GROUPS_LOADER_BUNDLE_ARGUMENT, groupsIds);
        getSupportLoaderManager().initLoader(LOAD_GROUPS, args, this);
    }

    private void setWallPostContent(View header, WallPost wallPost) {
        TextView contentTV = (TextView) header.findViewById(R.id.wallPostContentTV);
        contentTV.setText(wallPost.isRepost() ? wallPost.getRepostedWallPost().getText() : wallPost.getText());
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
                    updateWallOwnersHeaderInfo(mGroups, mHeader);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<CustomResponse> loader) { }

    /**
     * Данный метод обновляет header, который должен содержать информацию
     * о владельце стены, как которой была размещена запись.
     */
    private void updateWallOwnersHeaderInfo(List<Group> groups, View header) {
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
        //Трансформация изображения в круглую форму.
        Transformation transformation = PicassoUtils.getCircleTransformation();

        Group wallOwnerGroup = groups.get(0);

        TextView wallOwnerName = (TextView) header.findViewById(R.id.wallOwnerNameTV);
        wallOwnerName.setText(wallOwnerGroup.getName());

        TextView wallPostDate = (TextView) header.findViewById(R.id.wallPostDateTV);
        wallPostDate.setText(sdf.format(mWallPost.getDate()));

        ImageView wallOwnerAvatar = (ImageView) header.findViewById(R.id.wallOwnerAvatar);
        Picasso.with(this)
                .load(wallOwnerGroup.getPhotoURL())
                .transform(transformation)
                .into(wallOwnerAvatar);

        //Если запись является репостом, то отображаю еще и владельца стены, с которой был репост.
        if (groups.size() != 1) {
            Group forwardedGroup = groups.get(1);

            TextView forwardWallOwnerName = (TextView) header.findViewById(R.id.forwardWallOwnerNameTV);
            forwardWallOwnerName.setText(forwardedGroup.getName());

            TextView forwardWallPostDate = (TextView) header.findViewById(R.id.forwardWallPostDateTV);
            forwardWallPostDate.setText(sdf.format(mWallPost.getDate()));

            ImageView forwardWallOwnerAvatar = (ImageView) header.findViewById(R.id.forwardWallOwnerAvatar);
            Picasso.with(this)
                    .load(forwardedGroup.getPhotoURL())
                    .transform(transformation)
                    .into(forwardWallOwnerAvatar);
        }
    }
}