package com.example.ultramarket.ui.adminLayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.Brand;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class BrandActivity extends AppCompatActivity {

    private final static int GET_FROM_GALLERY = 1000;
    private final static int READ_MEDIA_PERMISSION_CODE = 1001;

    private StorageReference mStorageReference;

    private DatabaseReference brandDbReference;

    private Uri selectedImage = null;

    private String oldID = null;
    private String oldName = null;
    private String oldImageUrl = null;


    private ImageView img_brand;
    private ProgressBar progressBar;
    private EditText name_brand;
    private FloatingActionButton saveButton;
    private FloatingActionButton uploadImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand);

        img_brand = (ImageView) findViewById(R.id.brand_image);
        name_brand = (EditText) findViewById(R.id.brand_name);
        progressBar= (ProgressBar) findViewById(R.id.upload_image_progress);
        saveButton = (FloatingActionButton) findViewById(R.id.btn_save);
        uploadImageButton = (FloatingActionButton) findViewById(R.id.btn_upload_img);

        receiveBrand();
    }

    private void receiveBrand() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            oldID = extras.getString("ID");
            oldImageUrl = extras.getString("imageUrl");
            oldName = extras.getString("brand_name");

            name_brand.setText(oldName);
            Glide.with(this)
                    .load(oldImageUrl)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(img_brand);
        }
    }

    public void saveBrand(View view) {
        //case insert
        if(selectedImage != null && !name_brand.getText().toString().isEmpty() && oldID==null) {
            lockUI();
            insertBrand();
        }
        //case update
        else if(oldID!=null && !name_brand.getText().toString().isEmpty()){
                lockUI();
                updateBrand(selectedImage != null);
        }
        else{
            Toast.makeText(this, getString(R.string.enter_full_data), Toast.LENGTH_SHORT);
        }
    }

    private void updateBrand(boolean hasNewImage) {
        mStorageReference = FirebaseStorage.getInstance().getReference().child("Brands");

        if(hasNewImage){
            StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl);
            imageRef.delete().addOnSuccessListener(aVoid -> {
                Toast.makeText(BrandActivity.this, "Old image deleted", Toast.LENGTH_SHORT).show();
                final StorageReference photoRef = mStorageReference.child(selectedImage.getLastPathSegment());
                UploadTask uploadTask = photoRef.putFile(selectedImage);
                Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return photoRef.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String imageUrl = downloadUri.toString();
                        Toast.makeText(BrandActivity.this, R.string.uploaded_success, Toast.LENGTH_SHORT).show();
                        brandDbReference = FirebaseDatabase.getInstance().getReference(Brand.class.getSimpleName());
                        String id = oldID;
                        Brand brand = new Brand(id, name_brand.getText().toString(), imageUrl);
                        brandDbReference.child(id).setValue(brand);
                        Toast.makeText(BrandActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BrandActivity.this, "Failed update", Toast.LENGTH_SHORT).show();
                    }
                    openUI();
                    finish();
                    onBackPressed();
                });
            });
        }else{
            brandDbReference = FirebaseDatabase.getInstance().getReference(Brand.class.getSimpleName());
            String id = oldID;
            Brand brand = new Brand(id, name_brand.getText().toString(), oldImageUrl);
            brandDbReference.child(id).setValue(brand);
            Toast.makeText(BrandActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();

            openUI();
            finish();
            onBackPressed();
        }

    }

    private void insertBrand() {

        mStorageReference = FirebaseStorage.getInstance().getReference().child("Brands");

        final StorageReference photoRef = mStorageReference.child(selectedImage.getLastPathSegment());
        UploadTask uploadTask = photoRef.putFile(selectedImage);

        Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return photoRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                String imageUrl = downloadUri.toString();
                Toast.makeText(BrandActivity.this, R.string.uploaded_success, Toast.LENGTH_SHORT).show();
                brandDbReference = FirebaseDatabase.getInstance().getReference(Brand.class.getSimpleName());
                String id = brandDbReference.push().getKey();
                Brand brand = new Brand(id, name_brand.getText().toString(), imageUrl);
                brandDbReference.child(id).setValue(brand);
                Toast.makeText(BrandActivity.this, R.string.brand_added, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(BrandActivity.this, R.string.failed_upload, Toast.LENGTH_SHORT).show();
            }
            openUI();
            finish();
            onBackPressed();
        });
    }

    private void lockUI() {
        progressBar.setVisibility(View.VISIBLE);
        name_brand.setEnabled(false);
        saveButton.setEnabled(false);
        uploadImageButton.setEnabled(false);
    }

    private void openUI() {
        progressBar.setVisibility(View.GONE);
        name_brand.setEnabled(true);
        saveButton.setEnabled(true);
        uploadImageButton.setEnabled(true);
    }

    public void uploadFromGallery(View view) {

        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            getImageFromGallery();
        }
        else{
            if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(this, R.string.allow_permission
                        , Toast.LENGTH_SHORT).show();
            }
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permissions, READ_MEDIA_PERMISSION_CODE);
        }
    }

    private void getImageFromGallery() {
        startActivityForResult(new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == READ_MEDIA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getImageFromGallery();
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            selectedImage = data.getData();
            img_brand.setImageURI(selectedImage);
        }
    }

    private void onComplete(Task<Uri> task) {
        if (task.isSuccessful()) {
            Uri downloadUri = task.getResult();
            String imageUrl = downloadUri.toString();
            Toast.makeText(BrandActivity.this, R.string.uploaded_success, Toast.LENGTH_SHORT).show();
            brandDbReference = FirebaseDatabase.getInstance().getReference(Brand.class.getSimpleName());
            String id = oldID;
            Brand brand = new Brand(id, name_brand.getText().toString(), imageUrl);
            brandDbReference.child(id).setValue(brand);
            Toast.makeText(BrandActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(BrandActivity.this, "Failed update", Toast.LENGTH_SHORT).show();
        }
        openUI();
        finish();
        onBackPressed();
    }

    private void onSuccess(Void aVoid) {
        Toast.makeText(BrandActivity.this, "Old image deleted", Toast.LENGTH_SHORT).show();
        final StorageReference photoRef = mStorageReference.child(selectedImage.getLastPathSegment());
        UploadTask uploadTask = photoRef.putFile(selectedImage);
        Task<Uri> urlTask = uploadTask.continueWithTask((Task<UploadTask.TaskSnapshot> task) -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return photoRef.getDownloadUrl();
        }).addOnCompleteListener(this::onComplete);
    }
}