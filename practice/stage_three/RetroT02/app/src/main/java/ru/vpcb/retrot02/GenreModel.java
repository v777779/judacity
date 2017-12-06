package ru.vpcb.retrot02;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 20-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */

public class GenreModel {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;


    /**
     * @return Site name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name Site name
     */
    public void setName(String name) {
        this.name = name;
    }



}
