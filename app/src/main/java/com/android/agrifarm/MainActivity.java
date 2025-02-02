package com.android.agrifarm;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.FragmentTransaction;

import com.android.agrifarm.AccountFragment;
import com.android.agrifarm.AdCreateActivity;
import com.android.agrifarm.ChatsFragment;
import com.android.agrifarm.HomeFragment;
import com.android.agrifarm.CustomerHomeFragment;
import com.android.agrifarm.LoginOptionsActivity;
import com.android.agrifarm.MyAdsFragment;
import com.android.agrifarm.R;
import com.android.agrifarm.Utils;
import com.android.agrifarm.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth firebaseAuth;
    private TextView toolbarTitleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize toolbar title
        toolbarTitleTv = findViewById(R.id.toolbarTitleTv);

        if (firebaseAuth.getCurrentUser() == null) {
            startLoginOptions();
        }

        // Edge-to-edge layout
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Show default fragment (Home)
        showHomeFragment();
        toolbarTitleTv.setVisibility(View.GONE); // Hide for Home

        // Bottom navigation listener
        binding.bottomNv.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.id_home) {
                showHomeFragment();
                toolbarTitleTv.setVisibility(View.GONE); // Hide for Home
                return true;
            } else if (itemId == R.id.id_chat) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Utils.toast(MainActivity.this, "Login Required...");
                    startLoginOptions();
                    return false;
                } else {
                    showChatsFragment();
                    toolbarTitleTv.setVisibility(View.VISIBLE);
                    toolbarTitleTv.setText("Chats"); // Update title
                    return true;
                }
            } else if (itemId == R.id.id_myads) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Utils.toast(MainActivity.this, "Login Required...");
                    startLoginOptions();
                    return false;
                } else {
                    showMyAdsFragment();
                    toolbarTitleTv.setVisibility(View.VISIBLE);
                    toolbarTitleTv.setText("My Buying");
                    return true;
                }
            } else if (itemId == R.id.id_account) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Utils.toast(MainActivity.this, "Login Required...");
                    startLoginOptions();
                    return false;
                } else {
                    showAccountFragment();
                    toolbarTitleTv.setVisibility(View.VISIBLE);
                    toolbarTitleTv.setText("Account");
                    return true;
                }
            }
            return false;
        });

        binding.sellFab.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AdCreateActivity.class));
        });
    }

    private void showHomeFragment() {
        HomeFragment fragment = new HomeFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(binding.framgmentsFl.getId(), fragment, "HomeFragment");
        transaction.commit();
    }

    private void showChatsFragment() {
        ChatsFragment fragment = new ChatsFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(binding.framgmentsFl.getId(), fragment, "ChatFragment");
        transaction.commit();
    }

    private void showMyAdsFragment() {
        MyAdsFragment fragment = new MyAdsFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(binding.framgmentsFl.getId(), fragment, "MyAdsFragment");
        transaction.commit();
    }

    private void showAccountFragment() {
        AccountFragment fragment = new AccountFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(binding.framgmentsFl.getId(), fragment, "AccountFragment");
        transaction.commit();
    }

    private void startLoginOptions() {
        startActivity(new Intent(this, LoginOptionsActivity.class));
    }
}