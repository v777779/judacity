package ru.vpcb.notifications.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import static ru.vpcb.notifications.Utils.Config.EMPTY_TEAM_NAME;
import static ru.vpcb.notifications.Utils.Config.FD_REGEX_TEAMS;


/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 24-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */
public class FDTeam implements PostProcessingEnabler.PostProcessable {
    @SerializedName("_links")
    @Expose
    private FDLinks links;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("shortName")
    @Expose
    private String shortName;

    @SerializedName("squadMarketValue")
    @Expose
    private String squadMarketValue;

    @SerializedName("crestUrl")
    @Expose
    private String crestURL;

    private int id;
    private List<FDPlayer> players;
    private Date lastRefresh;

    public FDTeam() {
        id = -1;
    }


    public FDTeam(int id, String name, String code, String shortName,
                  String squadMarketValue, String crestURL, long lastRefresh) {
        this.id = id;
        this.name = name;
        this.code=code;
        this.shortName = shortName;
        this.squadMarketValue = squadMarketValue;
        this.crestURL = crestURL;
        this.lastRefresh = new Date(lastRefresh);
    }




    public String getLinkSelf() {
        return links.self.getHref();
    }


    public void setId() throws NullPointerException, NumberFormatException {
        // id team
        String href = links.self.getHref();
        id = Integer.valueOf(href.substring(href.lastIndexOf("/") + 1));
        if (id == -1) throw new NumberFormatException();


    }

    public void setLastRefresh(long lastRefresh) {
        this.lastRefresh = new Date(lastRefresh);
    }

    public int getId() {
        return id;
    }

    public List<FDPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<FDPlayer> players) {
        this.players = players;
    }

    public String getName() {
        if(name == null || name.isEmpty())return EMPTY_TEAM_NAME;
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getShortName() {
        return shortName;
    }

    public String getSquadMarketValue() {
        return squadMarketValue;
    }

    public String getCrestURL() {
        return crestURL;
    }

    public Date getLastRefresh() {
        return lastRefresh;
    }

    @Override
    public void postProcess() {
        setId();
    }

    // classes
    private class FDLinks {
        @SerializedName("self")
        @Expose
        private FDLink self;

        @SerializedName("fixtures")
        @Expose
        private FDLink fixtures;

        @SerializedName("players")
        @Expose
        private FDLink players;


    }


}
