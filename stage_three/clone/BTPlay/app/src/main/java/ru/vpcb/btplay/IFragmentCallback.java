package ru.vpcb.btplay;

import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 15-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */

public interface IFragmentCallback {
    void onCallback(int position);
    List<RecipeItem> getRecipeList();
    List<FragmentDetailItem> getItemList();
    int getSpanHeight();

}