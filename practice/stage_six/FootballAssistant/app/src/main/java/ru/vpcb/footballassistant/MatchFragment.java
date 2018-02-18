package ru.vpcb.footballassistant;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Binder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.vpcb.footballassistant.data.FDCompetition;
import ru.vpcb.footballassistant.data.FDFixture;
import ru.vpcb.footballassistant.data.FDTeam;
import ru.vpcb.footballassistant.notifications.NotificationUtils;
import ru.vpcb.footballassistant.utils.FDUtils;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_INTENT_LEAGUE_ID;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_INTENT_TEAM_ID;
import static ru.vpcb.footballassistant.utils.Config.FRAGMENT_TEAM_TAG;

public class MatchFragment extends Fragment {

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


    private View mRootView;
    private DetailActivity mActivity;
    private Context mContext;
    private int mAppBarHeight;
    private Unbinder mUnbinder;



    private Map<Integer,FDCompetition> mMap;
    private Map<Integer,FDTeam> mMapTeams;
    private Map<Integer,FDFixture> mMapFixtures;
    private FDFixture mFixture;
    private FDTeam mTeamHome;
    private FDTeam mTeamAway;





    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (DetailActivity) context;
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMap = mActivity.getMap();
        mMapTeams = mActivity.getMapTeams();  // already loaded by parent activity


        Bundle args =  getArguments();
        if(args != null)  {



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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.match_fragment, container, false);
        mUnbinder = ButterKnife.bind(this,mRootView);

        setupActionBar(savedInstanceState);
        setupListeners();
        bindViews();

        return mRootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        restoreActionBar();
        mUnbinder.unbind();
    }

    // methods

    private void bindViews() {



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
        if (savedInstance == null) {
            mAppBarHeight = appBarLayout.getHeight();
        }
        appBarLayout.getLayoutParams().height = 0;
    }

}
