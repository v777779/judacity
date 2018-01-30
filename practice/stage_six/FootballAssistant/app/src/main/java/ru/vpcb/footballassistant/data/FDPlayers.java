package ru.vpcb.footballassistant.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import static ru.vpcb.footballassistant.utils.Constants.FD_REGEX_TEAMS;


/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 24-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */
public class FDPlayers {
    @SerializedName("_links")
    @Expose
    private FDLinks links;

    @SerializedName("count")
    @Expose
    private int count;

    private int id;

    @SerializedName("players")
    @Expose
    private List<FDPlayer> players;

    public FDPlayers() {
        this.id = -1;
    }

    public class FDLinks {
        @SerializedName("self")
        @Expose
        private FDLink self;

        @SerializedName("team")
        @Expose
        private FDLink team;

    }

    public String getLinkTeam() {
        return links.team.getHref();
    }


    public void setId() throws NullPointerException, NumberFormatException {
        String href = getLinkTeam();
        id = Integer.valueOf(href.replaceAll(FD_REGEX_TEAMS, ""));
        if (id == -1) throw new NumberFormatException();
    }


    public int getId() {
        return id;
    }

    public List<FDPlayer> getPlayers() {
        return players;
    }
}
