package com.android.agrifarm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.agrifarm.databinding.ActivityDeleteAccountBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeleteAccountActivity extends AppCompatActivity {

    private ActivityDeleteAccountBinding binding;
    private static final String TAG = "DELETE_ACCOUNT_TAG";

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeleteAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize FirebaseAuth and FirebaseUser
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.toolbarBackBtn.setOnClickListener(v -> onBackPressed());

        binding.submitBtn.setOnClickListener(v -> deleteAccount());
    }

    private void deleteAccount() {
        Log.d(TAG, "deleteAccount : ");

        if (firebaseUser == null) {
            Log.e(TAG, "No user is logged in");
            return;
        }

        String myUid = firebaseAuth.getUid();

        progressDialog.setMessage("Deleting User Account");
        progressDialog.show();

        // Delete the user account
        firebaseUser.delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "onSuccess: Account deleted");

                    progressDialog.setMessage("Deleting User Ads");
                    DatabaseReference refUserAds = FirebaseDatabase.getInstance().getReference("Ads");
                    refUserAds.orderByChild("Uid").equalTo(myUid)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        ds.getRef().removeValue();
                                    }

                                    progressDialog.setMessage("Deleting User Data");
                                    DatabaseReference refUser = FirebaseDatabase.getInstance().getReference("user");
                                    refUser.child(myUid)
                                            .removeValue()
                                            .addOnSuccessListener(aVoid1 -> {
                                                Log.d(TAG, "onSuccess: User data deleted");
                                                startMainActivity();
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e(TAG, "onFailure: ", e);
                                                handleFailure(e);
                                            });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Handle failure here if necessary
                                    handleFailure(error.toException());
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: " + e);
                    handleFailure(e);
                });
    }

    private void handleFailure(Exception e) {
        progressDialog.dismiss();
        Utils.toast(this, "Failed to delete account due to " + e.getMessage());
        startMainActivity();
    }

    private void startMainActivity() {
        Log.d(TAG, "startMainActivity: ");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finishAffinity(); // To finish all previous activities in the stack
    }

    @Override
    public void onBackPressed() {
        startMainActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Dismiss the progress dialog if the activity is destroyed
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
