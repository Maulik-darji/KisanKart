package com.android.agrifarm;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class HomeCustomerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_home_customer);

        // Handle "Switch to Sell" button
        MaterialButton switchButton = findViewById(R.id.switch_farmer);
        switchButton.setOnClickListener(v -> {
            finish(); // Close this activity and return to HomeFragment
        });
    }
}