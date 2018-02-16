package ru.vpcb.footballassistant.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static ru.vpcb.footballassistant.utils.Config.FD_REGEX_COMPETITIONS;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 24-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */
public class FDStanding implements PostProcessingEnabler.PostProcessable {
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

    public FDStanding() {
        this.id = -1;
    }

    @Override
    public void postProcess() {
        setId();
    }

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
// id
        String href = links.team.getHref();  // for standings only
        if(group == null && href != null ) {
            id = Integer.valueOf(href.substring(href.lastIndexOf("/") + 1));
            if (id == -1) throw new NumberFormatException();
        }
    }


    public int getId() {
        return id;
    }

    public String getGroup() {
        return group;
    }

    public int getRank() {
        return rank;
    }

    public String getTeam() {
        return team;
    }

    public int getPosition() {
        return position;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getCrestURI() {
        return crestURI;
    }

    public int getPlayedGames() {
        return playedGames;
    }

    public int getPoints() {
        return points;
    }

    public int getGoals() {
        return goals;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public int getGoalDifference() {
        return goalDifference;
    }

    public int getWins() {
        return wins;
    }

    public int getDraws() {
        return draws;
    }

    public int getLosses() {
        return losses;
    }

    public FDStat getHome() {
        return home;
    }

    public FDStat getAway() {
        return away;
    }
}
