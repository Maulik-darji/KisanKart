package com.android.agrifarm;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.agrifarm.R;

public class FarmerDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_detail);

        TextView farmerName = findViewById(R.id.detail_name);
        if (farmerName != null) {
            String name = getIntent().getStringExtra("FARMER_NAME");
            farmerName.setText(name);
        } else {
            Log.e("FarmerDetailActivity", "TextView with ID detail_name not found");
        }
    }
}