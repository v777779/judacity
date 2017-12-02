package ru.vpcb.bakingapp;

import android.content.Context;
import android.support.annotation.Nullable;
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


import butterknife.BindView;
import butterknife.ButterKnife;
import ru.vpcb.bakingapp.data.RecipeItem;

import static ru.vpcb.bakingapp.utils.RecipeUtils.getIngredientString;
import static ru.vpcb.bakingapp.utils.RecipeUtils.getRecipeName;
import static ru.vpcb.bakingapp.utils.RecipeUtils.getShortDescription;
import static ru.vpcb.bakingapp.utils.RecipeUtils.getStepName;
import static ru.vpcb.bakingapp.utils.Constants.COLLAPSED_TYPE;
import static ru.vpcb.bakingapp.utils.Constants.EXPANDED_TYPE;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 15-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */

/**
 * DetailActivity RecyclerVeiw Adapter Class with RecipeItem.Step items
 */
public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.FCViewHolder> {

    /**
     * The flag is true if the list of Ingredients in RecyclerView is expanded
     */
    private boolean isExpanded;
    /**
     * The RecipeItem parent object
     */
    private RecipeItem mRecipeItem;
    /**
     * Callback interface object for RecyclerView Adapter
     */
    private IFragmentHelper mHelper;
    /**
     * List of Step objects is data source for RecyclerView Adapter
     */
    private List<RecipeItem.Step> mStepList;
    /**
     * List of ingredients objects is data source for RecyclerView Adapter
     */
    private List<RecipeItem.Ingredient> mIngredientList;

    /**
     * Context of current activity
     */
    private Context mContext;

    /**
     * Preference flag, is true if load thumbnails enabled
     */
    private boolean mIsLoadImages;

    /**
     * RecyclerView Adapter for RecylerView of Step items
     *
     * @param context      context of current activity
     * @param helper       IFragmentHelper callback interface object
     * @param recipeItem   RecipeItem parent data source object
     * @param isLoadImages boolean flag is true if load of thumbnails images is enabled
     */
    public DetailAdapter(Context context, IFragmentHelper helper, RecipeItem recipeItem, boolean isLoadImages) {
        mContext = context;
        mHelper = helper;
        mRecipeItem = recipeItem;
        mIsLoadImages = isLoadImages;
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

    /**
     * Creates FCViewHolder object
     * if the viewType is EXPANDED, buttons added and List of ingredients is showed
     * when isExpanded is true, buttons rotated arrows up
     * List of ingredient is VISIBLE
     * when isExpanded is false, buttons rotated arrows down
     * List of ingredient is VISIBLE
     * if the viewType is COLLAPSED, buttons are INVISIBLE, List of ingredients is GONE
     *
     * @param parent   ViewGroup parent view object
     * @param viewType int type of item, can be EXPANDED_TYPE or COLLAPSED_TYPE
     * @return FCViewHolder object of item of RecyclerView
     */
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

    /**
     * Binds Step data to itemView object
     * Instantiates onClick() method which
     * toggles flag isExpanded for the first item and calls notifyDataChanged()
     * calls IFragmentHelper.callback for all items except first
     *
     * @param holder   FCViewHolder object of Step item
     * @param position int position in the List RecipeItem.Step  of items
     */
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

    /**
     * Returns the size of List RecipeItem.Step  data source of RecyclerView Adapter
     *
     * @return int size of step objects
     */
    @Override
    public int getItemCount() {
        if (mStepList == null) return 0;
        return mStepList.size() + 1;
    }

    /**
     * Loads List RecipeItem.Step to mStepList
     * Loads List RecipeItem.Ingredients to mIngredientList.
     * Loads RecipeItem object from input RecipeItem object.
     * Notify adapter to reload RecyclerView.
     *
     * @param recipeItem RecipeItem input RecipeItem object
     * @return RecipeItem current value of mRecipeItem
     */
    public RecipeItem swapRecipe(RecipeItem recipeItem) {
        RecipeItem oldRecipeItem = mRecipeItem;
        mRecipeItem = recipeItem;
        if (recipeItem != null) {
            mStepList = recipeItem.getSteps();
            mIngredientList = recipeItem.getIngredients();
            notifyDataSetChanged();
        }
        return oldRecipeItem;

    }


    /**
     * Returns type of ItemView object.
     * There are two possible values  EXPANDED_TYPE and COLLAPSES_TYPE.
     *
     * @param position int position of ItemView
     * @return int type of ItemView object
     */
    @Override
    public int getItemViewType(int position) {
        if (position == 0) return EXPANDED_TYPE;
        return COLLAPSED_TYPE;
    }

    /**
     * FCViewHolder class of RecyclerView.
     */
    class FCViewHolder extends RecyclerView.ViewHolder {
        /**
         * Text of collapsed item first line
         */
        @Nullable
        @BindView(R.id.fc_recycler_head_text)
        TextView mHeaderText;
        /**
         * Text of collapsed item second line
         */
        @Nullable
        @BindView(R.id.fc_recycler_detail_text)
        TextView mDetailText;

        /**
         * Text of expanded item all lines except first.
         *  First line of expanded item is always INGREDIENTS:.
         */
        @Nullable
        @BindView(R.id.fc_recycler_child_detail_text)
        TextView mChildDetailText;
        /**
         * Image of thumbnail in the left of item
         */
        @Nullable
        @BindView(R.id.step_thumb)
        ImageView mThumbImage;
        /**
         * Left expand button image
         */
        @Nullable
        @BindView(R.id.expand_left)
        ImageView mLeftExpand;
        /**
         * Right expand button image
         */
        @Nullable
        @BindView(R.id.expand_right)
        ImageView mRightExpand;

        /**
         *  Constructor FCView Holder
         *
         * @param itemView View  object of item
         * @param viewType int  type of item EXPANDED_TYPE or COLLAPSED_TYPE
         */
        public FCViewHolder(View itemView, int viewType) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        /**
         *  Extracts data from List RecipeItem.Step and List RecipeItem.Ingredients into fields of item layout.
         *  Loads thumbnails images from imageURL, if not from videoURL.
         *  If preference mIsLoadImages is false, placeholder used instead.
         *           *
         * @param position
         * @param viewType
         */
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
                mHeaderText.setText(getStepName(mContext.getResources(), stepItem));
                mDetailText.setText(getShortDescription(mContext.getResources(), stepItem));

                // thumb
                mThumbImage.setVisibility(View.VISIBLE);
                mLeftExpand.setVisibility(View.GONE);
                mRightExpand.setVisibility(View.GONE);
                String imageURL = stepItem.getThumbnailURL();
                String videoURL = stepItem.getVideoURL();
                if (imageURL == null || imageURL.isEmpty()) {
                    imageURL = videoURL;
                }
                if (videoURL == null || videoURL.isEmpty()) {                            // default image
                    mThumbImage.setImageResource(R.drawable.ic_play_circle_white_24dp);

                } else if (!mIsLoadImages) {
                    mThumbImage.setImageResource(R.drawable.ic_play_circle_black_24dp);   // no load
                } else {
                    Glide.with(mContext)
                            .load(imageURL)
                            .apply(new RequestOptions().error(R.drawable.ic_play_circle_black_24dp))
                            .into(mThumbImage);
                }
            }

        }

        /**
         *  Fills second line of EXPANDED_TYPE and COLLAPSED_TYPE items.
         *  Fills all lines except first one in expanded part of EXPANDED_TYPE item.
         *  In COLLAPSED_TYPE item this expanded part is GONE.
         */
        private void setChildText() {
            if (isExpanded && mIngredientList != null && mIngredientList.size() > 0) {
                String s = getIngredientString(mContext.getResources(), mIngredientList);  // get and clear text
                mDetailText.setText(mContext.getString(R.string.ingredients_collapse));
                mChildDetailText.setText(s);
            } else {
                mDetailText.setText(mContext.getString(R.string.ingredients_expand));
            }
        }

        /**
         *  Fills first line of EXPANDED_TYPE and COLLAPSED_TYPE items.
         */
        private void setHeaderText() {
            mThumbImage.setVisibility(View.GONE);
            mLeftExpand.setVisibility(View.VISIBLE);
            mRightExpand.setVisibility(View.VISIBLE);

            if (mRecipeItem == null) {
                mHeaderText.setText(mContext.getString(R.string.play_header_error));
            } else {
                mHeaderText.setText(getRecipeName(mContext.getResources(), mRecipeItem));
            }
            mHeaderText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    mContext.getResources().getDimension(R.dimen.large_text_size));
            itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorBackHead));
        }

        /**
         *  Fills item placeholder if data soruces are empty
         */
        private void setEmptyStep() {
            mThumbImage.setVisibility(View.GONE);
            mLeftExpand.setVisibility(View.GONE);
            mRightExpand.setVisibility(View.GONE);
            mHeaderText.setText(mContext.getString(R.string.play_header_empty));
            mDetailText.setText(mContext.getString(R.string.play_body_error));
        }

    }

    /**
     *  Return state of isExpanded flag
     * @return boolean is true if expanded
     */
    public boolean isExpanded() {
        return isExpanded;
    }

    /**
     * Set the value of isExpanded flag
     *  Used by DetailActivity during setup RecyclerAdapter
     *  when device is rotated to hold of expanded state of RecyclerView
     *
     * @param expanded
     */
    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

}
