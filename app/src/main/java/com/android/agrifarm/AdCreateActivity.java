package com.android.agrifarm;

import static com.android.agrifarm.Utils.condition;

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
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.agrifarm.Crop;
import com.android.agrifarm.databinding.ActivityAdCreateBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdCreateActivity extends AppCompatActivity {

    private ActivityAdCreateBinding binding;

    private static final String TAG = "AD_CREATE_TAG";

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    private Uri imageUri = null;

    private ArrayList<ModelImagePicked> imagePickedArrayList;
    private AdapterImagesPicked adapterImagesPicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();

        ArrayAdapter<String> adapterCategoriee = new ArrayAdapter<>(this, R.layout.row_category_act, Utils.categories);
        binding.categoryAct.setAdapter(adapterCategoriee);

        ArrayAdapter<String> adapterConditions = new ArrayAdapter<>(this, R.layout.row_condition_act, condition);
        binding.QuantityEt.setAdapter(adapterConditions);

        imagePickedArrayList = new ArrayList<>();
        loadImages();

        // Set up the back button functionality
        // Set up the back button functionality
        ImageButton backBtn = binding.toolbarBackBtn; // Corrected line
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // This will close the current activity and return to the previous one
            }
        });

        binding.toolbarAdImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickOptions();
            }
        });

        binding.postAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private void loadImages() {
        Log.d(TAG, "loadImages: ");
        adapterImagesPicked = new AdapterImagesPicked(this, imagePickedArrayList);
        binding.imagesRv.setAdapter(adapterImagesPicked);
    }

    private void showImagePickOptions() {
        Log.d(TAG, "showImagePickOptions: ");
        PopupMenu popupMenu = new PopupMenu(this, binding.toolbarAdImageBtn);

        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Camera");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "Gallery");

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == 1) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        String[] cameraPermissions = new String[]{Manifest.permission.CAMERA};
                        requestCameraPermissions.launch(cameraPermissions);
                    } else {
                        String[] cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestCameraPermissions.launch(cameraPermissions);
                    }
                } else if (itemId == 2) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        pickImageGallery();
                    } else {
                        String storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                        requestStoragePermission.launch(storagePermission);
                    }
                }
                return true;
            }
        });
    }

    private ActivityResultLauncher<String> requestStoragePermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {
                    Log.d(TAG, "onActivityResult: isGranted: " + isGranted);
                    if (isGranted) {
                        pickImageGallery();
                    } else {
                        Utils.toast(AdCreateActivity.this, "Storage Permission denied...");
                    }
                }
            }
    );

    private ActivityResultLauncher<String[]> requestCameraPermissions = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    Log.d(TAG, "onActivityResult: " + result);
                    boolean areAllGranted = true;
                    for (Boolean isGranted : result.values()) {
                        areAllGranted = areAllGranted && isGranted;
                    }
                    if (areAllGranted) {
                        pickImageCamera();
                    } else {
                        Utils.toast(AdCreateActivity.this, "Camera or Storage or both permissions denied...");
                    }
                }
            }
    );

    private void pickImageGallery() {
        Log.d(TAG, "pickImageGallery: ");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    private void pickImageCamera() {
        Log.d(TAG, "pickImageCamera: ");
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "TEMPORARY_IMAGE");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "TEMPORARY_IMAGE_DESCRIPTION");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "onActivityResult: ");
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        imageUri = data.getData();
                        Log.d(TAG, "onActivityResult: imageUri " + imageUri);
                        String timeStamp = "" + Utils.getTimestamp();

                        ModelImagePicked modelImagePicked = new ModelImagePicked(timeStamp, imageUri, null, false);
                        imagePickedArrayList.add(modelImagePicked);
                        loadImages();
                    } else {
                        Utils.toast(AdCreateActivity.this, "Cancelled...!");
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "onActivityResult: ");
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: imageUri" + imageUri);
                        String timeStamp = "" + System.currentTimeMillis();
                        ModelImagePicked modelImagePicked = new ModelImagePicked(timeStamp, imageUri, null, false);
                        imagePickedArrayList.add(modelImagePicked);
                        loadImages();
                    } else {
                        Utils.toast(AdCreateActivity.this, "Cancelled...!");
                    }
                }
            }
    );

    private String crop = "";
    private String category = "";
    private String quantity = "";
    private String address = "";
    private String price = "";
    private String title = "";
    private String description = "";
    private double latitude = 0;
    private double longitude = 0;

    private void validateData() {
        Log.d(TAG, "validateData: ");
        crop = binding.produceEt.getText().toString().trim();
        category = binding.categoryAct.getText().toString().trim();
        quantity = binding.QuantityEt.getText().toString().trim();
        address = binding.locationAct.getText().toString().trim();
        price = binding.priceEt.getText().toString().trim();
        title = binding.titleEt.getText().toString().trim();
        description = binding.descriptionEt.getText().toString().trim();

        if (crop.isEmpty()) {
            binding.produceEt.setError("Enter Brand");
            binding.produceEt.requestFocus();
        } else if (category.isEmpty()) {
            binding.categoryAct.setError("Choose Category");
            binding.categoryAct.requestFocus();
        } else if (quantity.isEmpty()) {
            binding.QuantityEt.setError("Choose Condition");
            binding.QuantityEt.requestFocus();
        } else if (title.isEmpty()) {
            binding.titleEt.setError("Enter Title");
            binding.titleEt.requestFocus();
        } else if (description.isEmpty()) {
            binding.descriptionEt.setError("Enter Description");
            binding.descriptionEt.requestFocus();
        } else if (imagePickedArrayList.isEmpty()) {
            Utils.toast(this, "Pick at least one image");
        } else {
            postAd();
        }
    }

    private void postAd() {
        Log.d(TAG, "postAd: ");
        progressDialog.setMessage("Publishing Ad...");
        progressDialog.show();

        long timestamp = Utils.getTimestamp();
        DatabaseReference refAds = FirebaseDatabase.getInstance().getReference("Ads");
        String keyId = refAds.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", keyId);
        hashMap.put("uid", firebaseAuth.getUid());
        hashMap.put("crop", crop);
        hashMap.put("category", category);
        hashMap.put("quantity", quantity);
        hashMap.put("address", address);
        hashMap.put("price", price);
        hashMap.put("title", title);
        hashMap.put("description", description);
        hashMap.put("latitude", latitude);
        hashMap.put("longitude", longitude);
        hashMap.put("status", Utils.AD_STATUS_AVAILABLE);
        hashMap.put("timestamp", timestamp);

        refAds.child(keyId)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Ad Published");
                        uploadImagesStorage(keyId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                        progressDialog.dismiss();
                        Utils.toast(AdCreateActivity.this, "Failed to publish Ad due to " + e.getMessage());
                    }
                });
    }

    private void uploadImagesStorage(String adId) {
        Log.d(TAG, "uploadImagesStorage: ");
        for (int i = 0; i < imagePickedArrayList.size(); i++) {
            ModelImagePicked modelImagePicked = imagePickedArrayList.get(i);
            String imageName = modelImagePicked.getId();
            String filePathAndName = "Ads/" + imageName;

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);

            int imageIndexForProgress = i + 1;
            storageReference.putFile(modelImagePicked.getImageUri())
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            String message = "Uploading " + imageIndexForProgress + " of " + imagePickedArrayList.size() + " images...\n Progress " + (int) progress + "%";
                            Log.d(TAG, "onProgress: message" + message);
                            progressDialog.setMessage(message);
                            progressDialog.show();
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "onSuccess: ");
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            Uri uploadedImageUrl = uriTask.getResult();

                            if (uriTask.isSuccessful()) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("id", modelImagePicked.getId());
                                hashMap.put("imageUrl", uploadedImageUrl.toString());

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
                                ref.child(adId).child("Images").child(imageName)
                                        .updateChildren(hashMap);
                            }
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: ", e);
                            progressDialog.dismiss();
                            Utils.toast(AdCreateActivity.this, "Failed to upload image due to " + e.getMessage());
                        }
                    });
        }
    }
}
