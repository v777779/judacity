package ru.vpcb.retrot02;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 20-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */

public class GenreArray {
    @SerializedName("genres")
    @Expose
    private List<GenreModel> genres;


    public List<GenreModel> getGenres() {
        return genres;
    }
}
