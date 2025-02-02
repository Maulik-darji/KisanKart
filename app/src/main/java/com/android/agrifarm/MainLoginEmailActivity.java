package com.android.agrifarm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.android.agrifarm.databinding.ActivityMainLoginEmailBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainLoginEmailActivity extends AppCompatActivity {

    private static final String TAG = "LOGIN_TAG";

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private ActivityMainLoginEmailBinding binding; // View Binding class

    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize View Binding
        binding = ActivityMainLoginEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        // Handle back button click
        binding.toolbarBackBtn.setOnClickListener(v -> finish());

        // Handle "No Account" link click
        binding.noAccountTv.setOnClickListener(v -> {
            startActivity(new Intent(MainLoginEmailActivity.this, RegisterEmailActivity.class));
            finish();
        });

        // Handle forgot password link click
        binding.forgotpasswordTv.setOnClickListener(v -> {
            startActivity(new Intent(MainLoginEmailActivity.this, ForgotPasswordActivity.class));
        });

        // Handle login button click
        binding.loginBtn.setOnClickListener(v -> {
            validateData();  // Validate email and password before login
        });
    }

    private void validateData() {
        email = binding.emailEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString();

        Log.d(TAG, "validateData: email: " + email);
        Log.d(TAG, "validateData: password: " + password);

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEt.setError("Invalid Email");
            binding.emailEt.requestFocus();
        } else if (password.isEmpty()) {
            binding.passwordEt.setError("Enter Password");
            binding.passwordEt.requestFocus();
        } else {
            loginUser();  // Proceed with login if validation is successful
        }
    }

    private void loginUser() {
        progressDialog.setMessage("Logging In");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Log.d(TAG, "onSuccess: Logged In...");
                    progressDialog.dismiss();
                    // Navigate to the Main Activity after successful login
                    startActivity(new Intent(MainLoginEmailActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: ", e);
                    progressDialog.dismiss();
                    Utils.toast(MainLoginEmailActivity.this, "Failed due to " + e.getMessage());
                });
    }
}
