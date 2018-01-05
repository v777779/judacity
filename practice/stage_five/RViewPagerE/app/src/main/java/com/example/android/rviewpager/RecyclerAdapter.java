package com.example.android.rviewpager;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<String> mList;
    private Context mContext;
    private int mHeight;

    public RecyclerAdapter(Context context, int height) {
        mContext = context;
        mHeight = height;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = ((AppCompatActivity) mContext).getLayoutInflater()
                .inflate(R.layout.content_main_item, parent, false);
        view.getLayoutParams().height = mHeight;
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder,  int position) {
        holder.fill(position);
        final int m = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ICallback) mContext).onCallback(holder.mItemImage, m);
            }
        });
    }

//    @Override
//    public long getItemId(int position) {
//        return position;
//    }

    @Override
    public int getItemCount() {
        if (mList == null) return 0;
        return mList.size();
    }

    public void setCursor(List<String> list) {
        if (list == null) return;
        mList = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Nullable
        @BindView(R.id.item_image)
        ImageView mItemImage;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void fill(int position) {
            String imageName = mList.get(position);
            String imageURL = "file:///android_asset/images/" + mList.get(position);  // string URL
            Glide.with(mContext).load(imageURL).into(mItemImage);
//            Picasso.with(mContext).load(imageURL).into(mItemImage);
            mItemImage.setTransitionName(imageName);

        }

    }
}