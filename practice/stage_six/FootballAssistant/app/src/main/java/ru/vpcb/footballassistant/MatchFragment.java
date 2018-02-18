package ru.vpcb.footballassistant;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestBuilder;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.vpcb.footballassistant.data.FDCompetition;
import ru.vpcb.footballassistant.data.FDFixture;
import ru.vpcb.footballassistant.data.FDTeam;
import ru.vpcb.footballassistant.dbase.FDContract;
import ru.vpcb.footballassistant.dbase.FDLoader;
import ru.vpcb.footballassistant.dbase.FDProvider;
import ru.vpcb.footballassistant.glide.GlideUtils;
import ru.vpcb.footballassistant.utils.FDUtils;

import static ru.vpcb.footballassistant.glide.GlideUtils.setTeamImage;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_APP_BAR_HEIGHT;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_LOADER_DATA_URI;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_MATCH_AWAY_TEAM;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_MATCH_FIXTURE;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_MATCH_FIXTURE_ID;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_MATCH_HOME_TEAM;
import static ru.vpcb.footballassistant.utils.Config.EMPTY_DASH;
import static ru.vpcb.footballassistant.utils.Config.EMPTY_FIXTURE_ID;
import static ru.vpcb.footballassistant.utils.Config.EMPTY_LONG_DASH;
import static ru.vpcb.footballassistant.utils.Config.EMPTY_STRING;

public class MatchFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private RequestBuilder<PictureDrawable> mRequestSvgH;
    private RequestBuilder<Drawable> mRequestPngH;
    private RequestBuilder<PictureDrawable> mRequestSvgA;
    private RequestBuilder<Drawable> mRequestPngA;


    // match toolbar
    @BindView(R.id.image_sm_team_home)
    ImageView mViewTeamHome;
    @BindView(R.id.image_sm_team_away)
    ImageView mViewTeamAway;
    @BindView(R.id.text_sm_item_country)
    TextView mTextCountry;
    @BindView(R.id.text_sm_item_league)
    TextView mViewLeague;
    @BindView(R.id.text_sm_item_time)
    TextView mTextTime;
    @BindView(R.id.text_sm_team_home)
    TextView mTextTeamHome;
    @BindView(R.id.text_sm_team_away)
    TextView mTextTeamAway;
    @BindView(R.id.text_sm_item_date)
    TextView mTextDate;
    @BindView(R.id.text_sm_item_score_home)
    TextView mTextScoreHome;
    @BindView(R.id.text_sm_item_score_away)
    TextView mTextScoreAway;
    @BindView(R.id.text_sm_item_status)
    TextView mTextStatus;
    @BindView(R.id.match_notification_back)
    ImageView mViewNotification;
    @BindView(R.id.match_favorite_back)
    ImageView mViewFavorite;
    @BindView(R.id.icon_match_arrow_back)
    ImageView mViewArrowBack;
    @BindView(R.id.icon_match_share_action)
    ImageView mViewShare;

    // match start
    @BindView(R.id.match_start_date)
    TextView mTextStartDate;
    @BindView(R.id.match_time_day_high)
    TextView mTextDayHigh;
    @BindView(R.id.match_time_day_middle)
    TextView mTextDayMddle;
    @BindView(R.id.match_time_day_low)
    TextView mTextDayLow;
    @BindView(R.id.match_time_hour_high)
    TextView mTextHourHigh;
    @BindView(R.id.match_time_hour_low)
    TextView mTextHourLow;
    @BindView(R.id.match_time_min_high)
    TextView mTextMinHigh;
    @BindView(R.id.match_time_min_low)
    TextView mTextMinLow;


    // parameters
    private View mRootView;
    private DetailActivity mActivity;
    private Context mContext;
    private int mAppBarHeight;
    private Unbinder mUnbinder;


    private Map<Integer, FDCompetition> mMap;
    private Map<Integer, FDTeam> mMapTeams;
    private Map<Integer, FDFixture> mMapFixtures;
    private FDFixture mFixture;
    private FDTeam mHomeTeam;
    private FDTeam mAwayTeam;


    public static Fragment newInstance(FDFixture fixture, FDTeam homeTeam, FDTeam awayTeam) {
        MatchFragment fragment = new MatchFragment();
        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_MATCH_FIXTURE, fixture);
        if (homeTeam != null)
            args.putParcelable(BUNDLE_MATCH_HOME_TEAM, homeTeam);
        if (awayTeam != null)
            args.putParcelable(BUNDLE_MATCH_AWAY_TEAM, awayTeam);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (DetailActivity) context;
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            AppBarLayout appBarLayout = mActivity.getWindow().getDecorView().findViewById(R.id.app_bar);
            mAppBarHeight = appBarLayout.getHeight();
        } else {
            mAppBarHeight = savedInstanceState.getInt(BUNDLE_APP_BAR_HEIGHT);
        }
// args
        Bundle args = getArguments();
        if (args == null) return;
        mFixture = args.getParcelable(BUNDLE_MATCH_FIXTURE);
        mHomeTeam = args.getParcelable(BUNDLE_MATCH_HOME_TEAM);
        mAwayTeam = args.getParcelable(BUNDLE_MATCH_AWAY_TEAM);

        if (mFixture == null || mFixture.getId() <= 0 ||
                mFixture.getHomeTeamId() <= 0 ||
                mFixture.getAwayTeamId() <= 0) {
            return;
        }

// parameters
        mRequestSvgH = GlideUtils.getRequestBuilderSvg(mContext, R.drawable.fc_logo_widget_home);  // four requests for image logo
        mRequestPngH = GlideUtils.getRequestBuilderPng(mContext, R.drawable.fc_logo_widget_home);
        mRequestSvgA = GlideUtils.getRequestBuilderSvg(mContext, R.drawable.fc_logo_widget_away);
        mRequestPngA = GlideUtils.getRequestBuilderPng(mContext, R.drawable.fc_logo_widget_away);

        mMapTeams = new LinkedHashMap<>();
        mMapFixtures = new LinkedHashMap<>(); // fixed order

// loader
        Uri uri = FDProvider.buildLoaderIdUri(mContext, FDContract.FxEntry.LOADER_ID,
                mFixture.getHomeTeamId(), mFixture.getAwayTeamId());
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_LOADER_DATA_URI, uri);
        getLoaderManager().initLoader(FDContract.FxEntry.LOADER_ID, bundle, this);  // fixtures
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.match_fragment, container, false);
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return FDLoader.getInstance(mContext, id, args);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (loader == null || loader.getId() <= 0 || cursor == null || cursor.getCount() == 0) {
            return;
        }
        switch (loader.getId()) {
            case FDContract.FxEntry.LOADER_ID:
                Map<Integer, FDFixture> mapFixtures = FDUtils.readFixtures(cursor);
                if (mapFixtures == null || mapFixtures.isEmpty()) {
                    break;
                }
                mMapFixtures.putAll(mapFixtures);
                bindViewsFixtures();

                break;
            case FDContract.TmEntry.LOADER_ID:

                break;
            default:
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    // methods

    private int getFixtureId() {
        Bundle args = getArguments();
        if (args == null) return EMPTY_FIXTURE_ID;
        return args.getInt(BUNDLE_MATCH_FIXTURE_ID, EMPTY_FIXTURE_ID);

    }

    private void bindViewsFixtures() {


    }

    private int[] getOrders(int value) {
        int[] a = new int[3];
        for (int i = 0; i < a.length; i++) {
            a[i] = value % 10;
            value /= 10;
        }
        return a;
    }

    private void bindViews() {

// toolbar
        String country = FDUtils.getCountry(mFixture.getLeague());
        mTextCountry.setText(country);
        mViewLeague.setText(mFixture.getCompetitionName());
        setTeamImage(mHomeTeam.getCrestURL(), mViewTeamHome, mRequestSvgH, mRequestPngH,
                R.drawable.fc_logo_widget_home);
        setTeamImage(mAwayTeam.getCrestURL(), mViewTeamAway, mRequestSvgA, mRequestPngA,
                R.drawable.fc_logo_widget_away);
        mTextTime.setText(FDUtils.formatMatchTime(mFixture.getDate()));
        mTextTeamHome.setText(mFixture.getHomeTeamName());
        mTextTeamAway.setText(mFixture.getAwayTeamName());
        mTextDate.setText(FDUtils.formatMatchDate(mFixture.getDate()));
        mTextScoreHome.setText(FDUtils.formatFromInt(mFixture.getGoalsHome(), EMPTY_DASH));
        mTextScoreAway.setText(FDUtils.formatFromInt(mFixture.getGoalsAway(), EMPTY_DASH));
        mTextStatus.setText(mFixture.getStatus());
        if (mFixture.isFavorite()) {
            mViewFavorite.setImageResource(R.drawable.ic_star);
        }
        if (mFixture.isNotified()) {
            mViewNotification.setImageResource(R.drawable.ic_notifications);
        }
// start
        Date date = FDUtils.formatDateFromSQLite(mFixture.getDate());
        if (date != null) {
            long current = Calendar.getInstance().getTimeInMillis();
            long time = date.getTime();
            mTextStartDate.setText(FDUtils.formatMatchDateStart(mFixture.getDate()));
            if (current > time) {
                mTextDayHigh.setText(EMPTY_STRING);
                mTextDayMddle.setText(EMPTY_STRING);
                mTextDayLow.setText(EMPTY_STRING);
                mTextHourHigh.setText(EMPTY_STRING);
                mTextHourLow.setText(EMPTY_STRING);
                mTextMinHigh.setText(EMPTY_STRING);
                mTextMinLow.setText(EMPTY_STRING);
            } else {
                long delta = time - current;
                long days = delta / TimeUnit.DAYS.toMillis(1);
                delta = delta - days * TimeUnit.DAYS.toMillis(1);
                long hours = delta / TimeUnit.HOURS.toMillis(1);
                delta = delta - hours * TimeUnit.HOURS.toMillis(1);
                long minutes = delta / TimeUnit.MINUTES.toMillis(1);

                int[] a = getOrders((int)days);
                if (a[2] > 0) {
                    mTextDayHigh.setVisibility(View.VISIBLE);
                    mTextDayHigh.setText(FDUtils.formatFromInt(a[2], EMPTY_STRING));
                }
                mTextDayMddle.setText(FDUtils.formatFromInt(a[1], EMPTY_STRING));
                mTextDayLow.setText(FDUtils.formatFromInt(a[0], EMPTY_STRING));

                a = getOrders((int)hours);
                mTextHourHigh.setText(FDUtils.formatFromInt(a[1], EMPTY_STRING));
                mTextHourLow.setText(FDUtils.formatFromInt(a[0], EMPTY_STRING));

                a = getOrders((int)minutes);
                mTextMinHigh.setText(FDUtils.formatFromInt(a[1], EMPTY_STRING));
                mTextMinLow.setText(FDUtils.formatFromInt(a[0], EMPTY_STRING));


            }
        }
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

        mViewLeague.setPaintFlags(mViewLeague.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mViewLeague.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragmentLeague();
//                startActivityLeague(548);
            }
        });

        mViewTeamHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragmentTeam();
//                startActivityTeam(548);
            }
        });
        mViewTeamAway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragmentTeam();
//                startActivityTeam(535);
//                startActivityTeam(530);
            }
        });

        mViewNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// test!!!
//                Calendar c = Calendar.getInstance();
//                c.add(Calendar.SECOND, 60);
//                String dateSQLite = FDUtils.formatDateToSQLite(c.getTime());
//                FDFixture fixture = new FDFixture();
//                fixture.setDate(dateSQLite);
//                NotificationUtils.scheduleReminder(MatchFragment.this, fixture);
// test!!!
// TODO add flag for notification, database field for notification status, set and clear procedure
            }
        });

        mViewFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void restoreActionBar() {
        mActivity.getSupportActionBar().show();
        AppBarLayout appBarLayout = mActivity.getWindow().getDecorView().findViewById(R.id.app_bar);
        appBarLayout.getLayoutParams().height = mAppBarHeight;
    }

    private void setupActionBar(Bundle savedInstance) {
        mActivity.getSupportActionBar().hide();
        AppBarLayout appBarLayout = mActivity.getWindow().getDecorView().findViewById(R.id.app_bar);
        appBarLayout.getLayoutParams().height = 0;
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
