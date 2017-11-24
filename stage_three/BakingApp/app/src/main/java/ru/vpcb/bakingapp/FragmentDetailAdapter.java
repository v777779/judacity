package ru.vpcb.bakingapp;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;


import static ru.vpcb.bakingapp.utils.Constants.COLLAPSED_TYPE;
import static ru.vpcb.bakingapp.utils.Constants.EXPANDED_TYPE;

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
        } else {
            mStepList = new ArrayList<>();
            mIngredientList = new ArrayList<>();
        }
    }


    @Override
    public FCViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.fragment_detail_item, parent, false);

        if (viewType == EXPANDED_TYPE) {
            ImageView imageLeft = itemView.findViewById(R.id.expand_left);
            ImageView imageRight = itemView.findViewById(R.id.expand_right);
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
        private final ImageView mThumbImage;
        private final ImageView mLeftExpand;
        private final ImageView mRightExpand;


        public FCViewHolder(View itemView, int viewType) {
            super(itemView);
            mHeaderText = itemView.findViewById(R.id.fc_recycler_head_text);
            mDetailText = itemView.findViewById(R.id.fc_recycler_detail_text);
            mChildDetailText = itemView.findViewById(R.id.fc_recycler_child_detail_text);
            mThumbImage = itemView.findViewById(R.id.step_thumb);
            mLeftExpand = itemView.findViewById(R.id.expand_left);
            mRightExpand = itemView.findViewById(R.id.expand_right);
        }

        private void fill(int position, int viewType) {
            if (viewType == EXPANDED_TYPE) {

                setHeaderText();
                setChildText();

            } else {
                if (mStepList == null || mStepList.isEmpty() || position < 0 ||
                        position > mStepList.size() || mStepList.get(position - 1) == null) {
                    setEmptyStep();
                    return;
                }
// stepItem
                RecipeItem.Step stepItem = mStepList.get(position - 1);  // if stepItem == null exits
                if (position == 1) {
                    mHeaderText.setText(mContext.getString(R.string.play_header_intro));
                } else {
                    mHeaderText.setText(mContext.getString(R.string.play_header_step, "" + stepItem.getId()));
                }

                mDetailText.setText(stepItem.getShortDescription());

                // thumb
                mThumbImage.setVisibility(View.VISIBLE);
                mLeftExpand.setVisibility(View.GONE);
                mRightExpand.setVisibility(View.GONE);
                String imageURL = stepItem.getThumbnailURL();

                int thumbImageId = R.drawable.ic_play_circle_white_24dp;
                if (!stepItem.getVideoURL().isEmpty())
                    thumbImageId = R.drawable.ic_play_circle_black_24dp;

                if (imageURL.isEmpty()) {                            // default image
                    mThumbImage.setImageResource(thumbImageId);
                } else {
                    Glide.with(mContext)
                            .load(imageURL)
                            .apply(new RequestOptions().error(thumbImageId))
                            .into(mThumbImage);
                }
            }


        }

        private void setChildText() {
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
        }

        private void setHeaderText() {
            mThumbImage.setVisibility(View.GONE);
            mLeftExpand.setVisibility(View.VISIBLE);
            mRightExpand.setVisibility(View.VISIBLE);

            if (mRecipeItem == null) {
                mHeaderText.setText(mContext.getString(R.string.play_header_error));
            } else {
                mHeaderText.setText(mRecipeItem.getName());
            }
            mHeaderText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    mContext.getResources().getDimension(R.dimen.large_text_size));
            itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorBackHead));
        }

        private void setEmptyStep() {
            mThumbImage.setVisibility(View.GONE);
            mLeftExpand.setVisibility(View.GONE);
            mRightExpand.setVisibility(View.GONE);
            mHeaderText.setText(mContext.getString(R.string.play_header_empty));
            mDetailText.setText(mContext.getString(R.string.play_body_error));
        }
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

}
