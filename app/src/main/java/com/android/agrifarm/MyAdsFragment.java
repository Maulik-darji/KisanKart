
package com.android.agrifarm;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.android.agrifarm.HomeCustomerActivity;
import com.android.agrifarm.R;
import com.google.android.material.button.MaterialButton;

public class MyAdsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_ads, container, false);

        // Find the "Switch to Buy" button
        MaterialButton switchButton = view.findViewById(R.id.switch_farmer);

        // Set click listener
        switchButton.setOnClickListener(v -> {
            // Open the new activity
            Intent intent = new Intent(getActivity(), HomeCustomerActivity.class);
            startActivity(intent);
        });

        return view;
    }
}