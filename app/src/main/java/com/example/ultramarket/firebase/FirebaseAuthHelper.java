package com.example.ultramarket.firebase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.User;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

    public void logOut(Context context, OnSuccessListener<Void> listener) {
        AuthUI.getInstance().signOut(context).addOnSuccessListener(listener);
    }

    public void createUserWithEmailAndPassword(User user, String password,FirebaseAuthCallBacks firebaseAuthCallBacks) {
        mFirebaseAuth
                .createUserWithEmailAndPassword(user.getEmail(), password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    signInUserWithEmailAndPassword(user,password,firebaseAuthCallBacks);
                }
            }
        });

    }

    private void signInUserWithEmailAndPassword(User user, String password,FirebaseAuthCallBacks firebaseAuthCallBacks) {
        mFirebaseAuth.signInWithEmailAndPassword(user.getEmail(),password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    insertUser(Objects.requireNonNull(mFirebaseAuth.getCurrentUser()));
                    firebaseAuthCallBacks.onSignedInSuccessfully(user);
                }
            }
        });
    }

    public interface FirebaseAuthCallBacks {
        void onLoginStateChanges(User user);

        void onCheckAdminResult(boolean isAdmin);

        void onLoggedOutStateChanges();

        void onSignedInSuccessfully(User user);
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
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.login_layout) //your layout name
                .setEmailButtonId(R.id.email_button)
                .setGoogleButtonId(R.id.google_button)
                .build();

        // Create and launch sign-in intent
        AuthUI.SignInIntentBuilder builder = AuthUI.getInstance().createSignInIntentBuilder();

        activity.startActivityForResult(
                builder.setAuthMethodPickerLayout(customLayout)
                        .setIsSmartLockEnabled(false)
                        .setLogo(R.drawable.logo)
                        .setAvailableProviders(providers)
                        .setTheme(R.style.AppTheme)
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
