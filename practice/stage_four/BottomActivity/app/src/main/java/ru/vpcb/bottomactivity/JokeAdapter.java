package ru.vpcb.bottomactivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import static ru.vpcb.bottomactivity.MainActivity.BUNDLE_JOKE_STRING;

/**
 * Created by V1 on 13-Dec-17.
 */

public class JokeAdapter extends RecyclerView.Adapter<JokeAdapter.JKViewHolder> {
    private ICallback mCallback;
    private List<Integer> mList;
    private int mHeight;


    public JokeAdapter(List<Integer> list, int height, ICallback callback) {
        mList = list;
        mHeight = height;
        mCallback = callback;

    }

    @Override
    public JKViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.joke_recycler_item, parent, false);
        itemView.getLayoutParams().height = mHeight;

        return new JKViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(JKViewHolder holder, final int position) {


        if (mList == null || position < 0 || position > mList.size() - 1) return;
        holder.fill(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onComplete(position);
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
