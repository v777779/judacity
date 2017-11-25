package ru.vpcb.bakingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.gson.Gson;

import java.util.List;

import ru.vpcb.bakingapp.video.IVideoEventCallback;
import ru.vpcb.bakingapp.video.VideoEventListener;

import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_PLAY_BACK_ENDED;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_PLAY_PAUSE_READY;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_PLAY_SEEK_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_PLAY_WINDOW_INDEX;
import static ru.vpcb.bakingapp.utils.Constants.PLAY_BUTTON_ANIMATION;
import static ru.vpcb.bakingapp.utils.Constants.PLAY_CONTROL_SHOWTIME;
import static ru.vpcb.bakingapp.utils.Constants.RECIPE_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.RECIPE_SCREEN_WIDE;
import static ru.vpcb.bakingapp.utils.Constants.RECIPE_STEP_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.TAG_FDETAIL;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 15-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */

public class FragmentPlayer extends Fragment implements IFragmentHelper, IVideoEventCallback {

    private List<String> mCardList;
    private RecyclerView mRecyclerView;
    private FragmentMainAdapter mRecyclerAdapter;
    private int mPosition;
    private int mPositionMax;

    TextView mNameText;
    TextView mHeadText;
    TextView mBodyText;
    ImageView mPrevButton;
    ImageView mNextButton;
    TextView mNavigationText;
    View mPrevExt;
    View mNextExt;
    SimpleExoPlayerView mPlayerView;
    private ImageView mPlayButton;

    private Context mContext;
    private RecipeItem mRecipeItem;
    private List<RecipeItem.Step> mStepList;
    private RecipeItem.Step mCurrentStep;
    private boolean mIsWide;

    private boolean mPlaybackEnded;
    private long mPlaybackPosition;
    private boolean mPlaybackWhenReady;
    private int mCurrentWindow;
    private SimpleExoPlayer mPlayer;
    private VideoEventListener mVideoListener;
    private String mVideoURL;
    private boolean mIsVideoEnabled;


    public FragmentPlayer() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_play, container, false);
        Bundle playerArgs = getArguments();
//        mPosition = 0;
//        mPositionMax = 0;
//        mIsWide = false;
//        mRecipeItem = null;
//        mStepList = null;
        try {                                                               // null will be catch by Exception e
            mPosition = playerArgs.getInt(RECIPE_STEP_POSITION, 0);
            mIsWide = playerArgs.getBoolean(RECIPE_SCREEN_WIDE, false);
            mRecipeItem = new Gson().fromJson(playerArgs.getString(RECIPE_POSITION, null), RecipeItem.class);

            mStepList = mRecipeItem.getSteps();
            mPositionMax = mStepList.size();
            if (mPositionMax < 0 || mPositionMax < mPosition) {
                mPositionMax = mPosition;
            }
        } catch (Exception e) {
            Log.d(TAG_FDETAIL, e.getMessage());
        }


        mHeadText = rootView.findViewById(R.id.fp_head_text);
        mBodyText = rootView.findViewById(R.id.fp_body_text);
        mPrevButton = rootView.findViewById(R.id.prev_button);
        mNextButton = rootView.findViewById(R.id.next_button);
        mNavigationText = rootView.findViewById(R.id.navigation_text);
        mPrevExt = rootView.findViewById(R.id.prev_button_extended);
        mNextExt = rootView.findViewById(R.id.next_button_extended);

        mPlayerView = rootView.findViewById(R.id.exoplayer_view);
        mPlayButton = rootView.findViewById(R.id.ic_play_button);
        mVideoListener = new VideoEventListener(this);
// video
        if (savedInstanceState != null) {
            mCurrentWindow = savedInstanceState.getInt(BUNDLE_PLAY_WINDOW_INDEX);
            mPlaybackPosition = savedInstanceState.getLong(BUNDLE_PLAY_SEEK_POSITION);
            mPlaybackWhenReady = savedInstanceState.getBoolean(BUNDLE_PLAY_PAUSE_READY);
            mPlaybackEnded = savedInstanceState.getBoolean(BUNDLE_PLAY_BACK_ENDED);
            if (!mPlaybackEnded) {
                mPlayButton.setAlpha(0f);
            }
        } else {
            mCurrentWindow = 0;
            mPlaybackPosition = 0;
            mPlaybackWhenReady = true;
            mPlaybackEnded = false;
        }
        setupPlayButton();
// text
        mCurrentStep = getCurrentStep();
        if (mHeadText != null && mRecipeItem != null) {
            mHeadText.setText(mRecipeItem.getName());
        }
        setupNavTextView();
        setupNavButtons();
        setupBodyTextView();
        setVideoAccess();

        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        try {
//            mFragmentCallback = (IFragmentCallback) context;
//        } catch (ClassCastException e) {
//            e.printStackTrace();
//        }
        mContext = context;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPlayer != null) {
            mPlaybackPosition = mPlayer.getContentPosition();
            mCurrentWindow = mPlayer.getCurrentWindowIndex();
            mPlaybackWhenReady = mPlayer.getPlayWhenReady();

        }

        outState.putInt(BUNDLE_PLAY_WINDOW_INDEX, mCurrentWindow);
        outState.putLong(BUNDLE_PLAY_SEEK_POSITION, mPlayer.getContentPosition());
        outState.putBoolean(BUNDLE_PLAY_PAUSE_READY, mPlaybackWhenReady);
        outState.putBoolean(BUNDLE_PLAY_BACK_ENDED, mPlaybackEnded);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isLandMode()) {
            hideSystemUi();
        }
        if ((Build.VERSION.SDK_INT <= 23 || mPlayer == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        if (!isLandMode()) {
            return;
        }
        mPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private MediaSource buildMediaSource(String sourceURI) {
        MediaSource mediaSource = new ExtractorMediaSource(
                Uri.parse(sourceURI),
                new DefaultHttpDataSourceFactory("my_user_agent"),
                new DefaultExtractorsFactory(),
                null,
                null);
        return mediaSource;
    }

    private void setVideoAccess() {
        if (mCurrentStep == null || mCurrentStep.getVideoURL() == null ||
                mCurrentStep.getVideoURL().isEmpty()) {
            releasePlayer();
            mPlayerView.setVisibility(View.INVISIBLE);
            mPlayButton.setImageResource(R.drawable.cakes_020);
            mPlayButton.setVisibility(View.VISIBLE);
            mIsVideoEnabled = false;
            return;
        }
        mVideoURL = mCurrentStep.getVideoURL();
        mIsVideoEnabled = true;
    }

    private void initializePlayer() {
        if (!mIsVideoEnabled) {
            return;
        }

        if (mPlayer == null) {
            mPlayer = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(mContext),
                    new DefaultTrackSelector(),
                    new DefaultLoadControl());
// listeners
            mPlayer.addListener(mVideoListener);

            mPlayerView.setPlayer(mPlayer);
            mPlayer.setPlayWhenReady(mPlaybackWhenReady);
            mPlayer.seekTo(mCurrentWindow, mPlaybackPosition);  // get info
        }

        MediaSource mediaSource = buildMediaSource(mVideoURL);
        mPlayer.prepare(mediaSource, true, false);
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            mPlaybackPosition = mPlayer.getContentPosition();
            mCurrentWindow = mPlayer.getCurrentWindowIndex();
            mPlaybackWhenReady = mPlayer.getPlayWhenReady();
// listeners

            mPlayer.removeListener(mVideoListener);
            mPlayer.release();

            mPlayer = null;

        }
    }

    @Override
    public void onCallback(int position) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentPlayer playerFragment = new FragmentPlayer();
        Bundle playerArgs = new Bundle();

        playerArgs.putString(RECIPE_POSITION, new Gson().toJson(mRecipeItem));
        playerArgs.putInt(RECIPE_STEP_POSITION, position);
        playerArgs.putBoolean(RECIPE_SCREEN_WIDE, mIsWide);
        playerFragment.setArguments(playerArgs);

        if (mIsWide) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fc_p_container, playerFragment)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, playerFragment)
                    .commit();
        }

//        Snackbar.make(getView(), "Clicked on Fragment Player " + " stack: " +
//                        fragmentManager.getBackStackEntryCount(),
//                Snackbar.LENGTH_SHORT).show();
    }


    @Override
    public List<FragmentDetailItem> getItemList() {
        return null;
    }

    @Override
    public int getSpanHeight() {
        return 0;
    }


    private void setupNavButtonsVisibility() {
        if (mPosition <= 1) {
            mPrevButton.setVisibility(View.INVISIBLE);
        } else {
            mPrevButton.setVisibility(View.VISIBLE);
        }
        if (mPosition >= mPositionMax) {
            mNextButton.setVisibility(View.INVISIBLE);
        } else {
            mNextButton.setVisibility(View.VISIBLE);
        }
    }

    private void setupNavButtons() {
        if (mPrevExt == null || mNextExt == null) {
            return;
        }
        setupNavButtonsVisibility();

        mPrevExt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosition > 1) {
                    mPosition--;
                    onCallback(mPosition);
                }
                setupNavButtonsVisibility();
            }
        });

        mNextExt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosition < mPositionMax) {
                    mPosition++;
                    onCallback(mPosition);

                }
                setupNavButtonsVisibility();
            }
        });
    }

    private void setupNavTextView() {
        if (mNavigationText == null) {
            return;
        }
        if (mCurrentStep == null) {
            mNavigationText.setText(getString(R.string.play_header_empty));

        } else {
            String stepText = getString(R.string.play_header_step, "" + mCurrentStep.getId());
            if (mPosition == 1) {
                stepText = getString(R.string.play_header_intro);
            }
            mNavigationText.setText(stepText);
        }
    }

    private void setupBodyTextView() {
        if (mBodyText == null) {
            return;
        }
        if (mCurrentStep == null) {
            mBodyText.setText(getString(R.string.play_body_error));
        } else {
            String stepText = mCurrentStep.getDescription();
            if (stepText == null || stepText.isEmpty()) {
                stepText = getString(R.string.play_body_empty);
            } else {
                stepText = stepText.replaceAll("[^\\x00-\\xBE]", "");  // clear from broken symbols
            }
            mBodyText.setText(stepText);
        }
    }

    private RecipeItem.Step getCurrentStep() {
        if (mStepList == null || mPosition == 0 || mPosition > mStepList.size()) {
            return null;
        }
        return mStepList.get(mPosition - 1);
    }


    private void setupPlayButton() {
        mPlayerView.setControllerAutoShow(false);
        mPlayerView.setControllerShowTimeoutMs(PLAY_CONTROL_SHOWTIME);
        mPlayButton.setAlpha(0f);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlaybackPosition = mPlayer.getCurrentPosition();
                mPlaybackWhenReady = !mPlaybackWhenReady;
                mPlayerView.showController();
                if (mPlaybackEnded) {
                    mPlayButton.setAlpha(1f);
                    mPlaybackPosition = 0;
                    mPlaybackWhenReady = true;
                }

                mPlayer.seekTo(mPlaybackPosition);
                mPlayer.setPlayWhenReady(mPlaybackWhenReady);
// transiton
                if (mPlaybackWhenReady == true) {  // in pause
                    mPlayButton.animate()
                            .alpha(0f)
                            .setDuration(PLAY_BUTTON_ANIMATION)
                            .setListener(null);

                }
            }
        });

    }


    private boolean isLandMode() {
        DisplayMetrics dp = new DisplayMetrics();
        ((MainActivity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dp);
        return dp.widthPixels > dp.heightPixels;
    }

    @Override
    public void setPlayState(int playbackState) {

        if (playbackState == Player.STATE_READY) {
            mPlaybackEnded = false;

//            if (!mPlaybackWhenReady) mPlayButton.setImageResource(R.drawable.ic_play_circle_24dp);
//            else mPlayButton.setImageResource(R.drawable.ic_pause_circle_24dp);
        }

        mPlayButton.setVisibility(View.VISIBLE);
        if (playbackState == Player.STATE_ENDED) {
            mPlaybackWhenReady = false;
//            mPlayButton.setImageResource(R.drawable.ic_play_circle_24dp);
            mPlayButton.setAlpha(1f);
            mPlaybackEnded = true;
            mPlayerView.setControllerAutoShow(false);
        }
    }
}
