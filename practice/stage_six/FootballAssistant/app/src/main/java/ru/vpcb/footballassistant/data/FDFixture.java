package ru.vpcb.footballassistant.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import ru.vpcb.footballassistant.utils.FDUtils;

import static ru.vpcb.footballassistant.utils.Config.EMPTY_INT_VALUE;
import static ru.vpcb.footballassistant.utils.Config.EMPTY_LONG_DASH;
import static ru.vpcb.footballassistant.utils.Config.EMPTY_NOTIFICATION_ID;
import static ru.vpcb.footballassistant.utils.Config.EMPTY_TEAM_NAME;


/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 24-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */
public class FDFixture implements PostProcessingEnabler.PostProcessable {
    @SerializedName("_links")
    @Expose
    private FDLinks links;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("matchDay")
    @Expose
    private int matchDay;

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
    private int competitionId;
    private int homeTeamId;
    private int awayTeamId;

    // favorite
    private boolean isFavorite;

    // notification
    private boolean isNotified;
    private int notificationId;


    // widgets
    private String competitionName;

    public FDFixture() {
        this.id = EMPTY_INT_VALUE;                  // id
        this.competitionId = EMPTY_INT_VALUE;       // id competition
        this.homeTeamId = EMPTY_INT_VALUE;          // id teamHome
        this.awayTeamId = EMPTY_INT_VALUE;          // id teamAway

        this.isFavorite = false;
        this.isNotified = false;
        this.notificationId = EMPTY_NOTIFICATION_ID;

    }

    public FDFixture(int id, int competitionId, int homeTeamId, int awayTeamId,
                     String date, String status, int matchDay, String homeTeamName,
                     String awayTeamName, int goalsHomeTeam, int goalsAwayTeam,
                     double homeWin, double draw, double awayWin,
                     boolean isFavorite, boolean isNotified, int notificationId) {
        this.id = id;
        this.competitionId = competitionId;
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;
        this.date = date;
        this.status = status;
        this.matchDay = matchDay;
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
        this.result = new FDResult(goalsHomeTeam, goalsAwayTeam);
        this.odds = new FDOdds(homeWin, draw, awayWin);
        this.isFavorite = isFavorite;
        this.isNotified = isNotified;
        this.notificationId = notificationId;
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

        FDResult(int goalsHomeTeam, int goalsAwayTeam) {
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

        FDOdds(double homeWin, double draw, double awayWin) {
            this.homeWin = homeWin;
            this.draw = draw;
            this.awayWin = awayWin;
        }
    }


    public void setId() {
        if (links == null) return;
        if (links.self != null) {
            this.id = FDUtils.formatHrefToId(links.self.getHref());                    // id
        }
        if (links.competition != null) {
            this.competitionId = FDUtils.formatHrefToId(links.competition.getHref());  // id competition
        }
        if (links.homeTeam != null) {
            this.homeTeamId = FDUtils.formatHrefToId(links.homeTeam.getHref());        // id teamHome
        }
        if (links.awayTeam != null) {
            this.awayTeamId = FDUtils.formatHrefToId(links.awayTeam.getHref());        // id teamAway
        }

        date = FDUtils.formatDateToSQLite(date);

    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public int getCompetitionId() {
        return competitionId;
    }


    public String getDate() {
        return date;
    }

    public String getStatus() {
        if (status == null || status.isEmpty()) return EMPTY_LONG_DASH;
        return status.toUpperCase();
    }

    public int getMatchDay() {
        return matchDay;
    }


    public String getHomeTeamName() {
        if (homeTeamName == null || homeTeamName.isEmpty()) {
            return EMPTY_TEAM_NAME;
        }
        return homeTeamName;
    }

    public String getAwayTeamName() {
        if (awayTeamName == null || awayTeamName.isEmpty()) {
            return EMPTY_TEAM_NAME;
        }
        return awayTeamName;
    }

    public int getHomeTeamId() {
        return homeTeamId;
    }

    public int getAwayTeamId() {
        return awayTeamId;
    }

    public FDResult getResult() {
        return result;
    }

    public int getGoalsHome() {
        if (result == null) return EMPTY_INT_VALUE;
        return result.goalsHomeTeam;
    }

    public int getGoalsAway() {
        if (result == null) return EMPTY_INT_VALUE;
        return result.goalsAwayTeam;
    }

    public double getHomeWin() {
        if (odds == null) return EMPTY_INT_VALUE;
        return odds.homeWin;
    }

    public double getDraw() {
        if (odds == null) return EMPTY_INT_VALUE;
        return odds.draw;
    }

    public double getAwayWin() {
        if (odds == null) return EMPTY_INT_VALUE;
        return odds.awayWin;
    }


    // widgets
    public String getCompetitionName() {
        if (competitionName == null || competitionName.isEmpty()) return EMPTY_LONG_DASH;
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }


    public int getGoalsHomeTeam() {
        if (result == null || result.goalsHomeTeam < 0) return EMPTY_INT_VALUE;
        return result.goalsHomeTeam;
    }

    public int getGoalsAwayTeam() {
        if (result == null || result.goalsAwayTeam < 0) return EMPTY_INT_VALUE;
        return result.goalsAwayTeam;
    }

    // favorite
    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    // notification
    public boolean isNotified() {
        return isNotified;
    }

    public void setNotified(boolean notified) {
        isNotified = notified;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    @Override
    public void postProcess() {
        setId();
    }



}
