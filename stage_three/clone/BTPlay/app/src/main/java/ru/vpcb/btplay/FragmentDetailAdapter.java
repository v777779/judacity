package ru.vpcb.btplay;

import android.content.Context;
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
    private Context mContext;
    private LayoutInflater mInflater;
    private IFragmentHelper mHelper;
    private boolean isExpanded;
    private RecyclerView mParent;

    public FragmentDetailAdapter(Context context, IFragmentHelper helper) {
        mContext = context;
        mHelper = helper;
        mInflater = LayoutInflater.from(context);
        isExpanded = false;
        mItemList = mHelper.getItemList();
        mParent = helper.getRecycler();
    }


    @Override
    public FCViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView;
        if (viewType == EXPANDED_TYPE) {
            itemView = mInflater.inflate(R.layout.fragment_detail_item, parent, false);
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
        } else {
            itemView = mInflater.inflate(R.layout.fragment_detail_item, parent, false);
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
        if (mItemList == null) return 0;
        return mItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mItemList == null || mItemList.isEmpty()) return COLLAPSED_TYPE;
        return mItemList.get(position).getType();
    }

    class FCViewHolder extends RecyclerView.ViewHolder {
        private final TextView mText;
        private final TextView mText2;

        public FCViewHolder(View itemView, int viewType) {
            super(itemView);
            mText = itemView.findViewById(R.id.fc_recycler_text);
            mText2 = itemView.findViewById(R.id.fc_recycler_text2);

        }

        private void fill(int position, int viewType) {
            String s;
            if (mItemList == null || mItemList.isEmpty() || position < 0 || position > mItemList.size() - 1) {
                return;
            }
            s = mItemList.get(position).getName();
            mText.setText(s);
        }

    }


}
