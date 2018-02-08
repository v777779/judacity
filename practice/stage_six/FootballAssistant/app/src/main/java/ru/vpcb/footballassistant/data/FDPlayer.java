package ru.vpcb.footballassistant.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 24-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */
public class FDPlayer {
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("position")
    @Expose
    private String position;

    @SerializedName("jerseyNumber")
    @Expose
    private int jerseyNumber;

    @SerializedName("dateOfBirth")
    @Expose
    private Date dateOfBirth;

    @SerializedName("nationality")
    @Expose
    private String nationality;

    @SerializedName("contractUntil")
    @Expose
    private Date contractUntil;

    @SerializedName("marketValue")
    @Expose
    private String marketValue;

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public int getJerseyNumber() {
        return jerseyNumber;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public String getNationality() {
        return nationality;
    }

    public Date getContractUntil() {
        return contractUntil;
    }

    public String getMarketValue() {
        return marketValue;
    }
}
