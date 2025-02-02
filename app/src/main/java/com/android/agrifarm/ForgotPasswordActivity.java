package com.android.agrifarm;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = "FORGOT_PASS_TAG";

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    private EditText emailEt;
    private Button submitBtn;
    private View toolbarBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password); // Use the layout directly here

        // Initialize views
        emailEt = findViewById(R.id.emailEt);
        submitBtn = findViewById(R.id.submitBtn);
        toolbarBackBtn = findViewById(R.id.toolbarBackBtn);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        // Toolbar back button click listener
        toolbarBackBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        // Submit button click listener
        submitBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                validateData();
            }
        });
    }

    private String email = "";

    private void validateData(){
        Log.d(TAG, "validateData: ");

        email = emailEt.getText().toString().trim();

        Log.d(TAG, "validateData: email: " + email);

        // Check if the email is valid
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEt.setError("Invalid Email Pattern!");
            emailEt.requestFocus();
        }
        else{
            sendPasswrodRecoveryInstrutions();
        }
    }

    private void sendPasswrodRecoveryInstrutions(){
        Log.d(TAG, "sendPasswrodRecoveryInstrutions: ");
        progressDialog.setMessage("Sending password recovery instruction to " + email);
        progressDialog.show();

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Utils.toast(ForgotPasswordActivity.this, "Instructions to reset password are sent to " + email);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                        progressDialog.dismiss();
                        Utils.toast(ForgotPasswordActivity.this, "Failed to send due to " + e.getMessage());
                    }
                });
    }
}
