package ru.vpcb.bottomactivity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;


/**
 * Created by V1 on 13-Dec-17.
 */

public class JokeAdapter extends RecyclerView.Adapter<JokeAdapter.JKViewHolder> {
    Context mContext;
    private List<Integer> mList;
    private int mHeight;
    private SpanData mSp;

    public JokeAdapter(Context context, List<Integer> list, SpanData sp) {
        mContext = context;
        mList = list;
        mSp = sp;

    }

    @Override
    public JKViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.joke_recycler_item, parent, false);

        if (mContext.getResources().getBoolean(R.bool.is_vert)) {
            itemView.getLayoutParams().height = mSp.height;
        } else {
            itemView.getLayoutParams().width = mSp.width;
        }

        return new JKViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(JKViewHolder holder, final int position) {


        if (mList == null || position < 0 || position > mList.size() - 1) return;
        holder.fill(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ICallback)mContext).onComplete(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mList == null) return 0;
        return mList.size();
    }

    public class JKViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImage;

        public JKViewHolder(View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.joke_item_image);
//            mImage.getLayoutParams().height = mHeight;

        }

        private void fill(int position) {
            mImage.setImageResource(mList.get(position));
        }

    }


}
