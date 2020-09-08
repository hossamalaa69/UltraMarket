package com.example.ultramarket.firebase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.Location;
import com.example.ultramarket.database.Entities.User;
import com.example.ultramarket.helpers.AppExecutors;
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
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase.getInstance().getReference()
                        .child(User.class.getSimpleName()).child(user.getID())
                        .setValue(user).addOnSuccessListener(listener);
            }
        });

    }

    public void isAdmin(String id, Context context) {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase.getInstance().getReference().child("admins").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot user : snapshot.getChildren()) {
                            if (id.matches(user.getKey())) {
                                firebaseAuthCallBacks = (FirebaseAuthCallBacks) context;
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
        });

    }

    public void logOut(Context context, OnSuccessListener<Void> listener) {
        if (listener != null)
            AuthUI.getInstance().signOut(context).addOnSuccessListener(listener);
        else
            AuthUI.getInstance().signOut(context);
    }

    public void createUserWithEmailAndPassword(User user, String password, FirebaseAuthCallBacks firebaseAuthCallBacks) {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                mFirebaseAuth.createUserWithEmailAndPassword(user.getEmail(), password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (mFirebaseAuth.getCurrentUser() != null) {
                                user.setID(mFirebaseAuth.getCurrentUser().getUid());
                                insertUser(user, password, null);
                            }
                        } else {
                            firebaseAuthCallBacks.onFailedToSignUp();
                        }
                    }
                });
            }
        });
    }

    private void signInUserWithEmailAndPassword(User user, String password, FirebaseAuthCallBacks firebaseAuthCallBacks) {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                mFirebaseAuth.signInWithEmailAndPassword(user.getEmail(), password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                        }
                    }
                });
            }
        });

    }

    public void updateLocation(Location location, String id, OnSuccessListener<Void> listener) {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase.getInstance().getReference()
                        .child(User.class.getSimpleName()).child(id).child(Location.class.getSimpleName().toLowerCase())
                        .setValue(location).addOnSuccessListener(listener);
            }
        });
    }

    public void addUserImageUri(String imageUri, OnSuccessListener<Void> listener) {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase.getInstance().getReference().child(User.class.getSimpleName())
                        .child(mFirebaseAuth.getCurrentUser().getUid())
                        .child("imageUrl").setValue(imageUri).addOnSuccessListener(listener);
            }
        });
    }

    public void addRating(String uid, int rate, OnSuccessListener<Void> listener) {
        FirebaseDatabase.getInstance().getReference()
                .child(User.class.getSimpleName()).child(uid).child("rate")
                .setValue(rate);
    }


    public interface FirebaseAuthCallBacks {
        void onLoginStateChanges(User user);

        void onCheckAdminResult(boolean isAdmin);

        void onLoggedOutStateChanges();

        void onFailedToSignUp();
    }

    public FirebaseAuthHelper() {
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public void attachAuthStateListener(Context context) {
        firebaseAuthCallBacks = (FirebaseAuthCallBacks) context;
        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // loggedIn
                firebaseAuthCallBacks.onLoginStateChanges(new User(user));

            } else {
                firebaseAuthCallBacks.onLoggedOutStateChanges();
                // logged out
            }
        };
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    public FirebaseUser getCurrUser() {
        return mFirebaseAuth.getCurrentUser();
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

    public void insertUser(User user, String password, FirebaseAuthCallBacks listener) {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child(User.class.getSimpleName());
                userRef.child(user.getID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            userRef.child(user.getID()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        signInUserWithEmailAndPassword(user, password, firebaseAuthCallBacks);
                                    } else {
                                        firebaseAuthCallBacks.onFailedToSignUp();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    public void detachAuthStateListener() {
        if (mAuthStateListener != null)
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        mAuthStateListener = null;
    }

}
