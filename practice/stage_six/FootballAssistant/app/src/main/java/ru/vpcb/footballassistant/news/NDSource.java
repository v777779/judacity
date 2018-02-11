package ru.vpcb.footballassistant.news;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static ru.vpcb.footballassistant.utils.Config.EMPTY_DASH;
import static ru.vpcb.footballassistant.utils.Config.EMPTY_LONG_DASH;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 11-Feb-18
 * Email: vadim.v.voronov@gmail.com
 */
public class NDSource {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;



    public NDSource(String id, String name) {
        this.id = id;
        this.name = name;
    }


    public String getId() {
        if (id == null || id.isEmpty())return EMPTY_DASH;
        return id;
    }

    public String getName() {
        if (name == null || name.isEmpty()) return EMPTY_LONG_DASH;
        return name;
    }

}
