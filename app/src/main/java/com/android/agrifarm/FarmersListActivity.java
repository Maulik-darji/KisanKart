
package com.android.agrifarm;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.agrifarm.R;

public class FarmersListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmers_list);

        // Hardcoded farmers
        String[] farmers = {"Ramesh Patel", "Suresh Kumar", "Anil Sharma"};

        ListView listView = findViewById(R.id.farmers_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                farmers
        );
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, FarmerDetailActivity.class);
            intent.putExtra("FARMER_NAME", farmers[position]);
            startActivity(intent);
        });
    }
}