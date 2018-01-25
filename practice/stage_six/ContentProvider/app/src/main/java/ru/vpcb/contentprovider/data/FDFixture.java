package ru.vpcb.contentprovider.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import static ru.vpcb.contentprovider.utils.Constants.FD_REGEX_FIXTURES;
import static ru.vpcb.contentprovider.utils.Constants.FD_REGEX_TEAMS;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 24-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */
public class FDFixture {
    @SerializedName("_links")
    @Expose
    private FDLinks links;

    @SerializedName("date")
    @Expose
    private Date date;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("matchday")
    @Expose
    private int matchday;

    @SerializedName("homeTeamName")
    @Expose
    private String homeTeamName;

    @SerializedName("awayTeamName")
    @Expose
    private String awayTeamName;

    @SerializedName("result")
    @Expose
    private FDResult result;

    @SerializedName("odds")
    @Expose
    private FDOdds odds;

    private int id;

    private class FDLinks {
        @SerializedName("self")
        @Expose
        private FDLink self;

        @SerializedName("competition")
        @Expose
        private FDLink competition;

        @SerializedName("homeTeam")
        @Expose
        private FDLink homeTeam;

        @SerializedName("awayTeam")
        @Expose
        private FDLink awayTeam;

    }

    private class FDHalfTime {
        @SerializedName("goalsHomeTeam")
        @Expose
        private int goalsHomeTeam;

        @SerializedName("goalsAwayTeam")
        @Expose
        private int goalsAwayTeam;
    }


    private class FDResult {

        @SerializedName("goalsHomeTeam")
        @Expose
        private int goalsHomeTeam;

        @SerializedName("goalsAwayTeam")
        @Expose
        private int goalsAwayTeam;

        @SerializedName("halfTime")
        @Expose
        private FDHalfTime halfTime;

    }

    private class FDOdds {
        @SerializedName("homeWin")
        @Expose
        private double homeWin;

        @SerializedName("draw")
        @Expose
        private double draw;

        @SerializedName("awayWin")
        @Expose
        private double awayWin;

    }

    public String getLinkSelf() {
        return links.self.getHref();
    }


    public void setId() throws NullPointerException, NumberFormatException {
        String href = getLinkSelf();
        id = Integer.valueOf(href.replaceAll(FD_REGEX_FIXTURES, ""));
        if (id == -1) throw new NumberFormatException();
    }

    public int getId() {
        return id;
    }


    @Override
    public String toString() {
        return "FDFixture{" +
                "date=" + date +
                ", homeTeamName='" + homeTeamName + '\'' +
                ", awayTeamName='" + awayTeamName + '\'' +
                '}';
    }
}
