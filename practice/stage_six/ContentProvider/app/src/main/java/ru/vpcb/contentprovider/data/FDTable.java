package ru.vpcb.contentprovider.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import static ru.vpcb.contentprovider.utils.Constants.FD_REGEX_COMPETITIONS;
import static ru.vpcb.contentprovider.utils.Constants.FD_REGEX_TEAMS;

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

   private int id;

    public class FDLinks {
        @SerializedName("self")
        @Expose
        private FDLink self;

        @SerializedName("competition")
        @Expose
        private FDLink competition;

    }

    public String getLinkCompetition() {
        return links.competition.getHref();
    }


    public void setId() throws NullPointerException, NumberFormatException {
        String href = getLinkCompetition();
        id = Integer.valueOf(href.replaceAll(FD_REGEX_COMPETITIONS, ""));
        if (id == -1) throw new NumberFormatException();
    }


    public int getId() {
        return id;
    }
}
