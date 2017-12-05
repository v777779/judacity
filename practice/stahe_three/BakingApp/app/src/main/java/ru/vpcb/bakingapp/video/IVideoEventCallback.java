package ru.vpcb.bakingapp.video;


/**
 *  IVideoEventCallback interface for ExoPlayer Evevnt Listener
 *
 */
public interface IVideoEventCallback {
    /**
     * Callback methor to process state of ExoPlayer
     *
     * @param state int state of ExoPlayer
     */
    void setPlayState(int state);

}
