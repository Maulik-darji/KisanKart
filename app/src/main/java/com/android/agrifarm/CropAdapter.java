package com.android.agrifarm;
import com.android.agrifarm.Crop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class CropAdapter extends ArrayAdapter<Crop> {
    public CropAdapter(Context context, List<Crop> crops) {
        super(context, 0, crops);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.grid_item_product, parent, false);
            holder = new ViewHolder();
            holder.image = convertView.findViewById(R.id.product_image);
            holder.name = convertView.findViewById(R.id.product_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Crop crop = getItem(position);
        if (crop != null) {
            holder.image.setImageResource(crop.getIconResId());
            holder.name.setText(crop.getName());
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView image;
        TextView name;
    }
}