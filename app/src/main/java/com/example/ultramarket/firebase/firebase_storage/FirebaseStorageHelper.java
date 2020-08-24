package com.example.ultramarket.firebase.firebase_storage;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.ultramarket.helpers.AppExecutors;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FirebaseStorageHelper {
    private static FirebaseStorageHelper sInstance;
    private FirebaseStorage mFirebaseStorage;

    public FirebaseStorageHelper() {
        mFirebaseStorage = FirebaseStorage.getInstance();
    }

    public static FirebaseStorageHelper getsInstance() {
        if (sInstance == null) {
            sInstance = new FirebaseStorageHelper();
        }
        return sInstance;
    }

    public void uploadImage(Uri imageUri, OnCompleteListener<Uri> listener) {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                StorageReference imageRef = mFirebaseStorage.getReference()
                        .child("user_images").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(imageUri.getLastPathSegment());
                UploadTask task = imageRef.putFile(imageUri);
                Task<Uri> uriTask = task.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return imageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(listener);
            }
        });

    }

}
