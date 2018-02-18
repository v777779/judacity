package ru.vpcb.footballassistant.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.vpcb.footballassistant.utils.FDUtils;

import static ru.vpcb.footballassistant.utils.Config.EMPTY_INT_VALUE;


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

    public FDTeam() {
        this.id = EMPTY_INT_VALUE;
    }

    public FDTeam(int id, String name, String code, String shortName,
                  String squadMarketValue, String crestURL) {
        this.id = id;
        this.name = name;
        this.code=code;
        this.shortName = shortName;
        this.squadMarketValue = squadMarketValue;
        this.crestURL = crestURL;
    }


     public void setId() throws NullPointerException, NumberFormatException {
        if(links.self != null) {
            id = FDUtils.formatHrefToId(links.self.getHref());
        }
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

        public FDLinks() {
            this.self = new FDLink();
            this.fixtures = new FDLink();
            this.players = new FDLink();
        }
    }


}
