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

import ru.vpcb.footballassistant.glide.GlideUtils;
import ru.vpcb.footballassistant.glide.SvgSoftwareLayerSetter;
import ru.vpcb.footballassistant.utils.Config;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static ru.vpcb.footballassistant.utils.Config.RT_HEAD_VIEW_TYPE;
import static ru.vpcb.footballassistant.utils.Config.RT_ITEM_VIEW_TYPE_DARK;
import static ru.vpcb.footballassistant.utils.Config.RT_ITEM_VIEW_TYPE_LIGHT;

/**
 * RecyclerView Adapter class
 * Used to create and show Item objects of RecyclerView
 */
public class RecyclerLeagueAdapter extends RecyclerView.Adapter<RecyclerLeagueAdapter.ViewHolder> {
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
    public RecyclerLeagueAdapter(Context context, List<FDFixture> list,
                                 Map<Integer, FDCompetition> map, Map<Integer, FDTeam> mapTeam) {
        mContext = context;
        mRes = context.getResources();
        mList = list;
        mMap = map;
        mMapTeam = mapTeam;

        mIsWide = mRes.getBoolean(R.bool.is_wide);
        mIsLand = mRes.getBoolean(R.bool.is_land);
        mRequestBuilder = GlideUtils.getRequestBuilderSvg(context);
        mRequestBuilderCommon = GlideUtils.getRequestBuilderPng(context);

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
        if (position == 0) return RT_HEAD_VIEW_TYPE;
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
        int layoutId;
        if (viewType == RT_HEAD_VIEW_TYPE) layoutId = R.layout.league_recycler_head;
        else layoutId = R.layout.league_recycler_item;

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
        @Nullable
        @BindView(R.id.text_lg_team_pos)
        TextView mTextTeamPos;

        @Nullable
        @BindView(R.id.image_lg_team_logo)
        ImageView mImageTeamLogo;
        @Nullable
        @BindView(R.id.text_lg_team_name)
        TextView mTextTeamName;
        @Nullable
        @BindView(R.id.text_lg_matches)
        TextView mTextTeamMatches;
        @Nullable
        @BindView(R.id.text_tm_item_league)
        TextView mTextLeague;
        @Nullable
        @BindView(R.id.text_tm_item_date)
        TextView mTextDate;
        @Nullable
        @BindView(R.id.text_tm_item_status)
        TextView mTextStatus;
        @Nullable
        @BindView(R.id.constraint_recycler_league_item)
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
            mColorDark = ContextCompat.getColor(mContext, R.color.league_lg_item_back_dark);
        }

        /**
         * Fills TextViews with the data from source data object
         * Loads image with the Glide loader to ImageViews.
         *
         * @param position int position of item in RecyclerView
         */
        private void fill(int position) {
            if (mList == null || mList.get(position) == null) return;

            if (getItemViewType() == RT_HEAD_VIEW_TYPE) return;

            if (getItemViewType() == RT_ITEM_VIEW_TYPE_DARK) {
                layout.setBackgroundColor(mColorDark);
            }


            FDFixture fixture = mList.get(position);
//// test!!!
//            String league = EMPTY_LONG_DASH;
//            int competitionId = fixture.getCompetitionId();
//            if (mMap != null && competitionId > 0) {
//                FDCompetition competition = mMap.get(fixture.getCompetitionId());
//                if (competition != null) league = competition.getCaption();
//            }
//
//            String score = fixture.getMatchScore();
//            String dateTime = fixture.formatMatchDate();
//
//            if (getItemViewType() == RT_ITEM_VIEW_TYPE_DARK) {
//                layout.setBackgroundColor(mColorDark);
//            }
//
////            mImageHome.setImageResource(homeTeamImageId);
////            mImageAway.setImageResource(awayTeamImageId);
//
//            mTextScore.setText(score);
//            mTextLeague.setText(league);
//            mTextDate.setText(dateTime.substring(6));
//            mTextStatus.setText(fixture.getStatus());
            mTextTeamPos.setText(String.valueOf(position));
            mTextTeamName.setText(fixture.getHomeTeamName());
//            setTeamImage(fixture.getHomeTeamId(), mImageTeamLogo);
            GlideUtils.setTeamImage(fixture.getHomeTeamId(), mImageTeamLogo, mMapTeam,
                    mRequestBuilder, mRequestBuilderCommon);
        }


    }


}