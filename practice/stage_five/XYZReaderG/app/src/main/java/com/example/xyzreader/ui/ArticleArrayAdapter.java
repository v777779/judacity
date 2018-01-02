package com.example.xyzreader.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.xyzreader.R;

import java.util.List;

/**
 * Created by V1 on 19-Sep-17.
 */

public class ArticleArrayAdapter extends ArrayAdapter<String> {
    public ArticleArrayAdapter(Context context, List<String> resource) {  // если вызывается у суперкласса то Exception
        super(context, 0, resource);                        // 0 не используется
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String itemString = getItem(position);  // получить объект
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_text_item, parent, false);
        TextView name = rootView.findViewById(R.id.article_body_ext); // поле в item_flavor
        name.setText(itemString);

        return rootView;
    }
}
