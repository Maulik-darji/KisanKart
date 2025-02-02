package com.android.agrifarm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.android.agrifarm.databinding.FragmentAccountBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private Context mContext;
    private FragmentAccountBinding binding; // ViewBinding instance
    private static final String TAG = "ACCOUNT_TAG";

    @Override
    public void onAttach(@NonNull Context context) {
        mContext = context;
        super.onAttach(context);
    }

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout using ViewBinding
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Return the root view of the binding object
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize UI listeners
        setupUIListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMyInfo(); // Ensure data is loaded once the fragment is visible
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Nullify the binding to avoid memory leaks
        binding = null;
    }

    private void loadMyInfo() {
        if (binding == null) return; // Ensure binding is not null before proceeding

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user");
        ref.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (binding == null) return; // Ensure binding is not null before updating the UI

                        String dob = "" + snapshot.child("dob").getValue();
                        String email = "" + snapshot.child("email").getValue();
                        String name = "" + snapshot.child("name").getValue();
                        String phoneCode = "" + snapshot.child("phoneCode").getValue();
                        String phoneNumber = "" + snapshot.child("phoneNumber").getValue();
                        String timestamp = "" + snapshot.child("timestamp").getValue();
                        String userType = "" + snapshot.child("userType").getValue();
                        String profileImageUrl = "" + snapshot.child("profileImageUrl").getValue();

                        String phone = phoneCode + phoneNumber;

                        if (timestamp.equals("null")) {
                            timestamp = "0";
                        }

                        String formattedDate = Utils.formatTimestampDate(Long.parseLong(timestamp));

                        // Set values using ViewBinding
                        binding.emailTv.setText(email);
                        binding.nameTv.setText(name);
                        binding.phoneTv.setText(phone);
                        binding.memberSinceTv.setText(formattedDate);
                        binding.dobTv.setText(dob);

                        if (userType.equals("Email")) {
                            boolean isVerified = firebaseAuth.getCurrentUser().isEmailVerified();
                            if (isVerified) {
                                binding.verificationTv.setText("Verified");
                            } else {
                                binding.verificationTv.setText("Not Verified");
                            }
                        } else {
                            binding.verificationTv.setText("Verified");
                        }

                        // Load image with Glide
                        try {
                            Glide.with(mContext)
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.menu_person)
                                    .into(binding.profileTv);
                        } catch (Exception e) {
                            Log.e(TAG, "onDataChange: ", e);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle failure
                    }
                });
    }

    private void setupUIListeners() {
        // Using ViewBinding to reference and handle UI events
        binding.logOutCv.setOnClickListener(v -> {
            firebaseAuth.signOut();
            startActivity(new Intent(mContext, MainActivity.class));
            getActivity().finishAffinity();
        });

        binding.editProfileCv.setOnClickListener(v -> {
            startActivity(new Intent(mContext, ProfileEdit.class));
        });

        binding.changePasswordCv.setOnClickListener(v -> {
            startActivity(new Intent(mContext, ChangePasswordActivity.class));
        });

        binding.verifyAccountCv.setOnClickListener(v -> {
            verifyAccount();
        });

        binding.deleteAccountCv.setOnClickListener(v -> {
            startActivity(new Intent(mContext, DeleteAccountActivity.class));
            getActivity().finishAffinity();
        });
    }

    private void verifyAccount() {
        // Your account verification logic here
    }
}