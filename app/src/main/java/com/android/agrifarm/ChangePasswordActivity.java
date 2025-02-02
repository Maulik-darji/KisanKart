package com.android.agrifarm;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.agrifarm.databinding.ActivityChangePasswordBinding;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private ActivityChangePasswordBinding binding;
    private static final String TAG = "CHANGE_PASS_TAG";

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;

    private String currentPassword = "";
    private String newPassword = "";
    private String confirmPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private void validateData() {
        Log.d(TAG, "validateData: ");

        currentPassword = binding.currentPasswordEt.getText().toString().trim();
        newPassword = binding.newPasswordEt.getText().toString().trim();
        confirmPassword = binding.confirmNewPasswordEt.getText().toString().trim();

        Log.d(TAG, "validateData: currentPassword: " + currentPassword);
        Log.d(TAG, "validateData: newPassword: " + newPassword);
        Log.d(TAG, "validateData: confirmPassword: " + confirmPassword);

        if (currentPassword.isEmpty()) {
            binding.currentPasswordEt.setError("Enter your current password");
            binding.currentPasswordEt.requestFocus();
        } else if (newPassword.isEmpty()) {
            binding.newPasswordEt.setError("Enter new password");
            binding.newPasswordEt.requestFocus();
        } else if (confirmPassword.isEmpty()) {
            binding.confirmNewPasswordEt.setError("Enter confirm password");
            binding.confirmNewPasswordEt.requestFocus();
        } else if (!newPassword.equals(confirmPassword)) {
            binding.confirmNewPasswordEt.setError("Passwords don't match");
            binding.confirmNewPasswordEt.requestFocus();
        } else {
            authenticateUserForUpdatePassword();
        }
    }

    private void authenticateUserForUpdatePassword() {
        Log.d(TAG, "authenticateUserForUpdatePassword: ");

        progressDialog.setMessage("Authenticating user");
        progressDialog.show();

        AuthCredential authCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), currentPassword);
        firebaseUser.reauthenticate(authCredential)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "onSuccess: User re-authenticated");
                    updatePassword();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: ", e);
                    progressDialog.dismiss();
                    Utils.toast(ChangePasswordActivity.this, "Failed to authenticate due to " + e.getMessage());
                });
    }

    private void updatePassword() {
        Log.d(TAG, "updatePassword: ");

        progressDialog.setMessage("Updating password");
        progressDialog.show();

        firebaseUser.updatePassword(newPassword)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "onSuccess: Password updated");
                    progressDialog.dismiss();
                    Utils.toast(ChangePasswordActivity.this, "Password updated!");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: ", e);
                    progressDialog.dismiss();
                    Utils.toast(ChangePasswordActivity.this, "Failed to update password due to " + e.getMessage());
                });
    }
}