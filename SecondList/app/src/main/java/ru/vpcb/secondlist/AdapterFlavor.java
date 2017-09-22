package ru.vpcb.secondlist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by V1 on 19-Sep-17.
 */

public class AdapterFlavor extends ArrayAdapter<Flavor> {
    public AdapterFlavor(Context context, List<Flavor> resource) {  // если вызывается у суперкласса то Exception

        super(context, 0, resource);  // 0 не используется
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Flavor item = getItem(position);  // получить объект
        // собственно пункт на заполнение
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_flavor,parent,false);

        ImageView icon = rootView.findViewById(R.id.list_item_icon); // поле в item_flavor
        icon.setImageResource(item.getMImageId()); // картинка из объекта

        TextView name = rootView.findViewById(R.id.list_item_name); // поле в item_flavor
        name.setText(item.getMName());

        TextView version = rootView.findViewById(R.id.list_item_version); // поле в item_flavor
        version.setText(item.getMVersion());

        return rootView;
    }
}
