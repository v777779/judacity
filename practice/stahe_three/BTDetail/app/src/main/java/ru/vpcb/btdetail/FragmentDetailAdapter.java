package ru.vpcb.btdetail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class FragmentDetailAdapter extends RecyclerView.Adapter<FragmentDetailAdapter.FCViewHolder> {

    private static final int COLLAPSED_TYPE = 0;
    private static final int EXPANDED_TYPE = 1;
    private List<String> mList;
    private Context mContext;
    private LayoutInflater mInflater;
    private IFragmentHelper mHelper;
    private boolean isExpanded;


    public FragmentDetailAdapter(Context context, IFragmentHelper helper) {
        mContext = context;
        mHelper = helper;
        mList = mHelper.getList();
        mInflater = LayoutInflater.from(context);
        isExpanded = false;
    }


    @Override
    public FCViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        int itemLayoutID;
        if (isExpanded && viewType == EXPANDED_TYPE) {
            itemLayoutID = R.layout.fragment_detail_item_exp;
        } else {
            itemLayoutID = R.layout.fragment_detail_item;
        }
//        itemLayoutID = R.layout.fragment_detail_item;
        View itemView = mInflater.inflate(itemLayoutID, parent, false);

        return new FCViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(FCViewHolder holder, final int position) {


        holder.fill(position, holder.getItemViewType());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0) {
                    isExpanded = !isExpanded;
                    notifyDataSetChanged();
                } else {

                    mHelper.onCallback(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mList == null) return 0;
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isExpanded && position == 0) {
            return EXPANDED_TYPE;
        } else {
            return COLLAPSED_TYPE;
        }
    }

    class FCViewHolder extends RecyclerView.ViewHolder {
        private final TextView mText;
        private final TextView mText2;

        public FCViewHolder(View itemView, int viewType) {
            super(itemView);
            if (isExpanded && viewType == EXPANDED_TYPE) {
                mText = itemView.findViewById(R.id.fc_recycler_wide_text);
                mText2 = null;
            } else {
                mText = itemView.findViewById(R.id.fc_recycler_text);
                mText2 = itemView.findViewById(R.id.fc_recycler_text2);
            }
        }

        private void fill(int position, int viewType) {
            String s;
            if (mList == null || mList.isEmpty() || position < 0 || position > mList.size() - 1) {
                s = "Empty Card";
            } else {
                s = mList.get(position);
            }
            if (isExpanded && viewType == EXPANDED_TYPE) {
                mText.setText(s);
            } else {
                mText.setText(s);
                if (position == 0) {
                    mText2.setText("Click to Expand");
                }
            }
        }

    }


}
