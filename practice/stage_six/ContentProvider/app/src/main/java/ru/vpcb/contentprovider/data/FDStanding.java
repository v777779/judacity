package ru.vpcb.contentprovider.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static ru.vpcb.contentprovider.utils.Constants.FD_REGEX_COMPETITIONS;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 24-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */
public class FDStanding {
    @SerializedName("_links")
    @Expose
    private FDLinks links;

    // cup
    @SerializedName("group")
    @Expose
    private String group;

    @SerializedName("rank")
    @Expose
    private int rank;

    @SerializedName("team")
    @Expose
    private String team;

    @SerializedName("teamId")
    @Expose
    private int id;
// end cup

    @SerializedName("position")
    @Expose
    private int position;

    @SerializedName("teamName")
    @Expose
    private String teamName;

    @SerializedName("crestURI")
    @Expose
    private String crestURI;

    @SerializedName("playedGames")
    @Expose
    private int playedGames;

    @SerializedName("points")
    @Expose
    private int points;

    @SerializedName("goals")
    @Expose
    private int goals;

    @SerializedName("goalsAgainst")
    @Expose
    private int goalsAgainst;


    @SerializedName("goalDifference")
    @Expose
    private int goalDifference;

    @SerializedName("wins")
    @Expose
    private int wins;

    @SerializedName("draws")
    @Expose
    private int draws;

    @SerializedName("losses")
    @Expose
    private int losses;

    @SerializedName("home")
    @Expose
    private FDStat home;

    @SerializedName("away")
    @Expose
    private FDStat away;


    public class FDLinks {
        @SerializedName("team")
        @Expose
        private FDLink team;
    }

    private class FDStat {
        @SerializedName("goals")
        @Expose
        private int goals;

        @SerializedName("goalsAgainst")
        @Expose
        private int goalsAgainst;

        @SerializedName("wins")
        @Expose
        private int wins;

        @SerializedName("draws")
        @Expose
        private int draws;

        @SerializedName("losses")
        @Expose
        private int losses;
    }

    public String getLinkTeam() {
        return links.team.getHref();
    }


    public void setId() throws NullPointerException, NumberFormatException {
        String href = getLinkTeam();
        id = Integer.valueOf(href.replaceAll(FD_REGEX_COMPETITIONS, ""));
        if (id == 0) throw new NumberFormatException();
    }


    public int getId() {
        return id;
    }

}
