package com.example.ultramarket.firebase;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.User;
import com.example.ultramarket.helpers.Utils;
import com.example.ultramarket.ui.userUi.Activities.HomeActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class FirebaseAuthHelper {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static FirebaseAuthHelper sInstance;
    public static FirebaseAuthHelper getsInstance(){
        if(sInstance==null){
            sInstance = new FirebaseAuthHelper();
        }
        return sInstance;
    }

    private static FirebaseAuthCallBacks firebaseAuthCallBacks;
    public interface FirebaseAuthCallBacks{
        void onLoginStateChanges(FirebaseUser user);

        void onLoggedOutStateChanges();
    }

    public FirebaseAuthHelper() {
        mFirebaseAuth = FirebaseAuth.getInstance();
    }
    public void attachAuthStateListener(Context context){
        firebaseAuthCallBacks = (FirebaseAuthCallBacks) context;
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // loggedIn
                    firebaseAuthCallBacks.onLoginStateChanges(user);
                } else {
                    firebaseAuthCallBacks.onLoggedOutStateChanges();

                    // logged out
                }
            }
        };
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    public void detachAuthStateListener() {
        if (mAuthStateListener != null)
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        mAuthStateListener = null;
    }

}
