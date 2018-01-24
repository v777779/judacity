package ru.vpcb.contentprovider.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import ru.vpcb.contentprovider.FDLink;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 24-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */
public class FDTeam {
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

    public class FDLinks {
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
