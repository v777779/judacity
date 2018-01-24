package ru.vpcb.contentprovider.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import ru.vpcb.contentprovider.FDLink;

/**
 * Created by V1 on 24-Jan-18.
 */

public class FDLinksTemplate {
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
