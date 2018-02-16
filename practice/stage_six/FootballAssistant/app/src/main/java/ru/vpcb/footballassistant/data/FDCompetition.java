package ru.vpcb.footballassistant.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import static ru.vpcb.footballassistant.utils.Config.EMPTY_LONG_DASH;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 24-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */
public class FDCompetition {

    @SerializedName("_links")
    @Expose
    private FDLinks links;

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("caption")
    @Expose
    private String caption;

    @SerializedName("league")
    @Expose
    private String league;

    @SerializedName("year")
    @Expose
    private String year;

    @SerializedName("currentMatchday")
    @Expose
    private int currentMatchDay;

    @SerializedName("numberOfMatchdays")
    @Expose
    private int numberOfMatchDays;

    @SerializedName("numberOfTeams")
    @Expose
    private int numberOfTeams;

    @SerializedName("numberOfGames")
    @Expose
    private int numberOfGames;

    @SerializedName("lastUpdated")
    @Expose
    private Date lastUpdated;

    private Date lastRefresh;

    private List<FDTeam> teams;
    private List<FDFixture> fixtures;
    private FDTable table;


    public FDCompetition() {
    }

    public FDCompetition(int id, String caption, String league, String year,
                         int currentMatchDay, int numberOfMatchDays,
                         int numberOfTeams, int numberOfGames,
                         Date lastUpdated, Date lastRefresh) {

        this.links = null;
        this.id = id;
        this.caption = caption;
        this.league = league;
        this.year = year;
        this.currentMatchDay = currentMatchDay;
        this.numberOfMatchDays = numberOfMatchDays;
        this.numberOfTeams = numberOfTeams;
        this.numberOfGames = numberOfGames;
        this.lastUpdated = lastUpdated;
        this.lastRefresh = lastRefresh;
        this.teams = null;
        this.fixtures = null;
    }

    public class FDLinks {
        @SerializedName("self")
        @Expose
        private FDLink self;

        @SerializedName("teams")
        @Expose
        private FDLink teams;

        @SerializedName("fixtures")
        @Expose
        private FDLink fixtures;

        @SerializedName("leagueTable")
        @Expose
        private FDLink table;

    }

    public void setLastRefresh(Date lastRefresh) {
        this.lastRefresh = lastRefresh;
    }

    public int getId() {
        if (id <= 0) throw new NumberFormatException();
        return id;
    }

    public String getCaption() {
        if (caption == null || caption.isEmpty()) return EMPTY_LONG_DASH;
        return caption;
    }

    public List<FDTeam> getTeams() {
        return teams;
    }

    public void setTeams(List<FDTeam> teams) {
        this.teams = teams;
    }

    public List<FDFixture> getFixtures() {
        return fixtures;
    }

    public void setFixtures(List<FDFixture> fixtures) {
        this.fixtures = fixtures;
    }

    public FDTable getTable() {
        return table;
    }

    public void setTable(FDTable table) {
        this.table = table;
    }

    public String getLeague() {
        if (league == null || league.isEmpty()) return EMPTY_LONG_DASH;
        return league;
    }

    public String getYear() {
        return year;
    }

    public int getCurrentMatchDay() {
        return currentMatchDay;
    }

    public int getNumberOfMatchDays() {
        return numberOfMatchDays;
    }

    public int getNumberOfTeams() {
        return numberOfTeams;
    }

    public int getNumberOfGames() {
        return numberOfGames;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public Date getLastRefresh() {
        return lastRefresh;
    }
}
