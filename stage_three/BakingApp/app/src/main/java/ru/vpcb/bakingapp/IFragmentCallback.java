package ru.vpcb.bakingapp;

import java.util.List;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 15-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */

public interface IFragmentCallback {
    RecipeItem getRecipe(int position);
    void setRecipeList(List<RecipeItem> list);

}
