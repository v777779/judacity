package ru.vpcb.footballassistant;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.RequestBuilder;

import java.util.Calendar;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.vpcb.footballassistant.data.FDCompetition;
import ru.vpcb.footballassistant.data.FDFixture;
import ru.vpcb.footballassistant.data.FDTeam;
import ru.vpcb.footballassistant.utils.FootballUtils;

import static ru.vpcb.footballassistant.glide.GlideUtils.setTeamImage;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_APP_BAR_HEIGHT;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_NEWS_LINK;

public class NewsFragment extends Fragment implements ICallback {


    @BindView(R.id.icon_news_arrow_back)
    ImageView mViewArrowBack;
    @BindView(R.id.icon_news_share_action)
    ImageView mViewShare;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;


    private RequestBuilder<PictureDrawable> mRequestSvgH;
    private RequestBuilder<Drawable> mRequestPngH;
    private RequestBuilder<PictureDrawable> mRequestSvgA;
    private RequestBuilder<Drawable> mRequestPngA;


    // match toolbar
    // match start


    // parameters
    private View mRootView;
    private NewsActivity mActivity;
    private Context mContext;
    private int mAppBarHeight;
    private Unbinder mUnbinder;


    private Map<Integer, FDCompetition> mMap;
    private Map<Integer, FDTeam> mMapTeams;
    private Map<Integer, FDFixture> mMapFixtures;
    private FDFixture mFixture;
    private FDTeam mHomeTeam;
    private FDTeam mAwayTeam;
    private String mLink;


    public static Fragment newInstance(String link) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_NEWS_LINK, link);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (NewsActivity) context;
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
//            AppBarLayout appBarLayout = mActivity.getWindow().getDecorView().findViewById(R.id.app_bar);
//            mAppBarHeight = appBarLayout.getHeight();
        } else {
//            mAppBarHeight = savedInstanceState.getInt(BUNDLE_APP_BAR_HEIGHT);
        }
// args
        Bundle args = getArguments();
        if (args == null) return;
        mLink = args.getString(BUNDLE_NEWS_LINK);

        if (mLink == null || mLink.isEmpty()) {
            FootballUtils.showMessage(mContext,getString(R.string.news_no_data_message));
        }

// parameters
// loader

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.news_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, mRootView);

        setupActionBar(savedInstanceState);
        setupListeners();
        bindViews();
        return mRootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_APP_BAR_HEIGHT, mAppBarHeight);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        restoreActionBar();
        mUnbinder.unbind();
    }

    // callbacks
    @Override
    public void onComplete(View view, int value) {
        FootballUtils.showMessage(mContext, getString(R.string.text_test_recycler_click));
    }

    @Override
    public void onComplete(int mode, Calendar calendar) {
    }

    @Override
    public void onComplete(View view, String value) {

    }

    // methods
    private void stopProgress() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }


    private void setupProgress() {
        mProgressBar.setIndeterminate(true);
        mProgressBar.setVisibility(View.INVISIBLE);
    }


    private void bindViews() {

//// toolbar
//        String country = FDUtils.getCountry(mFixture.getLeague());
//        mTextCountry.setText(country);
//        mViewLeague.setText(mFixture.getCaption());
//        setTeamImage(mHomeTeam.getCrestURL(), mViewTeamHome, mRequestSvgH, mRequestPngH,
//                R.drawable.fc_logo_widget_home);
//        setTeamImage(mAwayTeam.getCrestURL(), mViewTeamAway, mRequestSvgA, mRequestPngA,
//                R.drawable.fc_logo_widget_away);
//        mTextTime.setText(FDUtils.formatMatchTime(mFixture.getDate()));
//        mTextTeamHome.setText(mFixture.getHomeTeamName());
//        mTextTeamAway.setText(mFixture.getAwayTeamName());
//        mTextDate.setText(FDUtils.formatMatchDate(mFixture.getDate()));
//        mTextScoreHome.setText(FDUtils.formatFromInt(mFixture.getGoalsHome(), EMPTY_DASH));
//        mTextScoreAway.setText(FDUtils.formatFromInt(mFixture.getGoalsAway(), EMPTY_DASH));
//        mTextStatus.setText(mFixture.getStatus());
//        if (mFixture.isFavorite()) {
//            mViewFavorite.setImageResource(R.drawable.ic_star);
//        }
//        if (mFixture.isNotified()) {
//            mViewNotification.setImageResource(R.drawable.ic_notifications);
//        }
//// start
//        Date date = FDUtils.formatDateFromSQLite(mFixture.getDate());
//        if (date != null) {
//            long current = Calendar.getInstance().getTimeInMillis();
//            long time = date.getTime();
//            mTextStartDate.setText(FDUtils.formatMatchDateStart(mFixture.getDate()));
//            if (current > time) {
//                mTextDayHigh.setText(EMPTY_DASH);
//                mTextDayMddle.setText(EMPTY_DASH);
//                mTextDayLow.setText(EMPTY_DASH);
//                mTextHourHigh.setText(EMPTY_DASH);
//                mTextHourLow.setText(EMPTY_DASH);
//                mTextMinHigh.setText(EMPTY_DASH);
//                mTextMinLow.setText(EMPTY_DASH);
//            } else {
//                long delta = time - current;
//                long days = delta / TimeUnit.DAYS.toMillis(1);
//                delta = delta - days * TimeUnit.DAYS.toMillis(1);
//                long hours = delta / TimeUnit.HOURS.toMillis(1);
//                delta = delta - hours * TimeUnit.HOURS.toMillis(1);
//                long minutes = delta / TimeUnit.MINUTES.toMillis(1);
//
//                int[] a = getOrders((int) days);
//                if (a[2] > 0) {
//                    mTextDayHigh.setVisibility(View.VISIBLE);
//                    mTextDayHigh.setText(FDUtils.formatFromInt(a[2], EMPTY_STRING));
//                }
//                mTextDayMddle.setText(FDUtils.formatFromInt(a[1], EMPTY_STRING));
//                mTextDayLow.setText(FDUtils.formatFromInt(a[0], EMPTY_STRING));
//
//                a = getOrders((int) hours);
//                mTextHourHigh.setText(FDUtils.formatFromInt(a[1], EMPTY_STRING));
//                mTextHourLow.setText(FDUtils.formatFromInt(a[0], EMPTY_STRING));
//
//                a = getOrders((int) minutes);
//                mTextMinHigh.setText(FDUtils.formatFromInt(a[1], EMPTY_STRING));
//                mTextMinLow.setText(FDUtils.formatFromInt(a[0], EMPTY_STRING));
//
//
//            }
//        } else {
//            mTextStartDate.setText(EMPTY_LONG_DASH);
//            mTextDayHigh.setText(EMPTY_DASH);
//            mTextDayMddle.setText(EMPTY_DASH);
//            mTextDayLow.setText(EMPTY_DASH);
//            mTextHourHigh.setText(EMPTY_DASH);
//            mTextHourLow.setText(EMPTY_DASH);
//            mTextMinHigh.setText(EMPTY_DASH);
//            mTextMinLow.setText(EMPTY_DASH);
//        }
//
//// bet
//
//        mTextBetHome.setText(FDUtils.formatMatchBet(mFixture.getHomeWin()));
//        mTextBetDraw.setText(FDUtils.formatMatchBet(mFixture.getDraw()));
//        mTextBetAway.setText(FDUtils.formatMatchBet(mFixture.getAwayWin()));
//
////        mTextBetHome.setText("27.1");
////        mTextBetDraw.setText("32.2");
////        mTextBetAway.setText("17.9");
//

    }


    private void startActivityLeague(int id) {
//        Intent intent = new Intent(this, LeagueActivity.class);
//        intent.putExtra(BUNDLE_INTENT_LEAGUE_ID, id);
//        startActivity(intent);
    }

    private void startActivityTeam(int id) {
//        Intent intent = new Intent(this, TeamActivity.class);
//        intent.putExtra(BUNDLE_INTENT_TEAM_ID, id);
//        startActivity(intent);
    }


    private void startFragmentLeague() {

    }

    private void startFragmentTeam() {
//        FragmentManager fm = getSupportFragmentManager();
//        Fragment fragment = TeamFragment.newInstance();
//
//        fm.popBackStackImmediate(FRAGMENT_TEAM_TAG, POP_BACK_STACK_INCLUSIVE);
//        fm.beginTransaction()
//                .add(R.id.container_match, fragment)
//                .addToBackStack(FRAGMENT_TEAM_TAG)
//                .commit();

    }

    private void setupListeners() {

        mViewArrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.onBackPressed();
            }
        });

        mViewShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void restoreActionBar() {
        mActivity.getSupportActionBar().show();
//        AppBarLayout appBarLayout = mActivity.getWindow().getDecorView().findViewById(R.id.app_bar);
//        appBarLayout.getLayoutParams().height = mAppBarHeight;
    }

    private void setupActionBar(Bundle savedInstance) {
        mActivity.getSupportActionBar().hide();
//        AppBarLayout appBarLayout = mActivity.getWindow().getDecorView().findViewById(R.id.app_bar);
//        appBarLayout.getLayoutParams().height = 0;
    }


// test!!! protrusion check
// resolved with  set AppBarLayout.height to 0
//        ((AppBarLayout) findViewById(R.id.app_bar)).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                if (Math.abs(verticalOffset) > 5) {
//                    int k = 1;
//                }
//            }
//        });

}
