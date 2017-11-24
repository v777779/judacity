package ru.vpcb.bakingapp.video;

import android.util.Log;
import android.view.Surface;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

/**
 * Created by V1 on 18-Nov-17.
 */

public class VideoEventListener implements Player.EventListener {
//    private static final String TAG = VideoEventListener.class.getSimpleName();
    private IVideoEventCallback mCallback;

    public VideoEventListener(IVideoEventCallback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        String stateString;
        mCallback.setPlayState(playbackState);

        switch (playbackState) {
            case Player.STATE_IDLE:
                stateString = "ExoPlayer.STATE_IDLE      -";
                break;
            case Player.STATE_BUFFERING:
                stateString = "ExoPlayer.STATE_BUFFERING -";
                break;
            case Player.STATE_READY:
                stateString = "ExoPlayer.STATE_READY     -";
                break;
            case Player.STATE_ENDED:
                stateString = "ExoPlayer.STATE_ENDED     -";

                break;
            default:
                stateString = "UNKNOWN_STATE             -";
                break;
        }
//        Log.d(TAG, "changed state to " + stateString + " playWhenReady: " + playWhenReady);
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }

    @Override
    public void onPositionDiscontinuity() {
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }


}
