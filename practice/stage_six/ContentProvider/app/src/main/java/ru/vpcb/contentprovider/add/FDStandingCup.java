package ru.vpcb.contentprovider.add;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static ru.vpcb.contentprovider.utils.Constants.FD_REGEX_COMPETITIONS;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 24-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */
class FDStandingCup {
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

    @SerializedName("playedGames")
    @Expose
    private int playedGames;

    @SerializedName("crestURI")
    @Expose
    private String crestURI;


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


    public int getId() {
        return id;
    }

}
