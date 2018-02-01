package ru.vpcb.footballassistant.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import static ru.vpcb.footballassistant.utils.Config.FD_REGEX_FIXTURES;


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
    private Date lastRefresh;


    public FDFixture() {
        this.id = -1;
    }


    public FDFixture( int id, Date date, String status, int matchday,
                     String homeTeamName, String awayTeamName,
                      int goalsHomeTeam,int goalsAwayTeam,
                     double homeWin, double draw, double awayWin, Date lastRefresh ) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.matchday = matchday;
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
        this.result = new FDResult(goalsHomeTeam,goalsAwayTeam);
        this.odds = new FDOdds(homeWin,draw,awayWin);
        this.lastRefresh = lastRefresh;
    }


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

        public FDResult(int goalsHomeTeam, int goalsAwayTeam) {
            this.goalsHomeTeam = goalsHomeTeam;
            this.goalsAwayTeam = goalsAwayTeam;
            this.halfTime = null;
        }
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

        public FDOdds(double homeWin, double draw, double awayWin) {
            this.homeWin = homeWin;
            this.draw = draw;
            this.awayWin = awayWin;
        }
    }

    public String getLinkSelf() {
        return links.self.getHref();
    }


    public void setId() throws NullPointerException, NumberFormatException {
        String href = getLinkSelf();
        id = Integer.valueOf(href.replaceAll(FD_REGEX_FIXTURES, ""));
        if (id == -1) throw new NumberFormatException();
    }

    public void setLastRefresh(long lastRefresh) {
        this.lastRefresh = new Date(lastRefresh);
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public int getMatchday() {
        return matchday;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public FDResult getResult() {
        return result;
    }

    public int getGoalsHome() {
        if(result == null) return -1;
        return result.goalsHomeTeam;
    }

    public int getGoalsAway() {
        if(result == null) return -1;
        return result.goalsAwayTeam;
    }

    public double getHomeWin() {
        if(odds == null) return -1;
        return odds.homeWin;
    }

    public double getDraw() {
        if(odds == null) return -1;
        return odds.draw;
    }

    public double getAwayWin() {
        if(odds == null) return -1;
        return odds.awayWin;
    }

    public Date getLastRefresh() {
        return lastRefresh;
    }
}
