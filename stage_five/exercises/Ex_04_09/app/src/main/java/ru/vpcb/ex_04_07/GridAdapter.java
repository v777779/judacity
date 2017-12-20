package ru.vpcb.ex_04_07;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class GridAdapter extends BaseAdapter {
    AppCompatActivity mActivity;

    public GridAdapter(AppCompatActivity activity) {
        mActivity = activity;
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int [] IMAGE_IDS = new int[] {R.drawable.image_001, R.drawable.image_002, R.drawable.image_003};

        if (convertView == null) {
            convertView = mActivity.getLayoutInflater().inflate(R.layout.grid_item, parent, false);

            ImageView imageView =  convertView.findViewById(R.id.image_view);
            imageView.setImageResource(IMAGE_IDS[position%IMAGE_IDS.length]);
        }

        return convertView;
    }
}
