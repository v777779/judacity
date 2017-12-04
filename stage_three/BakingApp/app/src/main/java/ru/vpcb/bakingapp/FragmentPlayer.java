package ru.vpcb.bakingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
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
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.vpcb.bakingapp.data.RecipeItem;
import ru.vpcb.bakingapp.video.IVideoEventCallback;
import ru.vpcb.bakingapp.video.VideoEventListener;
import timber.log.Timber;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static ru.vpcb.bakingapp.utils.RecipeUtils.clrText;
import static ru.vpcb.bakingapp.utils.RecipeUtils.getDescription;
import static ru.vpcb.bakingapp.utils.RecipeUtils.getRecipeName;
import static ru.vpcb.bakingapp.utils.RecipeUtils.getStepName;
import static ru.vpcb.bakingapp.utils.RecipeUtils.isOnline;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_PLAY_BACK_ENDED;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_PLAY_PAUSE_READY;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_PLAY_SEEK_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_PLAY_WINDOW_INDEX;
import static ru.vpcb.bakingapp.utils.Constants.FRAGMENT_PLAYER_NAME;
import static ru.vpcb.bakingapp.utils.Constants.PLAY_BUTTON_ANIMATION;
import static ru.vpcb.bakingapp.utils.Constants.PLAY_CONTROL_SHOWTIME;
import static ru.vpcb.bakingapp.utils.Constants.RECIPE_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.RECIPE_STEP_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.SYSTEM_UI_HIDE_FLAGS;
import static ru.vpcb.bakingapp.utils.Constants.SYSTEM_UI_SHOW_FLAGS;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 15-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */

/**
 * FragmentPlayer class shows Fragment  with Recipe.Step data
 */
public class FragmentPlayer extends Fragment implements IFragmentHelper, IVideoEventCallback {
    /**
     * The text with Step name
     */
    @Nullable
    @BindView(R.id.fp_head_text)
    TextView mHeadText;
    /**
     * The text with Step description
     */
    @Nullable
    @BindView(R.id.fp_body_text)
    TextView mBodyText;

    /**
     * Bottom left navigation button
     */
    @Nullable
    @BindView(R.id.prev_button)
    ImageView mPrevButton;
    /**
     * Bottom right navigation button
     */
    @Nullable
    @BindView(R.id.next_button)
    ImageView mNextButton;


    /**
     * The text with Step name between buttons
     */
    @Nullable
    @BindView(R.id.navigation_text)
    TextView mNavigationText;

    /**
     * FrameLayout that extends clickable area of left button
     */
    @Nullable
    @BindView(R.id.prev_button_extended)
    View mPrevExt;
    /**
     * FrameLayout that extends clickable area of right button
     */
    @Nullable
    @BindView(R.id.next_button_extended)
    View mNextExt;

    /**
     * ExoPlayer View object
     */
    @Nullable
    @BindView(R.id.exoplayer_view)
    SimpleExoPlayerView mPlayerView;

    /**
     * ExoPlayer Play button Overlay Image
     */
    @Nullable
    @BindView(R.id.ic_play_button)
    ImageView mPlayButton;
    /**
     * ExoPlayer Play Button Background Overlay Image
     */
    @Nullable
    @BindView(R.id.ic_play_button_back)
    ImageView mPlayButtonBack;
    /**
     * ExoPlayer Placeholder Image.
     */
    @Nullable
    @BindView(R.id.fp_video_card)
    View mCardView;

    /**
     * Current context of activity.
     */
    private Context mContext;
    /**
     * The RecipeItem parent object.
     */
    private RecipeItem mRecipeItem;
    /**
     * The List of RecipeItem Step objects.
     */
    private List<RecipeItem.Step> mStepList;
    /**
     * The current RecipeItem Step object.
     */
    private RecipeItem.Step mCurrentStep;
    /**
     * The position of current RecipeItem Step object in List.
     */
    private int mPosition;
    /**
     * The maximum position of RecipeItem Step object in List
     */
    private int mPositionMax;

    /**
     * The flag if screen smallest wide greater or even 550dp (true) or less (false)
     */
    private boolean mIsWide;

    /**
     * The flag is true when playback was ended
     */
    private boolean mPlaybackEnded;
    /**
     * The current position of player while playing video
     */
    private long mPlaybackPosition;
    /**
     * The flag of play or pause mode, is tru when player is playing
     */
    private boolean mPlaybackWhenReady;

    /**
     * The value of current window of player
     */
    private int mCurrentWindow;
    /**
     * ExoPlayer object
     */
    private SimpleExoPlayer mPlayer;
    /**
     * ExoPlayer Player.EventListener object
     */
    private VideoEventListener mVideoListener;
    /**
     * The string with URL of video source
     */
    private String mVideoURL;
    /**
     * The flag is true when video is enabled.
     * Video is enabled if connection to network exists and mVideoURL value is valid.
     */
    private boolean mIsVideoEnabled;
    /**
     * The flag is true if device orientation is landscape.
     * Used to set FULL_SCREEN mode for devices with smallest screen wide less than 550dp
     */
    private boolean mIsLandMode;
    /**
     * ButterKnife object, used to close all binds on destroy.
     */
    private Unbinder mUnBinder;

    /**
     * Constructor default.
     */
    public FragmentPlayer() {
    }

    /**
     * Returns new FragmentPlayer object
     * Creates Bundle with parameters, put it into fragment object
     * Bundle parameters
     * mRecipeItem     RecipeItem object converted to JSON format at RECIPE_POSITION
     * mPosition       position of current Step at RECIPE_STEP_POSITION
     *
     * @return FragmentPlayer object with bundle of parameters
     */
    public static FragmentPlayer newInstance(RecipeItem recipeItem, int position) {
        FragmentPlayer playerFragment = new FragmentPlayer();
        Bundle playerArgs = new Bundle();
        playerArgs.putString(RECIPE_POSITION, new Gson().toJson(recipeItem));
        playerArgs.putInt(RECIPE_STEP_POSITION, position);
//        playerArgs.putBoolean(RECIPE_SCREEN_WIDE, mIsWide);
        playerFragment.setArguments(playerArgs);
        return playerFragment;
    }
    /**
     * Create main View of FragmentPlayer.
     * Inflates layout of Fragment.
     * Extracts parameters from input Intent bundle.
     * Fill layout with parameters from Intent bundle.
     * Starts ExoPlayer playback.
     * Bundle parameters:<br>
     * mPosition    the position of current Step in List of steps.
     * mIsWide      the flag of current screen, true if screen is wide.
     * mRecipeItem  the parent RecipeItem object.
     * *
     *
     * @param inflater           LayoutInflater inflates layout of fragment
     * @param container          ViewGroup parent view
     * @param savedInstanceState Bundle with instance parameters
     *                           Bundle parameters:<br>
     *                           mCurrentWindow   int value used by ExoPlayer
     *                           mPlaybackPosition long value used by ExoPlayer
     *                           mPlaybackWhenReady  boolean flag shows if player is in play or pause mode
     *                           mPlaybackEnded boolean flag shows if playback was ended
     * @return View object of fragment
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_play, container, false);
        mUnBinder = ButterKnife.bind(this, rootView);

        Bundle playerArgs = getArguments();
//        mIsLandMode = isLandMode();
        mIsLandMode = getResources().getBoolean(R.bool.is_land);
        mIsWide = getResources().getBoolean(R.bool.is_wide);
        try {                                                               // null will be catch by Exception e
            mPosition = playerArgs.getInt(RECIPE_STEP_POSITION, 0);
//            mIsWide = playerArgs.getBoolean(RECIPE_SCREEN_WIDE, false);


            mRecipeItem = new Gson().fromJson(playerArgs.getString(RECIPE_POSITION, null), RecipeItem.class);
            mStepList = mRecipeItem.getSteps();
            mPositionMax = mStepList.size();
            if (mPositionMax < 0 || mPositionMax < mPosition) {
                mPositionMax = mPosition;
            }
        } catch (Exception e) {
            Timber.d(e.getMessage());
        }
        mVideoListener = new VideoEventListener(this);
// video
        if (savedInstanceState != null) {
            mCurrentWindow = savedInstanceState.getInt(BUNDLE_PLAY_WINDOW_INDEX, 0);
            mPlaybackPosition = savedInstanceState.getLong(BUNDLE_PLAY_SEEK_POSITION, 0);
            mPlaybackWhenReady = savedInstanceState.getBoolean(BUNDLE_PLAY_PAUSE_READY, true);
            mPlaybackEnded = savedInstanceState.getBoolean(BUNDLE_PLAY_BACK_ENDED, false);
//            if (!mPlaybackEnded) {
//                mPlayButton.setAlpha(0f);
//            }
        } else {
            mCurrentWindow = 0;
            mPlaybackPosition = 0;
            mPlaybackWhenReady = false;
            mPlaybackEnded = false;
        }

// text
        mCurrentStep = getCurrentStep();
        if (mHeadText != null && mRecipeItem != null) {
            mHeadText.setText(getRecipeName(mContext.getResources(), mRecipeItem));
        }
        setupNavTextView();
        setupNavButtons();
        setupBodyTextView();
        setVideoAccess();
        setupPlayButton();

        rootView.setSystemUiVisibility(SYSTEM_UI_SHOW_FLAGS);
        return rootView;
    }

    /**
     * Attaches parent activity context to this fragment.
     * Used to fill mContext with parent activity context.
     *
     * @param context
     */
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

    /**
     * Saves instance data to Bundle object
     * Bundle parameters:<br>
     * mCurrentWindow   int value used by ExoPlayer
     * mPlaybackPosition long value used by ExoPlayer
     * mPlaybackWhenReady  boolean flag shows if player is in play or pause mode
     * mPlaybackEnded boolean flag shows if playback was ended
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPlayer != null) {
            mPlaybackPosition = mPlayer.getContentPosition();
            mCurrentWindow = mPlayer.getCurrentWindowIndex();
            mPlaybackWhenReady = mPlayer.getPlayWhenReady();

            outState.putInt(BUNDLE_PLAY_WINDOW_INDEX, mCurrentWindow);
            outState.putLong(BUNDLE_PLAY_SEEK_POSITION, mPlayer.getContentPosition());
            outState.putBoolean(BUNDLE_PLAY_PAUSE_READY, mPlaybackWhenReady);
            outState.putBoolean(BUNDLE_PLAY_BACK_ENDED, mPlaybackEnded);
        }
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
        hideSystemUi();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnBinder.unbind();
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        if (!mIsLandMode || mIsWide) {
            return;
        }
        if (!mIsVideoEnabled) {
            getView().setSystemUiVisibility(SYSTEM_UI_HIDE_FLAGS);
        } else {
            mPlayerView.setSystemUiVisibility(SYSTEM_UI_HIDE_FLAGS);
        }
    }

    /**
     * Returns MediaSource object for input URL parameter
     *
     * @param sourceURI String  media URL
     * @return MediaSource object
     */
    private MediaSource buildMediaSource(String sourceURI) {
        MediaSource mediaSource = new ExtractorMediaSource(
                Uri.parse(sourceURI),
                new DefaultHttpDataSourceFactory("my_user_agent"),
                new DefaultExtractorsFactory(),
                null,
                null);
        return mediaSource;
    }

    /**
     * Checks if video is possible release ExoPlayer if not.
     * Set mIsVideoEnabled flag true if video is enabled.
     */
    private void setVideoAccess() {
        if (mCurrentStep == null || mCurrentStep.getVideoURL() == null ||
                mCurrentStep.getVideoURL().isEmpty() || !isOnline(mContext)) {
            releasePlayer();
            mPlayerView.setVisibility(View.INVISIBLE);
            mPlayButton.setImageResource(R.drawable.ic_play_circle_white_24dp);
            mPlayButton.setAlpha(1f);
            mPlayButtonBack.setAlpha(0f);
            mCardView.setBackgroundResource(R.drawable.cakes_020);


            mIsVideoEnabled = false;
            return;
        }
        mVideoURL = mCurrentStep.getVideoURL();
        mIsVideoEnabled = true;
    }

    /**
     * Initializes ExoPlayer object and attach Player.EventListener to ExoPlayer.
     */
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

    /**
     * Releases ExoPlayer object when video playing is not accessible or on stop of fragment activity.
     */
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

    /**
     * Callback method from IFragmentHelper interface object.
     * Replaces current FragmentPlayer when button prev or next clicked.
     * Used for not wide screens only, when there is FragmentPlayer on screen only.
     * And there are prev and next buttons in the bottom of layout.
     *
     * @param position int current position of Step object in List of RecipeItem Step
     */
    @Override
    public void onCallback(int position) {
        if (mIsWide) {
            return;
        }
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentPlayer playerFragment = FragmentPlayer.newInstance(mRecipeItem,position);

//        FragmentPlayer playerFragment = new FragmentPlayer();
//        Bundle playerArgs = new Bundle();
//
//        playerArgs.putString(RECIPE_POSITION, new Gson().toJson(mRecipeItem));
//        playerArgs.putInt(RECIPE_STEP_POSITION, position);
//        playerArgs.putBoolean(RECIPE_SCREEN_WIDE, mIsWide);
//        playerFragment.setArguments(playerArgs);

        fragmentManager.popBackStack(FRAGMENT_PLAYER_NAME, POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, playerFragment)
                .addToBackStack(FRAGMENT_PLAYER_NAME)
                .commit();

    }

    /**
     *  PlaceHolder for IFragmentHelper interface
     */
    @Override
    public void showError() {

    }

    /**
     *  Setup visibility if navigation buttons according to position
     *  In the first Step of the list the prev button is invisible, in the last Step the next one.
     */
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

    /**
     *  Setup navigation button callback methods
     *  onCallback() method the current FragmentPlayer replaced by the new one
     *  Checks and sets visibility of navigation buttons
     */
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

    /**
     *  Sets navigation bar text according to Step data
     */
    private void setupNavTextView() {
        if (mNavigationText == null) {
            return;
        }
        if (mCurrentStep == null) {
            mNavigationText.setText(getString(R.string.play_header_empty));

        } else {
            mNavigationText.setText(getStepName(mContext.getResources(), mCurrentStep));
        }
    }

    /**
     * Sets Step description text
     *
     */
    private void setupBodyTextView() {
        if (mBodyText == null) {
            return;
        }
        if (mCurrentStep == null) {
            mBodyText.setText(getString(R.string.play_body_error));
        } else {
            String stepText = getDescription(mContext.getResources(), mCurrentStep);
            if (stepText == null || stepText.isEmpty()) {
                stepText = getString(R.string.play_body_empty);
            }
            mBodyText.setText(stepText);
        }
    }

    /**
     *  Returns current Step object by the mPosition value
     *
     * @return RecipeItem.Step object
     */
    private RecipeItem.Step getCurrentStep() {
        if (mStepList == null || mPosition == 0 || mPosition > mStepList.size()) {
            return null;
        }
        return mStepList.get(mPosition - 1);
    }


    /**
     *  Sets ExoPlayer Overlay Image of Play Button visibility
     *  The overlay image is visible if video is Ended or not started.
     *  Performs smooth transition between states of visibility
     */
    private void setupPlayButton() {
        if (!mIsVideoEnabled) {
            return;
        }
        mPlayerView.setControllerAutoShow(false);
        mPlayerView.setControllerShowTimeoutMs(PLAY_CONTROL_SHOWTIME);

        if (mPlaybackWhenReady) mPlayButton.setAlpha(0f);
        else mPlayButton.setAlpha(1f);

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

//    private boolean isLandMode() {
//        return Configuration.ORIENTATION_LANDSCAPE == getResources().getConfiguration().orientation;
//    }

    /**
     *  Returns current screen orientation mode
     *
     * @return boolean flag of orientation, true if orientation is landscape
     */
    private boolean isLandMode() {
        DisplayMetrics dp = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dp);
        return dp.widthPixels > dp.heightPixels;
    }

    /**
     * Callback for ExoPlayer Player.EventListener.
     * Checks STATE_READY and  STATE_ENDED states of player
     * and sets visibility of overlay image
     *
     * @param playbackState
     */
    @Override
    public void setPlayState(int playbackState) {

        if (playbackState == Player.STATE_READY) {
            mPlaybackEnded = false;
        }

        mPlayButton.setVisibility(View.VISIBLE);
        if (playbackState == Player.STATE_ENDED) {
            mPlaybackWhenReady = false;
            mPlayButton.setAlpha(1f);
            mPlaybackEnded = true;
            mPlayerView.setControllerAutoShow(false);
        }
    }
}
