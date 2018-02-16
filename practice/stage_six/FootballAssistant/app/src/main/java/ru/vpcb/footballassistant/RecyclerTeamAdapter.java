package ru.vpcb.footballassistant;


import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.vpcb.footballassistant.data.FDCompetition;
import ru.vpcb.footballassistant.data.FDFixture;
import ru.vpcb.footballassistant.data.FDTeam;
import ru.vpcb.footballassistant.glide.SvgSoftwareLayerSetter;
import ru.vpcb.footballassistant.utils.Config;


import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static ru.vpcb.footballassistant.utils.Config.EMPTY_LONG_DASH;
import static ru.vpcb.footballassistant.utils.Config.RT_ITEM_VIEW_TYPE_DARK;
import static ru.vpcb.footballassistant.utils.Config.RT_ITEM_VIEW_TYPE_LIGHT;
import static ru.vpcb.footballassistant.utils.FDUtils.formatMatchDate;
import static ru.vpcb.footballassistant.utils.FDUtils.formatMatchScore;

/**
 * RecyclerView Adapter class
 * Used to create and show Item objects of RecyclerView
 */
public class RecyclerTeamAdapter extends RecyclerView.Adapter<RecyclerTeamAdapter.ViewHolder> {
    /**
     * Cursor object source of data
     */
    private Cursor mCursor;
    /**
     * Context  context of calling activity
     */
    private Context mContext;
    /**
     * Span object used for RecyclerView as storage of display item parameters
     */
    private Config.Span mSpan;
    /**
     * Boolean is true for landscape layout
     */
    private boolean mIsLand;
    /**
     * Boolean is true for tablet with sw800dp
     */
    private boolean mIsWide;
    /**
     * Resources of activity
     */
    private Resources mRes;
    // test!!!
    private static int counter = 0;

    private List<FDFixture> mList;
    private Map<Integer, FDCompetition> mMap;
    private Map<Integer, FDTeam> mMapTeam;
    private RequestBuilder<PictureDrawable> mRequestBuilder;
    private RequestBuilder<Drawable> mRequestBuilderCommon;


    /**
     * Constructor of RecyclerAdapter
     *
     * @param context Context of calling activity
     */
    public RecyclerTeamAdapter(Context context, List<FDFixture> list,
                               Map<Integer, FDCompetition> map, Map<Integer, FDTeam> mapTeam) {
        mContext = context;
        mRes = context.getResources();
        mList = list;
        mMap = map;
        mMapTeam = mapTeam;

        mIsWide = mRes.getBoolean(R.bool.is_wide);
        mIsLand = mRes.getBoolean(R.bool.is_land);

        setupRequestBuilder();

    }

    /**
     * Returns itemID by position
     *
     * @param position in position of item
     * @return int itemID
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Returns item viewType by position
     *
     * @param position in position of item
     * @return int item viewType
     */

    @Override
    public int getItemViewType(int position) {

        return position % 2 == 0 ? RT_ITEM_VIEW_TYPE_LIGHT : RT_ITEM_VIEW_TYPE_DARK;
    }

    /**
     * Creates ViewHolder of Item of RecyclerView
     * Sets width or height of item according to span and size of RecyclerView Container
     *
     * @param parent   ViewGroup parent of item
     * @param viewType int type of View of Item, unused in this application
     * @return ViewHolder of Item of RecyclerView
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.team_recycler_item;
        View view = ((AppCompatActivity) mContext).getLayoutInflater()
                .inflate(layoutId, parent, false);
//        view.getLayoutParams().height = mSpan.getHeight();
        return new ViewHolder(view);
    }


    /**
     * Fills ViewHolder Item with image and text from data source.
     * Sets onClickListener which calls  ICallback.onComplete(view, int) method in calling activity.
     *
     * @param holder   ViewHolder object which is filled
     * @param position int position of item in Cursor data source
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.fill(position);
        final int pos = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ICallback) mContext).onComplete(view, pos);
            }
        });

    }

    /**
     * Returns number of Items of Cursor mCursor data source
     *
     * @return int number of Items of Cursor mCursor data source
     */
    @Override
    public int getItemCount() {
        if (mList == null) return 0;
        return mList.size();
    }

    /**
     * Replaces mList with new List<FDFixture> object and
     * calls notifyDataSetChanged() method.
     *
     * @param list List<FDFixture> parameter.
     */
    public void swap(List<FDFixture> list) {
        if (list == null) return;
        mList = list;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class of RecyclerView Item
     * Used to hold text and image resources of Item of RecyclerView
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_tm_item_score)
        TextView mTextScore;


        @BindView(R.id.image_tm_team_home)
        ImageView mImageHome;

        @BindView(R.id.image_tm_team_away)
        ImageView mImageAway;

        @BindView(R.id.text_tm_item_home)
        TextView mTextHome;

        @BindView(R.id.text_tm_item_away)
        TextView mTextAway;

        @BindView(R.id.text_tm_item_league)
        TextView mTextLeague;

        @BindView(R.id.text_tm_item_date)
        TextView mTextDate;

        @BindView(R.id.text_tm_item_status)
        TextView mTextStatus;

        @BindView(R.id.constraint_recycler_match_item)
        View layout;

        private int mColorDark;


        /**
         * Constructor
         * Binds all views with the ButterKnife object.
         *
         * @param view View of parent
         */
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mColorDark = ContextCompat.getColor(mContext, R.color.match_recycler_card_back_dark);
        }

        /**
         * Fills TextViews with the data from source data object
         * Loads image with the Glide loader to ImageViews.
         *
         * @param position int position of item in RecyclerView
         */
        private void fill(int position) {
            if (mList == null || mList.get(position) == null) return;

            FDFixture fixture = mList.get(position);
// test!!!
            String league = EMPTY_LONG_DASH;
            int competitionId = fixture.getCompetitionId();
            if (mMap != null && competitionId > 0) {
                FDCompetition competition = mMap.get(fixture.getCompetitionId());
                if (competition != null) league = competition.getCaption();
            }

            String score = formatMatchScore(fixture.getDate());
            String dateTime = formatMatchDate(fixture.getDate());

            if (getItemViewType() == RT_ITEM_VIEW_TYPE_DARK) {
                layout.setBackgroundColor(mColorDark);
            }

//            mImageHome.setImageResource(homeTeamImageId);
//            mImageAway.setImageResource(awayTeamImageId);

            mTextScore.setText(score);
            mTextLeague.setText(league);
            mTextDate.setText(dateTime.substring(6));
            mTextStatus.setText(fixture.getStatus());
            mTextHome.setText(fixture.getHomeTeamName());
            mTextAway.setText(fixture.getAwayTeamName());

            setTeamImage(fixture.getHomeTeamId(), mImageHome);
            setTeamImage(fixture.getAwayTeamId(), mImageAway);


        }


        private void setTeamImage(int id, ImageView imageView) {
            if (mMapTeam == null) return;
            FDTeam team = mMapTeam.get(id);
            if (team == null) return;

            String imageURL = team.getCrestURL();
            if (imageURL == null || imageURL.isEmpty()) return;
            imageURL = Config.imageCheckReplaceURL(imageURL);  // address replacement for known addresses

            if(imageURL.toLowerCase().endsWith("svg")) {
                mRequestBuilder.load(imageURL).into(imageView);
            }else {
                mRequestBuilderCommon.load(imageURL).into(imageView);
            }
//            Glide.with(mContext)
//                    .load(imageURL)
//                    .apply(new RequestOptions()
//                            .placeholder(R.drawable.fc_logo_loading)
//                            .error(R.drawable.fc_logo)
//                    )
//                    .into(imageView);
//            loadWithCallback(imageURL,imageView);

        }


        private void loadWithCallback(String imageURL, ImageView imageView) {


            Glide.with(mContext)
                    .load(imageURL)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.fc_logo_loading)
                            .error(R.drawable.fc_logo))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e,
                                                    Object model,
                                                    Target<Drawable> target,
                                                    boolean isFirstResource) {

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource,
                                                       Object model,
                                                       Target<Drawable> target,
                                                       DataSource dataSource,
                                                       boolean isFirstResource) {


                            return false;
                        }
                    })
                    .into(imageView);


        }

    }

    private void loadStandard(int id, ImageView imageView) {
        if (mMapTeam == null) return;
        FDTeam team = mMapTeam.get(id);
        if (team == null) return;
        String imageURL = team.getCrestURL();
        if (imageURL == null || imageURL.isEmpty()) return;

        Glide.with(mContext)
                .load(imageURL)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.fc_logo_loading)
                        .error(R.drawable.fc_logo)
                )
                .into(imageView);
    }

    private void setupRequestBuilder() {
        mRequestBuilder = Glide.with(mContext)
                .as(PictureDrawable.class)
                .apply(new RequestOptions()
                .placeholder(R.drawable.fc_logo_loading)
                .error(R.drawable.fc_logo)
                )
                .listener(new SvgSoftwareLayerSetter());
        mRequestBuilderCommon = Glide.with(mContext)
                .as(Drawable.class)
                .apply(new RequestOptions()
                .placeholder(R.drawable.fc_logo_loading)
                .error(R.drawable.fc_logo)
                )
                .listener(new CommonRequestListener());
    }

    private class CommonRequestListener implements RequestListener<Drawable> {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            return false;
        }
    }

}