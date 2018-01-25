package ru.vpcb.contentprovider.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 24-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */
public class FDCompetitions {

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
}
