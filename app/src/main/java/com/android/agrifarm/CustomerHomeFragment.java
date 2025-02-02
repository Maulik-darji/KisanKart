package com.android.agrifarm;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class CustomerHomeFragment extends Fragment {
    private GridView gridView;
    private List<com.android.agrifarm.Crop> items = new ArrayList<>();
    private CropAdapter cropAdapter;
    private EditText searchBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize GridView
        gridView = rootView.findViewById(R.id.product_grid);

        // Initialize search bar
        searchBar = rootView.findViewById(R.id.search_bar);

        // Initialize with crops
        initializeCrops();

        // Setup category buttons
        setupCategoryButtons(rootView);

        // Setup search functionality
        setupSearchBar();

        return rootView;
    }

    private void initializeCrops() {
        // Initially populate with some default crops for display
        items.clear();
        items.add(new com.android.agrifarm.Crop("Wheat", R.drawable.wheat_icon));
        items.add(new com.android.agrifarm.Crop("Rice", R.drawable.rice_icon));
        items.add(new com.android.agrifarm.Crop("Millet", R.drawable.millet_icon));
        items.add(new com.android.agrifarm.Crop("Oats", R.drawable.oats_icon));
        items.add(new com.android.agrifarm.Crop("Sorghum",R.drawable.sorghum_icon));

        items.add(new com.android.agrifarm.Crop("Amarnath", R.drawable.amaranth_icon));
        items.add(new com.android.agrifarm.Crop("Lentils", R.drawable.lentis_icon));
        items.add(new com.android.agrifarm.Crop("Green Gram", R.drawable.green_gram));
        items.add(new com.android.agrifarm.Crop("Horse Gram", R.drawable.horse_gram_icon));
        items.add(new com.android.agrifarm.Crop("Kidney Beans",R.drawable.kidney_beans_icon));

        items.add(new com.android.agrifarm.Crop("Lobia", R.drawable.lobia_icon));
        items.add(new com.android.agrifarm.Crop("Red Lentils", R.drawable.red_lentil_icon));
        items.add(new com.android.agrifarm.Crop("Pigeon Pea", R.drawable.toor_icon));
        items.add(new com.android.agrifarm.Crop("Apple", R.drawable.apple_icon));
        items.add(new com.android.agrifarm.Crop("Banana",R.drawable.banana_icon));

        items.add(new com.android.agrifarm.Crop("Litchi", R.drawable.litchi_icon));
        items.add(new com.android.agrifarm.Crop("Papaya", R.drawable.papaya_icon));
        items.add(new com.android.agrifarm.Crop("Pinaple", R.drawable.pinaapple_icon));
        items.add(new com.android.agrifarm.Crop("Strawberry", R.drawable.strawberry_icon));
        items.add(new com.android.agrifarm.Crop("Guava",R.drawable.guava_icon));

        items.add(new com.android.agrifarm.Crop("Jackfruit", R.drawable.jackfruit_icon));
        items.add(new com.android.agrifarm.Crop("Mango", R.drawable.mango_icon));
        items.add(new com.android.agrifarm.Crop("Musk Melon", R.drawable.musk_melon_icon));
        items.add(new com.android.agrifarm.Crop("Pomegranate", R.drawable.pomegranate_icon));
        items.add(new com.android.agrifarm.Crop("Grape",R.drawable.grape_icon));

        items.add(new com.android.agrifarm.Crop("Pigeon Pea", R.drawable.toor_icon));
        items.add(new com.android.agrifarm.Crop("Corn", R.drawable.corn_icon));
        items.add(new com.android.agrifarm.Crop("Aubergine", R.drawable.brinjal_icon));
        items.add(new com.android.agrifarm.Crop("Cabbage", R.drawable.cabbage_icon));
        items.add(new com.android.agrifarm.Crop("Carrot",R.drawable.carrot_icon));

        items.add(new com.android.agrifarm.Crop("Cauliflower", R.drawable.cauliflowe_icon));
        items.add(new com.android.agrifarm.Crop("Coriander", R.drawable.coriander_icon));
        items.add(new com.android.agrifarm.Crop("Cucumber", R.drawable.cucumber_icon));
        items.add(new com.android.agrifarm.Crop("Onion", R.drawable.onion_icon));
        items.add(new com.android.agrifarm.Crop("Spinach",R.drawable.spinach_icon));

        items.add(new com.android.agrifarm.Crop("Wheat", R.drawable.wheat_icon));
        items.add(new com.android.agrifarm.Crop("Rice", R.drawable.rice_icon));
        items.add(new com.android.agrifarm.Crop("Tomato", R.drawable.tomato_icon));





        // Set adapter
        cropAdapter = new CropAdapter(requireContext(), items);
        gridView.setAdapter(cropAdapter);
    }

    private void showCropsForCategory(String category) {
        List<com.android.agrifarm.Crop> filteredCrops = new ArrayList<>();

        // Filter crops based on category
        if (category.equals("Cereals & Grains")) {
            filteredCrops.add(new com.android.agrifarm.Crop("Wheat", R.drawable.wheat_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Rice", R.drawable.rice_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Millet", R.drawable.millet_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Oats", R.drawable.oats_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Sorghum", R.drawable.sorghum_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Amarnath", R.drawable.amaranth_icon));

        } else if (category.equals("Pulses & Legumes")) {
            // Example: add pulses crops
            filteredCrops.add(new com.android.agrifarm.Crop("Lentils", R.drawable.lentis_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Chickpeas", R.drawable.chickpeas_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Bengal Gram", R.drawable.bengal_gram_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Black Gram", R.drawable.black_gram_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Green Gram", R.drawable.green_gram));
            filteredCrops.add(new com.android.agrifarm.Crop("Horse Gram", R.drawable.horse_gram_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Kidney Beans", R.drawable.kidney_beans_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Lobia", R.drawable.lobia_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Red Lentils", R.drawable.red_lentil_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Pigeon Pea", R.drawable.toor_icon));



        } else if (category.equals("Fruits")) {
            // Example: add fruits crops
            filteredCrops.add(new com.android.agrifarm.Crop("Apple", R.drawable.apple_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Banana", R.drawable.banana_icon));


            filteredCrops.add(new com.android.agrifarm.Crop("Litchi", R.drawable.litchi_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Papaya", R.drawable.papaya_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Pinapple", R.drawable.pinaapple_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Strawberry", R.drawable.strawberry_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Guava", R.drawable.guava_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Jackfruti", R.drawable.jackfruit_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Mango", R.drawable.mango_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Musk Melon", R.drawable.musk_melon_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("pomegranate", R.drawable.pomegranate_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Grapes", R.drawable.grape_icon));



        } else if (category.equals("Vegetables")) {
            filteredCrops.add(new com.android.agrifarm.Crop("Tomato", R.drawable.tomato_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Corn", R.drawable.corn_icon));


            filteredCrops.add(new com.android.agrifarm.Crop("Aubergine", R.drawable.brinjal_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Cabbage", R.drawable.cabbage_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Carrot", R.drawable.carrot_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Cauliflower", R.drawable.cauliflowe_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Coriander", R.drawable.coriander_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Cucumber", R.drawable.cucumber_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Onion", R.drawable.onion_icon));
            filteredCrops.add(new com.android.agrifarm.Crop("Spinach", R.drawable.spinach_icon));
        }

        // Set filtered crops to adapter
        cropAdapter = new CropAdapter(requireContext(), filteredCrops);
        gridView.setAdapter(cropAdapter);
        gridView.setOnItemClickListener(null); // Clear farmer click listeners
    }

    private void setupCategoryButtons(View rootView) {
        Button btnCereals = rootView.findViewById(R.id.tab_cereals);
        btnCereals.setOnClickListener(v -> showCropsForCategory("Cereals & Grains"));

        Button btnPulses = rootView.findViewById(R.id.tab_pulses);
        btnPulses.setOnClickListener(v -> showCropsForCategory("Pulses & Legumes"));

        Button btnFruits = rootView.findViewById(R.id.tab_fruits);
        btnFruits.setOnClickListener(v -> showCropsForCategory("Fruits"));

        Button btnVegetables = rootView.findViewById(R.id.tab_vegetables);
        btnVegetables.setOnClickListener(v -> showCropsForCategory("Vegetables"));
    }

    private void setupSearchBar() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter the list based on the search query
                filterItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }

    private void filterItems(String query) {
        if (gridView.getAdapter() == cropAdapter) {
            // Filter crops
            List<com.android.agrifarm.Crop> filteredCrops = new ArrayList<>();
            for (com.android.agrifarm.Crop crop : items) {
                if (crop.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredCrops.add(crop);
                }
            }
            cropAdapter = new CropAdapter(requireContext(), filteredCrops);
            gridView.setAdapter(cropAdapter);
        }
    }
}
