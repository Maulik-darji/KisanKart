package com.android.agrifarm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.agrifarm.databinding.ActivityLoginOptionsBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginOptionsActivity extends AppCompatActivity {

    // Declare the view binding variable
    private ActivityLoginOptionsBinding binding;

    private static final String TAG = "LOGIN_OPTIONS_TAG";

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using view binding
        binding = ActivityLoginOptionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // Ensure this is defined in strings.xml
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Close button
        binding.closeBtn.setOnClickListener(v -> finish());

        // MaterialButton for email login
        binding.loginEmailBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginOptionsActivity.this, MainLoginEmailActivity.class);
            startActivity(intent);
        });

        binding.loginPhoneBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(LoginOptionsActivity.this, LoginPhoneActivity.class));
            }
        });

        // Google login button
        binding.loginGoogleBtn.setOnClickListener(v -> beginGoogleLogin());
    }

    private void beginGoogleLogin() {
        Log.d(TAG, "beginGoogleLogin: ");

        // Get the Google sign-in intent
        Intent googleSignInIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(googleSignInIntent);
    }

    private ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "onActivityResult: ");
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            Log.d(TAG, "onActivityResult: Account ID:" + account.getId());
                            firebaseAuthWithGoogleAccount(account.getIdToken());
                        } catch (ApiException e) {
                            Log.e(TAG, "onActivityResult: ", e);
                        }
                    } else {
                        Log.d(TAG, "onActivityResult: Cancelled");
                        Utils.toast(LoginOptionsActivity.this, "Cancelled...");
                    }
                }
            }
    );

    private void firebaseAuthWithGoogleAccount(String idToken) {
        Log.d(TAG, "firebaseAuthWithGoogleAccount: idToken" + idToken);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    if (authResult.getAdditionalUserInfo().isNewUser()) {
                        Log.d(TAG, "onSuccess: New User, Account Created...");
                        updateUserInfoDb();
                    } else {
                        Log.d(TAG, "onSuccess: Existing User, Logged In");
                        startActivity(new Intent(LoginOptionsActivity.this, MainActivity.class));
                        finishAffinity();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: ", e);
                    Utils.toast(LoginOptionsActivity.this, "" + e.getMessage());
                });
    }

    private void updateUserInfoDb() {
        Log.d(TAG, "updateUserInfoDb: ");

        progressDialog.setMessage("Saving User Info");
        progressDialog.show();

        long timestamp = Utils.getTimestamp();
        String registerUserEmail = firebaseAuth.getCurrentUser().getEmail();
        String registerUserId = firebaseAuth.getUid();
        String name = firebaseAuth.getCurrentUser().getDisplayName();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", "" + name);
        hashMap.put("phoneCode", "");
        hashMap.put("phoneNumber", "");
        hashMap.put("profileImageUrl", "");
        hashMap.put("dob", "");
        hashMap.put("UserType", "Google");
        hashMap.put("typingTo", "");
        hashMap.put("timestamp", timestamp);
        hashMap.put("onlineStatus", true);
        hashMap.put("email", registerUserEmail);
        hashMap.put("uid", registerUserId);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
        ref.child(registerUserId)
                .setValue(hashMap)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "onSuccess: User info saved...");
                    progressDialog.dismiss();

                    startActivity(new Intent(LoginOptionsActivity.this, MainActivity.class));
                    finishAffinity();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: ", e);
                    Utils.toast(LoginOptionsActivity.this, "Failed to save user info due to " + e.getMessage());
                });
    }
}
