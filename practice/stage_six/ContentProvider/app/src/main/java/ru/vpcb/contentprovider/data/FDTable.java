package ru.vpcb.contentprovider.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 24-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */
public class FDTable {
    @SerializedName("_links")
    @Expose
    private FDLinks links;

    @SerializedName("matchday")
    @Expose
    private int matchDay;

    @SerializedName("leagueCaption")
    @Expose
    private String leagueCaption;

    @SerializedName("standing")
    @Expose
    private List<FDStanding> standing;


    public class FDLinks {
        @SerializedName("self")
        @Expose
        private FDLink self;

        @SerializedName("competition")
        @Expose
        private FDLink competition;

    }
}
