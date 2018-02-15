package ru.vpcb.notifications.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static ru.vpcb.notifications.Utils.Config.DATE_WIDGET_PATTERN;
import static ru.vpcb.notifications.Utils.Config.EMPTY_DASH;
import static ru.vpcb.notifications.Utils.Config.EMPTY_LONG_DASH;
import static ru.vpcb.notifications.Utils.Config.EMPTY_MATCH_TIME;


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
    private int competitionId;
    private int homeTeamId;
    private int awayTeamId;
    private String competitionName;


    public FDFixture() {
        this.id = -1;
    }

    public FDFixture(Date date) {  // for comparator
        this.id = -1;
        this.date = date;
        this.homeTeamName = "home_team";
        this.awayTeamName = "away_team";
        this.status = "demo";
    }

    public FDFixture(int id, Date date, String status, int matchday,
                     String homeTeamName, String awayTeamName,
                     int goalsHomeTeam, int goalsAwayTeam,
                     double homeWin, double draw, double awayWin, Date lastRefresh) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.matchday = matchday;
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
        this.result = new FDResult(goalsHomeTeam, goalsAwayTeam);
        this.odds = new FDOdds(homeWin, draw, awayWin);
        this.lastRefresh = lastRefresh;
    }

    @Override
    public void postProcess() {
        setId();
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


    public void setId() throws NullPointerException, NumberFormatException {
// id fixture
        String href = links.self.getHref();
        id = Integer.valueOf(href.substring(href.lastIndexOf("/") + 1));
        if (id == -1) throw new NumberFormatException();
// id competition
        href = links.competition.getHref();
        competitionId = Integer.valueOf(href.substring(href.lastIndexOf("/") + 1));
// id teamHome
        href = links.homeTeam.getHref();
        homeTeamId = Integer.valueOf(href.substring(href.lastIndexOf("/") + 1));
// id teamAway
        href = links.awayTeam.getHref();
        awayTeamId = Integer.valueOf(href.substring(href.lastIndexOf("/") + 1));

    }

    public void setLastRefresh(long lastRefresh) {
        this.lastRefresh = new Date(lastRefresh);
    }

    public void setCompetitionId(int competitionId) {

        this.competitionId = competitionId;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }


    public int getId() {
        return id;
    }

    public int getCompetitionId() {
        return competitionId;
    }

    public Date getDate() {
        return date;
    }

    public String getStatus() {
        if (status == null || status.isEmpty()) return EMPTY_LONG_DASH;
        return status.toUpperCase();
    }

    public int getMatchDay() {
        return matchday;
    }


    public String getHomeTeamName() {
        if (homeTeamName == null || homeTeamName.isEmpty()) {
            return EMPTY_LONG_DASH;
        }
        return homeTeamName;
    }

    public String getAwayTeamName() {
        if (awayTeamName == null || awayTeamName.isEmpty()) {
            return EMPTY_LONG_DASH;
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
        if (result == null) return -1;
        return result.goalsHomeTeam;
    }

    public int getGoalsAway() {
        if (result == null) return -1;
        return result.goalsAwayTeam;
    }

    public double getHomeWin() {
        if (odds == null) return -1;
        return odds.homeWin;
    }

    public double getDraw() {
        if (odds == null) return -1;
        return odds.draw;
    }

    public double getAwayWin() {
        if (odds == null) return -1;
        return odds.awayWin;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getLastRefresh() {
        return lastRefresh;
    }


    public String getCompetitionName() {
        return competitionName;
    }

    public String getMatchTime() {
        if (date == null) return EMPTY_LONG_DASH;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return String.format(Locale.ENGLISH,
                "%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
    }

    public String getMatchDate() {
        if (date == null) return EMPTY_LONG_DASH;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return String.format(Locale.ENGLISH,
                "%02d:%02d,%02d/%02d/%04d",
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1,
                c.get(Calendar.YEAR));
    }

    public String getMatchDateWidget() {

        if (date == null) return EMPTY_MATCH_TIME;
        SimpleDateFormat df = new SimpleDateFormat(DATE_WIDGET_PATTERN);
        return df.format(date);
    }

    public String getMatchScoreHome() {
        if (result == null || result.goalsHomeTeam < 0) return EMPTY_DASH;
        return String.valueOf(result.goalsHomeTeam);
    }

    public String getMatchScoreAway() {
        if (result == null || result.goalsAwayTeam < 0) return EMPTY_DASH;
        return String.valueOf(result.goalsAwayTeam);
    }


    public String getMatchScore() {
        if (result == null) return EMPTY_LONG_DASH;
        return String.format(Locale.ENGLISH, "%d : %d",
                result.goalsHomeTeam, result.goalsAwayTeam);
    }


    @Override
    public String toString() {
        return String.format("%s %s:%s %s", SimpleDateFormat.getDateTimeInstance(
                DateFormat.MEDIUM, DateFormat.SHORT).format(date),
                homeTeamName.trim(), awayTeamName.trim(), status);
    }
}
