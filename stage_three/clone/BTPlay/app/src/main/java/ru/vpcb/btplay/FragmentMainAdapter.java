package ru.vpcb.btplay;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 15-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */

public class FragmentMainAdapter extends RecyclerView.Adapter<FragmentMainAdapter.FCViewHolder> {
    private List<String> mList;
    private Context mContext;
    private LayoutInflater mInflater;
    private IFragmentHelper mHelper;

    public FragmentMainAdapter(Context context, IFragmentHelper helper) {
        mContext = context;
        mHelper = helper;
        mList = mHelper.getList();
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public FCViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = mInflater.inflate(R.layout.fragment_main_item, parent, false);

        return new FCViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FCViewHolder holder, final int position) {
        String s;
        if (mList == null || mList.isEmpty() || position < 0 || position > mList.size() - 1) {
            s = "Empty Card";
        } else {
            s = mList.get(position);
        }
        holder.fill(s);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHelper.onCallback(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mList == null) return 0;
        return mList.size();
    }


    class FCViewHolder extends RecyclerView.ViewHolder {
        private final TextView mText;


        public FCViewHolder(View itemView) {
            super(itemView);
            mText = (TextView) itemView.findViewById(R.id.fc_recycler_text);
        }

        private void fill(String s) {
            mText.setText(s);
        }

    }


}
