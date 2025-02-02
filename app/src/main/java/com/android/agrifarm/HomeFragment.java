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

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private GridView gridView;
    private List<Crop> items = new ArrayList<>();
    private CropAdapter cropAdapter;
    private EditText searchBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Handle "Switch to Buy" button
        MaterialButton switchButton = rootView.findViewById(R.id.switch_customer);
        switchButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), HomeCustomerActivity.class);
            startActivity(intent);
        });

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
        // Populate with default crops
        items.clear();
        items.add(new Crop("Wheat", R.drawable.wheat_icon));
        items.add(new Crop("Rice", R.drawable.rice_icon));
        items.add(new Crop("Millet", R.drawable.millet_icon));
        items.add(new Crop("Oats", R.drawable.oats_icon));
        items.add(new Crop("Sorghum", R.drawable.sorghum_icon));
        items.add(new Crop("Amarnath", R.drawable.amaranth_icon));
        items.add(new Crop("Lentils", R.drawable.lentis_icon));
        items.add(new Crop("Green Gram", R.drawable.green_gram));
        items.add(new Crop("Horse Gram", R.drawable.horse_gram_icon));
        items.add(new Crop("Kidney Beans", R.drawable.kidney_beans_icon));
        items.add(new Crop("Lobia", R.drawable.lobia_icon));
        items.add(new Crop("Red Lentils", R.drawable.red_lentil_icon));
        items.add(new Crop("Pigeon Pea", R.drawable.toor_icon));
        items.add(new Crop("Apple", R.drawable.apple_icon));
        items.add(new Crop("Banana", R.drawable.banana_icon));
        items.add(new Crop("Litchi", R.drawable.litchi_icon));
        items.add(new Crop("Papaya", R.drawable.papaya_icon));
        items.add(new Crop("Pinaple", R.drawable.pinaapple_icon));
        items.add(new Crop("Strawberry", R.drawable.strawberry_icon));
        items.add(new Crop("Guava", R.drawable.guava_icon));
        items.add(new Crop("Jackfruit", R.drawable.jackfruit_icon));
        items.add(new Crop("Mango", R.drawable.mango_icon));
        items.add(new Crop("Musk Melon", R.drawable.musk_melon_icon));
        items.add(new Crop("Pomegranate", R.drawable.pomegranate_icon));
        items.add(new Crop("Grape", R.drawable.grape_icon));
        items.add(new Crop("Corn", R.drawable.corn_icon));
        items.add(new Crop("Aubergine", R.drawable.brinjal_icon));
        items.add(new Crop("Cabbage", R.drawable.cabbage_icon));
        items.add(new Crop("Carrot", R.drawable.carrot_icon));
        items.add(new Crop("Cauliflower", R.drawable.cauliflowe_icon));
        items.add(new Crop("Coriander", R.drawable.coriander_icon));
        items.add(new Crop("Cucumber", R.drawable.cucumber_icon));
        items.add(new Crop("Onion", R.drawable.onion_icon));
        items.add(new Crop("Spinach", R.drawable.spinach_icon));
        items.add(new Crop("Tomato", R.drawable.tomato_icon));

        // Set adapter
        cropAdapter = new CropAdapter(requireContext(), items);
        gridView.setAdapter(cropAdapter);
    }

    private void showCropsForCategory(String category) {
        List<Crop> filteredCrops = new ArrayList<>();

        // Filter crops based on category
        if (category.equals("Cereals & Grains")) {
            filteredCrops.add(new Crop("Wheat", R.drawable.wheat_icon));
            filteredCrops.add(new Crop("Rice", R.drawable.rice_icon));
            filteredCrops.add(new Crop("Millet", R.drawable.millet_icon));
            filteredCrops.add(new Crop("Oats", R.drawable.oats_icon));
            filteredCrops.add(new Crop("Sorghum", R.drawable.sorghum_icon));
            filteredCrops.add(new Crop("Amarnath", R.drawable.amaranth_icon));
        } else if (category.equals("Pulses & Legumes")) {
            filteredCrops.add(new Crop("Lentils", R.drawable.lentis_icon));
            filteredCrops.add(new Crop("Green Gram", R.drawable.green_gram));
            filteredCrops.add(new Crop("Horse Gram", R.drawable.horse_gram_icon));
            filteredCrops.add(new Crop("Kidney Beans", R.drawable.kidney_beans_icon));
            filteredCrops.add(new Crop("Lobia", R.drawable.lobia_icon));
            filteredCrops.add(new Crop("Red Lentils", R.drawable.red_lentil_icon));
            filteredCrops.add(new Crop("Pigeon Pea", R.drawable.toor_icon));
        } else if (category.equals("Fruits")) {
            filteredCrops.add(new Crop("Apple", R.drawable.apple_icon));
            filteredCrops.add(new Crop("Banana", R.drawable.banana_icon));
            filteredCrops.add(new Crop("Litchi", R.drawable.litchi_icon));
            filteredCrops.add(new Crop("Papaya", R.drawable.papaya_icon));
            filteredCrops.add(new Crop("Pinaple", R.drawable.pinaapple_icon));
            filteredCrops.add(new Crop("Strawberry", R.drawable.strawberry_icon));
            filteredCrops.add(new Crop("Guava", R.drawable.guava_icon));
            filteredCrops.add(new Crop("Jackfruit", R.drawable.jackfruit_icon));
            filteredCrops.add(new Crop("Mango", R.drawable.mango_icon));
            filteredCrops.add(new Crop("Musk Melon", R.drawable.musk_melon_icon));
            filteredCrops.add(new Crop("Pomegranate", R.drawable.pomegranate_icon));
            filteredCrops.add(new Crop("Grape", R.drawable.grape_icon));
        } else if (category.equals("Vegetables")) {
            filteredCrops.add(new Crop("Tomato", R.drawable.tomato_icon));
            filteredCrops.add(new Crop("Corn", R.drawable.corn_icon));
            filteredCrops.add(new Crop("Aubergine", R.drawable.brinjal_icon));
            filteredCrops.add(new Crop("Cabbage", R.drawable.cabbage_icon));
            filteredCrops.add(new Crop("Carrot", R.drawable.carrot_icon));
            filteredCrops.add(new Crop("Cauliflower", R.drawable.cauliflowe_icon));
            filteredCrops.add(new Crop("Coriander", R.drawable.coriander_icon));
            filteredCrops.add(new Crop("Cucumber", R.drawable.cucumber_icon));
            filteredCrops.add(new Crop("Onion", R.drawable.onion_icon));
            filteredCrops.add(new Crop("Spinach", R.drawable.spinach_icon));
        }

        // Set filtered crops to adapter
        cropAdapter = new CropAdapter(requireContext(), filteredCrops);
        gridView.setAdapter(cropAdapter);
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
                filterItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }

    private void filterItems(String query) {
        List<Crop> filteredCrops = new ArrayList<>();
        for (Crop crop : items) {
            if (crop.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredCrops.add(crop);
            }
        }
        cropAdapter = new CropAdapter(requireContext(), filteredCrops);
        gridView.setAdapter(cropAdapter);
    }
}