package com.example.ultramarket.firebase;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.ultramarket.database.Entities.User;
import com.example.ultramarket.helpers.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseAuthHelper {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static FirebaseAuthHelper sInstance;

    public static FirebaseAuthHelper getsInstance() {
        if (sInstance == null) {
            sInstance = new FirebaseAuthHelper();
        }
        return sInstance;
    }

    private static FirebaseAuthCallBacks firebaseAuthCallBacks;

    public void updateUserData(User user) {
        FirebaseDatabase.getInstance().getReference()
                .child(User.class.getSimpleName())
                .child(user.getID()).child("latitude").setValue(user.getLatitude());
        FirebaseDatabase.getInstance().getReference()
                .child(User.class.getSimpleName())
                .child(user.getID()).child("longitude").setValue(user.getLongitude());
    }

    public interface FirebaseAuthCallBacks {
        void onLoginStateChanges(User user);

        void onLoggedOutStateChanges();
    }

    public FirebaseAuthHelper() {
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public void attachAuthStateListener(Context context) {
        firebaseAuthCallBacks = (FirebaseAuthCallBacks) context;
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // loggedIn
                    insertUser(user);
                    firebaseAuthCallBacks.onLoginStateChanges(new User(user));

                } else {
                    firebaseAuthCallBacks.onLoggedOutStateChanges();
                    // logged out
                }
            }
        };
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void insertUser(FirebaseUser user) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child(User.class.getSimpleName());
        userRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {

                    userRef.child(user.getUid()).setValue(new User(user));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void detachAuthStateListener() {
        if (mAuthStateListener != null)
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        mAuthStateListener = null;
    }

}
