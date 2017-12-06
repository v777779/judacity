package ru.vpcb.exot5;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;


public class MainActivity extends AppCompatActivity implements IPlayerCallback {

    private static final String mediaURI = "https://d17h27t6h515a5.cloudfront.net/topher/" +
            "2017/April/58ffd974_-intro-creampie/-intro-creampie.mp4";
    private static final String localURI = "asset:///intro-creampie.mp4";
    private static final String videoURI = "http://download.blender.org/peach/bigbuckbunny_movies/BigBuckBunny_320x180.mp4";
    private static final String audioURI = "http://storage.googleapis.com/exoplayer-test-media-0/play.mp3";
    private static final String dashURI =
            "http://www.youtube.com/api/manifest/dash/id/bf5bb2419360daf1/source/" +
                    "youtube?as=fmp4_audio_clear,fmp4_sd_hd_clear&sparams=ip,ipbits,expire,source," +
                    "id,as&ip=0.0.0.0&ipbits=0&expire=19000000000&signature=" +
                    "51AF5F39AB0CEC3E5497CD9C900EBFEAECCCB5C7.8506521BFC350652163895D4C26DEE124209AA9E&key=ik0";

    private SimpleExoPlayerView mPlayerView;
    private SimpleExoPlayer mPlayer;
    private DefaultBandwidthMeter mBandwidthMeter;
    private int mCurrentWindow;
    private long mPlaybackPosition;
    private boolean mPlaybackWhenReady = true;
    private ExoListener mExoListener;
    private ImageView mPlayButton;
    private boolean mPlaybackEnded;
    // animation
    private int mAnimationTime;
    private boolean isLandscape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPlayButton = (ImageView) findViewById(R.id.ic_play_button);
        mPlayerView = (SimpleExoPlayerView) findViewById(R.id.exoplayer_view);
        mBandwidthMeter = new DefaultBandwidthMeter();
        mExoListener = new ExoListener(this);

        if (savedInstanceState != null) {
            mCurrentWindow = savedInstanceState.getInt("window_index");
            mPlaybackPosition = savedInstanceState.getLong("seek_position");
            mPlaybackWhenReady = savedInstanceState.getBoolean("pause_ready");
            mPlaybackEnded = savedInstanceState.getBoolean("playback_ended");
            if (!mPlaybackEnded)
                mPlayButton.setAlpha(0f);

        } else {
            mCurrentWindow = 0;
            mPlaybackPosition = 0;
            mPlaybackWhenReady = false;
            mPlaybackEnded = false;
        }
// mode
        DisplayMetrics dp = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dp);
        isLandscape = dp.widthPixels < dp.heightPixels;


// animation
        mAnimationTime = 500; // ms
//        CustomLayout cl = (CustomLayout) findViewById(R.id.child_constraint);  // FrameLayout
        mPlayerView.setControllerAutoShow(false);
        mPlayerView.setControllerShowTimeoutMs(2500);

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
                            .setDuration(mAnimationTime)
                            .setListener(null);

                }
            }
        });

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPlayer != null) {
            mPlaybackPosition = mPlayer.getContentPosition();
            mCurrentWindow = mPlayer.getCurrentWindowIndex();
            mPlaybackWhenReady = mPlayer.getPlayWhenReady();

        }
        outState.putInt("window_index", mCurrentWindow);
        outState.putLong("seek_position", mPlayer.getContentPosition());
        outState.putBoolean("pause_ready", mPlaybackWhenReady);
        outState.putBoolean("playback_ended", mPlaybackEnded);
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

    private MediaSource buildAssetSource(String sourceURI) {

        MediaSource mediaSource = new ExtractorMediaSource(
                Uri.parse(sourceURI),
                new DefaultDataSourceFactory(this, "my_user_agent"),
                new DefaultExtractorsFactory(),
                null,
                null);
        return mediaSource;
    }

    private MediaSource buildMediaSource(String videoURI, String audioURI, String mediaURI) {
        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("my_user_agent");

        ExtractorMediaSource videoSource = new ExtractorMediaSource(
                Uri.parse(videoURI), dataSourceFactory, extractorsFactory, null, null);

        ExtractorMediaSource audioSource = new ExtractorMediaSource(
                Uri.parse(audioURI), dataSourceFactory, extractorsFactory, null, null);

        ExtractorMediaSource videoSource2 = new ExtractorMediaSource(
                Uri.parse(mediaURI), dataSourceFactory, extractorsFactory, null, null);


        MediaSource mediaSource = new ConcatenatingMediaSource(
                new ExtractorMediaSource[]{videoSource, audioSource, videoSource2});
        return mediaSource;
    }

    private MediaSource buildStreamSource(String dashURI) {

        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        DataSource.Factory dataSourceFactory =
                new DefaultHttpDataSourceFactory("my_user_agent", mBandwidthMeter);
        DashChunkSource.Factory dashChunkFactory =
                new DefaultDashChunkSource.Factory(dataSourceFactory);

        MediaSource mediaSource = new DashMediaSource(
                Uri.parse(dashURI), dataSourceFactory, dashChunkFactory, null, null);

        return mediaSource;
    }

    private void initializePlayer() {

        if (mPlayer == null) {
            mPlayer = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(this),
                    new DefaultTrackSelector(),
                    new DefaultLoadControl());
// listeners
            mPlayer.addListener(mExoListener);
            mPlayer.addVideoListener(mExoListener);
            mPlayer.setVideoDebugListener(mExoListener);
            mPlayer.setAudioDebugListener(mExoListener);

            mPlayerView.setPlayer(mPlayer);
            mPlayer.setPlayWhenReady(mPlaybackWhenReady);
            mPlayer.seekTo(mCurrentWindow, mPlaybackPosition);  // get info
        }

        MediaSource mediaSource = buildAssetSource(localURI);
        mPlayer.prepare(mediaSource, true, false);
    }

    private void initializeStreamPlayer() {
        if (mPlayer == null) {
            TrackSelection.Factory adaptiveTrack =
                    new AdaptiveTrackSelection.Factory(mBandwidthMeter);

            mPlayer = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(this),
                    new DefaultTrackSelector(adaptiveTrack), //new DefaultTrackSelector(),
                    new DefaultLoadControl());
// listeners
            mPlayer.addListener(mExoListener);
            mPlayer.addVideoListener(mExoListener);
            mPlayer.setVideoDebugListener(mExoListener);
            mPlayer.setAudioDebugListener(mExoListener);

            mPlayerView.setPlayer(mPlayer);
            mPlayer.setPlayWhenReady(mPlaybackWhenReady);
            mPlayer.seekTo(mCurrentWindow, mPlaybackPosition);  // get info
        }

        MediaSource mediaSource = buildStreamSource(dashURI);
        mPlayer.prepare(mediaSource, true, false);
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            mPlaybackPosition = mPlayer.getContentPosition();
            mCurrentWindow = mPlayer.getCurrentWindowIndex();
            mPlaybackWhenReady = mPlayer.getPlayWhenReady();
// listeners

            mPlayer.removeListener(mExoListener);
            mPlayer.removeVideoListener(mExoListener);
            mPlayer.setVideoDebugListener(null);
            mPlayer.setAudioDebugListener(null);
            mPlayer.release();

            mPlayer = null;

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
        if (!isLandscape) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        mPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
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

    @Override
    public void setPauseState() {

        mPlayButton.setImageResource(R.drawable.ic_pause_circle_24dp);
        mPlayButton.setVisibility(View.VISIBLE);
    }
}
