package com.example.xyzreader.ui;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;

import java.util.List;

public class RecyclerBodyAdapter extends RecyclerView.Adapter<RecyclerBodyAdapter.ViewHolder> {

    private List<String> mList;
    private Context mContext;
    private Typeface mCaecilia;


    public RecyclerBodyAdapter(Context context, Typeface caecilia) {
        mContext = context;
        mCaecilia = caecilia;
    }

    // TODO remove later
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = ((AppCompatActivity) mContext).getLayoutInflater()
                .inflate(R.layout.fragment_text_item, parent, false);


        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.fill(position);

    }

    @Override
    public int getItemCount() {
        if (mList == null) return 0;
        return mList.size();
    }

    public void swap(List<String> list) {
        if (list == null) return;
        mList = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mItemText;

        public ViewHolder(View view) {
            super(view);
            mItemText = view.findViewById(R.id.article_body_ext);
            mItemText.setTypeface(mCaecilia);
        }

        private void fill(int position) {
            if (mList == null || position < 0 || position >= mList.size()) return;

            String s = mList.get(position);
            if (s != null && !s.isEmpty()) {
                mItemText.setText(s);
            }
        }
    }


}