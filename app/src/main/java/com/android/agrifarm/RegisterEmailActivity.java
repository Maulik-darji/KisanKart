package com.android.agrifarm;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import androidx.appcompat.widget.AppCompatImageButton;

public class RegisterEmailActivity extends AppCompatActivity {

    private static final String TAG = "REGISTER_TAG";

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private EditText emailEt, passwordEt, cPasswordEt;
    private ImageButton toolbarBackBtn;

    private TextView haveAccountTv;

    private TextView registerBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_email);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize Views using findViewById
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        cPasswordEt = findViewById(R.id.cPasswordEt);
        toolbarBackBtn = findViewById(R.id.toolbarBackBtn);
        haveAccountTv = findViewById(R.id.haveAccountTv);
        registerBtn = findViewById(R.id.registerBtn);

        // ProgressDialog initialization
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        // Set listeners
        toolbarBackBtn.setOnClickListener(v -> {
            startActivity(new Intent(RegisterEmailActivity.this, MainLoginEmailActivity.class));
            finish(); // Close this activity
        });

        haveAccountTv.setOnClickListener(v -> {
            startActivity(new Intent(RegisterEmailActivity.this, MainLoginEmailActivity.class));
            finish(); // Close this activity
        });


        registerBtn.setOnClickListener(v -> validData());

        // Handle edge to edge and system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private String email, password, cPassword;

    private void validData() {
        email = emailEt.getText().toString().trim();
        password = passwordEt.getText().toString();
        cPassword = cPasswordEt.getText().toString();
        Log.d(TAG, "validData: email: " + email);
        Log.d(TAG, "validData: password: " + password);
        Log.d(TAG, "validData: cPassword: " + cPassword);

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEt.setError("Invalid Email Pattern");
            emailEt.requestFocus();
        } else if (password.isEmpty()) {
            passwordEt.setError("Enter Password");
            passwordEt.requestFocus();
        } else if (!password.equals(cPassword)) {
            cPasswordEt.setError("Password Not Match");
            cPasswordEt.requestFocus();
        } else {
            registerUser();
        }
    }

    private void registerUser() {
        progressDialog.setMessage("Creating Account");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Log.d(TAG, "onSuccess: Register Success");
                    updateUserInfo();
                    progressDialog.dismiss();
                    // Redirect to another activity or show success message
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: ", e);
                    Utils.toast(RegisterEmailActivity.this, "Failed due to " + e.getMessage());
                    progressDialog.dismiss();
                });
    }

    private void updateUserInfo() {
        progressDialog.setMessage("Saving User Info");

        long timestamp = Utils.getTimestamp();
        String registerUserEmail = firebaseAuth.getCurrentUser().getEmail();
        String registerUserId = firebaseAuth.getUid();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", "");
        hashMap.put("phoneCode", "");
        hashMap.put("phoneNumber", "");
        hashMap.put("profileImageUrl", "");
        hashMap.put("dob", "");
        hashMap.put("UserType", "Email");
        hashMap.put("typingTo", "");
        hashMap.put("timestamp", timestamp);
        hashMap.put("onlineStatus", true);
        hashMap.put("email", registerUserEmail);
        hashMap.put("uid", registerUserId);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.child(registerUserId)
                .setValue(hashMap)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "onSuccess: Info saved...");
                    progressDialog.dismiss();
                    startActivity(new Intent(RegisterEmailActivity.this, MainActivity.class));
                    finishAffinity();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: ", e);
                    progressDialog.dismiss();
                    Utils.toast(RegisterEmailActivity.this, "Failed due to save info due to" + e.getMessage());
                });
    }
}
