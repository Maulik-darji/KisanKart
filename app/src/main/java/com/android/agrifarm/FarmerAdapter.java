package com.android.agrifarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class FarmerAdapter extends ArrayAdapter<Farmer> {
    public FarmerAdapter(Context context, List<Farmer> farmers) {
        super(context, 0, farmers);
    }

    public List<Farmer> getFarmers() {
        return getFarmers();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item_farmer, parent, false);
        }

        Farmer farmer = getItem(position);
        TextView name = convertView.findViewById(R.id.farmer_name);
        TextView price = convertView.findViewById(R.id.farmer_price);
        TextView location = convertView.findViewById(R.id.farmer_location);

        if (farmer != null) {
            name.setText(farmer.getName());
            price.setText("Price: ₹" + farmer.getPrice() + "/kg");
        }

        return convertView;
    }
}