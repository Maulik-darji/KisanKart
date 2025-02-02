package com.android.agrifarm;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.agrifarm.databinding.ActivityProfileEditBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class ProfileEdit extends AppCompatActivity {

    private ActivityProfileEditBinding binding;
    private static final String TAG = "PROFILE_EDIT_TAG";
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private Uri imageUri = null;
    private String name = "";
    private String dob = "";
    private String email = "";
    private String phoneCode = "";
    private String phoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();

        loadMyInfo();

        binding.toolbarBackBtn.setOnClickListener(v -> finish());

        binding.profileImagePickFab.setOnClickListener(v -> imagePickDialog());

        binding.updateBtn.setOnClickListener(v -> validateData());
    }

    private void validateData() {
        name = binding.nameEt.getText().toString().trim();
        dob = binding.dobEt.getText().toString().trim();
        email = binding.emailEt.getText().toString().trim();
        phoneCode = binding.countryCodePicker.getSelectedCountryCodeWithPlus();
        phoneNumber = binding.phoneNumberEt.getText().toString().trim();

        if (name.isEmpty()) {
            binding.nameEt.setError("Enter name");
            return;
        }
        if (dob.isEmpty()) {
            binding.dobEt.setError("Enter DOB");
            return;
        }
        if (email.isEmpty()) {
            binding.emailEt.setError("Enter email");
            return;
        }
        if (phoneNumber.isEmpty()) {
            binding.phoneNumberEt.setError("Enter phone number");
            return;
        }

        if (imageUri == null) {
            updateProfileDb(null);
        } else {
            uploadProfileImageStorage();
        }
    }

    private void uploadProfileImageStorage() {
        Log.d(TAG, "uploadProfileImageStorage: ");
        progressDialog.setMessage("Uploading user profile image...");
        progressDialog.show();

        String filePathAndName = "UserImages/" + "profile_" + firebaseAuth.getUid();

        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putFile(imageUri)
                .addOnProgressListener(snapshot -> {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    Log.d(TAG, "onProgress: Progress: " + progress);
                    progressDialog.setMessage("Uploading profile image. Progress: " + (int) progress + "%");
                })
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d(TAG, "onSuccess: Uploaded");
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    uriTask.addOnSuccessListener(uri -> updateProfileDb(uri.toString()));
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: ", e);
                    progressDialog.dismiss();
                    Utils.toast(ProfileEdit.this, "Failed to upload profile image due to " + e.getMessage());
                });
    }

    private void updateProfileDb(String imageUrl) {
        progressDialog.setMessage("Updating user info...");
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name);
        hashMap.put("dob", dob);
        hashMap.put("email", email); // Always save email
        hashMap.put("phoneCode", phoneCode); // Always save phone code
        hashMap.put("phoneNumber", phoneNumber); // Always save phone number

        if (imageUrl != null) {
            hashMap.put("profileImageUrl", imageUrl);
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
        reference.child(firebaseAuth.getUid())
                .updateChildren(hashMap)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "onSuccess: Info updated");
                    progressDialog.dismiss();
                    Utils.toast(ProfileEdit.this, "Profile Updated...");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: ", e);
                    progressDialog.dismiss();
                    Utils.toast(ProfileEdit.this, "Failed to update info due to " + e.getMessage());
                });
    }

    private void loadMyInfo() {
        Log.d(TAG, "loadMyInfo: ");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user");
        ref.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String dob = "" + snapshot.child("dob").getValue();
                        String email = "" + snapshot.child("email").getValue();
                        String name = "" + snapshot.child("name").getValue();
                        String phoneCode = "" + snapshot.child("phoneCode").getValue();
                        String phoneNumber = "" + snapshot.child("phoneNumber").getValue();
                        String profileImageUrl = "" + snapshot.child("profileImageUrl").getValue();

                        // Handle null/empty phoneCode
                        if (phoneCode == null || phoneCode.isEmpty() || phoneCode.equals("null")) {
                            phoneCode = "+1"; // Default country code
                        }

                        // Update UI
                        binding.emailEt.setText(email);
                        binding.nameEt.setText(name);
                        binding.phoneNumberEt.setText(phoneNumber);
                        binding.dobEt.setText(dob);

                        // Set country code safely
                        try {
                            int code = Integer.parseInt(phoneCode.replace("+", ""));
                            binding.countryCodePicker.setCountryForPhoneCode(code);
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Invalid phoneCode: " + phoneCode);
                            binding.countryCodePicker.setCountryForPhoneCode(1); // Fallback to US
                        }

                        // Load profile image
                        try {
                            Glide.with(ProfileEdit.this)
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.menu_person)
                                    .into(binding.profileTv);
                        } catch (Exception e) {
                            Log.e(TAG, "onDataChange: ", e);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "onCancelled: " + error.getMessage());
                    }
                });
    }

    private void imagePickDialog() {
        PopupMenu popupMenu = new PopupMenu(this, binding.profileImagePickFab);
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Camera");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "Gallery");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 1) {
                Log.d(TAG, "onMenuItemClick: Camera Clicked");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestCameraPermission.launch(new String[]{Manifest.permission.CAMERA});
                } else {
                    requestCameraPermission.launch(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE});
                }
            } else if (item.getItemId() == 2) {
                Log.d(TAG, "onMenuItemClick: Gallery Clicked");
                pickImageGallery();
            }
            return true;
        });
    }

    private final ActivityResultLauncher<String[]> requestCameraPermission = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                boolean areAllGranted = true;
                for (Boolean isGranted : result.values()) {
                    areAllGranted = areAllGranted && isGranted;
                }
                if (areAllGranted) {
                    pickImageCamera();
                } else {
                    Utils.toast(ProfileEdit.this, "Camera or Storage permissions denied...");
                }
            }
    );

    private void pickImageCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "TEMP_TITLE");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "TEMP_DESCRIPTION");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Glide.with(ProfileEdit.this).load(imageUri).into(binding.profileTv);
                } else {
                    Utils.toast(ProfileEdit.this, "Cancelled...");
                }
            }
    );

    private void pickImageGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    imageUri = data.getData();
                    Glide.with(ProfileEdit.this).load(imageUri).into(binding.profileTv);
                } else {
                    Utils.toast(ProfileEdit.this, "Cancelled...");
                }
            }
    );
}