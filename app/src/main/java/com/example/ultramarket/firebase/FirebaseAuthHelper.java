package com.example.ultramarket.firebase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class FirebaseAuthHelper {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static FirebaseAuthHelper sInstance;
    private static final int RC_SIGN_IN = 1;


    public static FirebaseAuthHelper getsInstance() {
        if (sInstance == null) {
            sInstance = new FirebaseAuthHelper();
        }
        return sInstance;
    }

    private static FirebaseAuthCallBacks firebaseAuthCallBacks;

    public void updateUserData(User user, OnSuccessListener<Void> listener) {
        FirebaseDatabase.getInstance().getReference()
                .child(User.class.getSimpleName()).child(user.getID())
                .setValue(user).addOnSuccessListener(listener);
    }

    public void isAdmin(String id, Context context) {
        FirebaseDatabase.getInstance().getReference().child("admins").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot user : snapshot.getChildren()) {
                    if (id.matches(user.getKey())) {
                        firebaseAuthCallBacks.onCheckAdminResult(true);
                        return;
                    }
                }
                firebaseAuthCallBacks.onCheckAdminResult(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface FirebaseAuthCallBacks {
        void onLoginStateChanges(User user);

        void onCheckAdminResult(boolean isAdmin);

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

    public void loginToFirebase(AppCompatActivity activity) {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
        // Create and launch sign-in intent
        activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setLogo(R.drawable.logo)
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
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
