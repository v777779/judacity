package ru.vpcb.btplay;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static ru.vpcb.btplay.utils.Constants.CHILD_TYPE;
import static ru.vpcb.btplay.utils.Constants.COLLAPSED_TYPE;
import static ru.vpcb.btplay.utils.Constants.EXPANDED_TYPE;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 15-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */

public class FragmentDetailAdapter extends RecyclerView.Adapter<FragmentDetailAdapter.FCViewHolder> {

    private List<FragmentDetailItem> mItemList;
    private boolean isExpanded;
    private RecipeItem mRecipeItem;
    private IFragmentHelper mHelper;

    private List<RecipeItem.Step> mStepList;
    private List<RecipeItem.Ingredient> mIngredientList;
    private Context mContext;

    public FragmentDetailAdapter(Context context, IFragmentHelper helper, RecipeItem recipeItem) {
        mContext = context;
        mHelper = helper;
        mRecipeItem = recipeItem;
        isExpanded = false;
        mStepList = null;
        mIngredientList = null;

        if (recipeItem != null) {
            mStepList = mRecipeItem.getSteps();
            mIngredientList = mRecipeItem.getIngredients();
        }
    }


    @Override
    public FCViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.fragment_detail_item, parent, false);

        if (viewType == EXPANDED_TYPE) {
            ImageView imageLeft = itemView.findViewById(R.id.circle_expand_left);
            ImageView imageRight = itemView.findViewById(R.id.circle_expand_right);

            imageLeft.setVisibility(View.VISIBLE);
            imageRight.setVisibility(View.VISIBLE);

            if (isExpanded) {
                imageLeft.setScaleY(-1);  // flip horizontal
                imageRight.setScaleY(-1);  // flip horizontal
                itemView.findViewById(R.id.fc_recycler_detail_child).setVisibility(View.VISIBLE);
            } else {
                imageLeft.setScaleY(1);  // flip horizontal
                imageRight.setScaleY(1);
                itemView.findViewById(R.id.fc_recycler_detail_child).setVisibility(View.GONE);
            }
        }

        return new FCViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(final FCViewHolder holder, final int position) {
        holder.fill(position, holder.getItemViewType());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0) {
                    isExpanded = !isExpanded;
                    notifyItemChanged(position);
                } else {
                    mHelper.onCallback(position);

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mStepList == null) return 0;
        return mStepList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return EXPANDED_TYPE;
        return COLLAPSED_TYPE;
    }

    class FCViewHolder extends RecyclerView.ViewHolder {
        private final TextView mHeaderText;
        private final TextView mDetailText;
        private final TextView mChildDetailText;

        public FCViewHolder(View itemView, int viewType) {
            super(itemView);
            mHeaderText = itemView.findViewById(R.id.fc_recycler_head_text);
            mDetailText = itemView.findViewById(R.id.fc_recycler_detail_text);
            mChildDetailText = itemView.findViewById(R.id.fc_recycler_child_detail_text);
        }

        private void fill(int position, int viewType) {


            if (position == 0) {
                mHeaderText.setText(mRecipeItem.getName());
                mHeaderText.setTextSize(24);
                itemView.setBackgroundColor(ContextCompat.getColor(mContext,R.color.colorBackHead));
//                mHeaderText.setTextColor(ContextCompat.getColor(mContext,R.color.colorBackHeadText));
                if (isExpanded && mIngredientList != null && mIngredientList.size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    int count = 1;
                    for (RecipeItem.Ingredient ingredient : mIngredientList) {
                        sb.append(count + ". " + ingredient + "\n");
                        count++;
                    }
                    mDetailText.setText(mContext.getString(R.string.click_collapse));
                    mChildDetailText.setText(sb.toString());
                } else {
                    mDetailText.setText(mContext.getString(R.string.click_expand));
                }
            } else {
                if (mStepList == null || mStepList.isEmpty() || position < 0 || position > mStepList.size()) {
                    return;
                }
                mHeaderText.setText("Step " + (position));
                mDetailText.setText(mStepList.get(position - 1).getShortDescription());
            }

        }

    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
