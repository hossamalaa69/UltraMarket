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

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.Brand;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;

    private DatabaseReference brandDbReference;

    private Uri selectedImage = null;

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
    }

    public void saveBrand(View view) {
        if(selectedImage != null && !name_brand.getText().toString().isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            name_brand.setEnabled(false);
            saveButton.setEnabled(false);
            uploadImageButton.setEnabled(false);
            uploadToFirebase();
        }else{
            Toast.makeText(this, getString(R.string.enter_full_data), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadToFirebase() {

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("Brands");

        final StorageReference photoRef = mStorageReference.child(selectedImage.getLastPathSegment());
        UploadTask uploadTask = photoRef.putFile(selectedImage);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return photoRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
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
                    // Handle failures
                    Toast.makeText(BrandActivity.this, R.string.failed_upload, Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
                name_brand.setEnabled(true);
                saveButton.setEnabled(true);
                uploadImageButton.setEnabled(true);
                finish();
                onBackPressed();
            }
        });
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


}