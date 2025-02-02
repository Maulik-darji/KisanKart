package com.android.agrifarm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class LoginPhoneActivity extends AppCompatActivity {

    private static final String TAG = "LOGIN_PHONE_TAG";

    private RelativeLayout phoneInputRL, otpInputRL;
    private TextView resendOtpTV, loginLabelTv;
    private EditText phoneNumberEt, otpEt;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private String mVerificationId;
    private String phoneCode = "", phoneNumber = "", phoneNumberWithCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone);

        // Initialize views using findViewById
        phoneInputRL = findViewById(R.id.phoneInputRL);
        otpInputRL = findViewById(R.id.otpInputRL);
        resendOtpTV = findViewById(R.id.resendOtpTV);
        loginLabelTv = findViewById(R.id.loginLabelTv);
        phoneNumberEt = findViewById(R.id.phoneNumberEt);
        otpEt = findViewById(R.id.otpEt);

        // Set visibility of layouts
        phoneInputRL.setVisibility(View.VISIBLE);
        otpInputRL.setVisibility(View.GONE);

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        phoneLoginCallBack();

        // Back button listener
        findViewById(R.id.toolbarBackBtn).setOnClickListener(v -> finish());

        // Send OTP button listener
        findViewById(R.id.sendOtpBtn).setOnClickListener(v -> validateData());

        // Resend OTP button listener
        resendOtpTV.setOnClickListener(v -> resendVerificationCode(forceResendingToken));

        // Verify OTP button listener
        findViewById(R.id.verifyOtpBtn).setOnClickListener(v -> {
            String otp = otpEt.getText().toString().trim();
            Log.d(TAG, "onClick: OTP: " + otp);
            if (otp.isEmpty()) {
                Utils.toast(LoginPhoneActivity.this, "Enter OTP!");
                otpEt.setError("Enter OTP");
                otpEt.requestFocus();
            } else if (otp.length() < 6) {
                otpEt.setError("OTP length must be 6 characters");
                otpEt.requestFocus();
            } else {
                verifyPhoneNumberWithCode(mVerificationId, otp);
            }
        });

        // Handle window insets for proper padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void validateData() {
        phoneCode = "+91"; // Hardcoding for simplicity (you can use a country picker here)
        phoneNumber = phoneNumberEt.getText().toString().trim();
        phoneNumberWithCode = phoneCode + phoneNumber;

        Log.d(TAG, "validateData: phoneCode " + phoneCode);
        Log.d(TAG, "validateData: phoneNumber " + phoneNumber);
        Log.d(TAG, "validateData: phoneNumberWithCode " + phoneNumberWithCode);

        if (phoneNumber.isEmpty()) {
            Utils.toast(this, "Please enter phone number");
            phoneNumberEt.setError("Phone number is required");
        } else {
            startPhoneNumberVerification();
        }
    }

    private void startPhoneNumberVerification() {
        Log.d(TAG, "startPhoneNumberVerification: ");
        progressDialog.setMessage("Sending OTP to " + phoneNumberWithCode);
        progressDialog.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumberWithCode)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void phoneLoginCallBack() {
        Log.d(TAG, "phoneLoginCallBack: ");

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted: ");
                signinWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.e(TAG, "onVerificationFailed: ", e);
                progressDialog.dismiss();
                Utils.toast(LoginPhoneActivity.this, "" + e.getMessage());
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);

                mVerificationId = verificationId;
                forceResendingToken = token;

                progressDialog.dismiss();

                phoneInputRL.setVisibility(View.GONE);
                otpInputRL.setVisibility(View.VISIBLE);

                Utils.toast(LoginPhoneActivity.this, "OTP sent to " + phoneNumberWithCode);
                loginLabelTv.setText("Please enter the verification code sent to " + phoneNumberWithCode);
            }
        };
    }

    private void verifyPhoneNumberWithCode(String verificationId, String otp) {
        Log.d(TAG, "verifyPhoneNumberWithCode: verificationId: " + verificationId);
        Log.d(TAG, "verifyPhoneNumberWithCode: otp: " + otp);
        progressDialog.setMessage("Verifying OTP");
        progressDialog.show();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        signinWithPhoneAuthCredential(credential);
    }

    private void resendVerificationCode(PhoneAuthProvider.ForceResendingToken token) {
        Log.d(TAG, "resendVerificationCode: ForceResendingToken: " + token);

        progressDialog.setMessage("Resending OTP to " + phoneNumberWithCode);
        progressDialog.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumberWithCode)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .setForceResendingToken(token)
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signinWithPhoneAuthCredential(PhoneAuthCredential credential) {
        Log.d(TAG, "signinWithPhoneAuthCredential: ");
        progressDialog.setMessage("Logging In");

        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    Log.d(TAG, "onSuccess: Logged in");

                    if (authResult.getAdditionalUserInfo().isNewUser()) {
                        Log.d(TAG, "onSuccess: New User, Account created...");
                        updateUserInfoDb();
                    } else {
                        Log.d(TAG, "onSuccess: Existing User, Logged In");
                        startActivity(new Intent(LoginPhoneActivity.this, MainActivity.class));
                        finishAffinity();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: ", e);
                    progressDialog.dismiss();
                    Utils.toast(LoginPhoneActivity.this, "Failed to login: " + e.getMessage());
                });
    }

    private void updateUserInfoDb() {
        Log.d(TAG, "updateUserInfoDb: ");
        progressDialog.setMessage("Saving user info");
        progressDialog.show();

        String registerUserId = firebaseAuth.getUid();
        long timestamp = Utils.getTimestamp();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", "");
        hashMap.put("phoneCode", phoneCode);
        hashMap.put("phoneNumber", phoneNumber);
        hashMap.put("profileImageUrl", "");
        hashMap.put("dob", "");
        hashMap.put("UserType", "Phone");
        hashMap.put("typingTo", "");
        hashMap.put("timestamp", timestamp);
        hashMap.put("onlineStatus", true);
        hashMap.put("email", "");
        hashMap.put("uid", registerUserId);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user");
        ref.child(registerUserId)
                .setValue(hashMap)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "onSuccess: User info saved");
                    progressDialog.dismiss();

                    startActivity(new Intent(LoginPhoneActivity.this, MainActivity.class));
                    finishAffinity();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: ", e);
                    Utils.toast(LoginPhoneActivity.this, "Failed to save user info: " + e.getMessage());
                });
    }
}
