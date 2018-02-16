package ru.vpcb.footballassistant.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import ru.vpcb.footballassistant.utils.FDUtils;

import static ru.vpcb.footballassistant.utils.Config.EMPTY_DASH;
import static ru.vpcb.footballassistant.utils.Config.EMPTY_INT_VALUE;
import static ru.vpcb.footballassistant.utils.Config.EMPTY_LONG_DASH;
import static ru.vpcb.footballassistant.utils.Config.EMPTY_STRING;
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

    // widgets
    private String competitionName;

    public FDFixture() {
        this.id = EMPTY_INT_VALUE;                  // id
        this.competitionId = EMPTY_INT_VALUE;       // id competition
        this.homeTeamId = EMPTY_INT_VALUE;          // id teamHome
        this.awayTeamId = EMPTY_INT_VALUE;          // id teamAway
    }

    public FDFixture(int id, int competitionId, int homeTeamId, int awayTeamId,
                     String date, String status, int matchDay, String homeTeamName,
                     String awayTeamName, int goalsHomeTeam, int goalsAwayTeam,
                     double homeWin, double draw, double awayWin) {
        this.id = id;
        this.competitionId= competitionId;
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;
        this.date = date;
        this.status = status;
        this.matchDay = matchDay;
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
        this.result = new FDResult(goalsHomeTeam, goalsAwayTeam);
        this.odds = new FDOdds(homeWin, draw, awayWin);
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


    public void setId()  {
        if (links == null) return;
        if (links.self != null) {
            this.id = FDUtils.formatId(links.self.getHref());                    // id
        }
        if (links.competition != null) {
            this.competitionId = FDUtils.formatId(links.competition.getHref());  // id competition
        }
        if (links.homeTeam != null) {
            this.homeTeamId = FDUtils.formatId(links.homeTeam.getHref());        // id teamHome
        }
        if (links.awayTeam != null) {
            this.awayTeamId = FDUtils.formatId(links.awayTeam.getHref());        // id teamAway
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


    // TODO check and set if null
    public String getDate() {
        return date;     // check and set if null
    }

    public String getStatus() {
        if (status == null || status.isEmpty()) return EMPTY_TEAM_NAME;
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


    @Override
    public void postProcess() {
        setId();
    }


    @Override
    public String toString() {
        return String.format("%s %s:%s %s", SimpleDateFormat.getDateTimeInstance(
                DateFormat.MEDIUM, DateFormat.SHORT).format(date),
                homeTeamName.trim(), awayTeamName.trim(), status);
    }
}
