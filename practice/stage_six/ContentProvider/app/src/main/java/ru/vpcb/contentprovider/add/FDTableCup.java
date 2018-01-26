package ru.vpcb.contentprovider.add;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.vpcb.contentprovider.data.FDStandingGroup;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 24-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */
class FDTableCup {

    @SerializedName("leagueCaption")
    @Expose
    private String leagueCaption;

    @SerializedName("matchday")
    @Expose
    private int matchDay;

    @SerializedName("standings")
    @Expose
    private List<FDStandingGroup> standings;

    private int id;

    public void setId(int id) throws NumberFormatException {
        this.id = id;
        if (id == -1) throw new NumberFormatException();
    }

    public int getId() {
        return id;
    }

    public List<FDStandingGroup> getStandings() {
        return standings;
    }
}
