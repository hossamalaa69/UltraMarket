package com.example.ultramarket.ui.userUi.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.User;
import com.example.ultramarket.firebase.FirebaseAuthHelper;
import com.example.ultramarket.helpers.Utils;
import com.example.ultramarket.ui.adminLayer.AdminHomeActivity;
import com.google.firebase.auth.UserProfileChangeRequest;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity implements FirebaseAuthHelper.FirebaseAuthCallBacks {

    @BindView(R.id.user_sign_up_confirm_password)
    EditText mConfirmPass;
    @BindView(R.id.user_sign_up_password)
    EditText mPass;
    @BindView(R.id.user_sign_up_email)
    EditText mEmail;
    @BindView(R.id.user_sign_up_phone)
    EditText mPhone;
    @BindView(R.id.user_sign_up_user_name)
    EditText mUserName;
    @BindView(R.id.user_sign_up_country)
    Spinner mCountrySpinner;
    @BindView(R.id.user_sign_up_progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.user_sign_up_show_hide_pass)
    ImageButton mShowHidePass;
    private static boolean isNewEmail = true;


    @OnClick(R.id.user_sign_up_show_hide_pass)
    public void showHidePass(View view) {
        if (mShowHidePass.isSelected()) {
            //show pass and set selectedd
            mShowHidePass.setImageResource(R.drawable.visibility_off_24);
            mPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mShowHidePass.setSelected(false);
        } else {
            // hide pass and set on selected
            mShowHidePass.setImageResource(R.drawable.remove_red_eye_24);
            mPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mShowHidePass.setSelected(true);
        }
    }

    @OnClick(R.id.user_sign_up_create_email_btn)
    public void onCreateEmailClicked(View View) {
        // TODO create Email
        if (isValidInputs()) {
            isNewEmail = true;
            mProgressBar.setVisibility(android.view.View.VISIBLE);
            User user = getUserDataFromUi();
            String password = mPass.getText().toString().trim();
            FirebaseAuthHelper.getsInstance().createUserWithEmailAndPassword(user, password, this);
        } else {
            Utils.createToast(this, R.string.invalid_data, Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onFailedToSignUp() {
        Utils.createToast(this, R.string.failed_to_sign_up, Toast.LENGTH_LONG);
        FirebaseAuthHelper.getsInstance().logOut(this, null);
        mProgressBar.setVisibility(android.view.View.GONE);

    }

    @Override
    public void onLoginStateChanges(User user) {
        User insertedUser;
        if (isNewEmail) {
            insertedUser = getUserDataFromUi();
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(insertedUser.getName()).build();
            FirebaseAuthHelper.getsInstance().getCurrUser().updateProfile(profileChangeRequest);
        } else
            insertedUser = user;
        insertedUser.setID(user.getID());
      //  FirebaseAuthHelper.getsInstance().insertUser(insertedUser);
        FirebaseAuthHelper.getsInstance().isAdmin(insertedUser.getID(), SignUpActivity.this);

    }

    @Override
    public void onCheckAdminResult(boolean isAdmin) {
        mProgressBar.setVisibility(View.GONE);
        if (isAdmin) {
            startActivity(new Intent(SignUpActivity.this, AdminHomeActivity.class));

        } else {
            startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
        }
    }

    @Override
    public void onLoggedOutStateChanges() {

    }

    private User getUserDataFromUi() {
        User user = new User();
        user.setPhone(mPhone.getText().toString().trim());
        user.setEmail(mEmail.getText().toString().trim());
        user.setName(mUserName.getText().toString().trim());
        user.getLocation().setCountry_name(mCountrySpinner.getSelectedItem().toString().trim());
        return user;
    }

    private boolean isValidInputs() {
        return !mUserName.getText().toString().matches("")
                && !mEmail.getText().toString().matches("")
                && !mPhone.getText().toString().matches("")
                && mPhone.getText().toString().length() < 14
                && mPhone.getText().toString().length() > 6
                && !mPass.getText().toString().matches("")
                && mPass.getText().toString().length() > 7
                && mPass.getText().toString().length() < 16
                && mConfirmPass.getText().toString().matches(mPass.getText().toString())
                && mConfirmPass.getText().toString().matches(mPass.getText().toString())
                && mCountrySpinner.getSelectedItemPosition() > 0;

    }

    @OnClick(R.id.user_sign_up_create_sign_in_btn)
    public void onSignInClicked(View view) {
        signInWithEmailOrGoogle();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_sign_up);
        ButterKnife.bind(this);
        mProgressBar.setVisibility(View.GONE);
        isNewEmail = true;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, Utils.availableCountries);
        mCountrySpinner.setAdapter(adapter);
        mShowHidePass.setSelected(false);
    }

    private void signInWithEmailOrGoogle() {
        isNewEmail = false;
        FirebaseAuthHelper.getsInstance().loginToFirebase(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseAuthHelper.getsInstance().attachAuthStateListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseAuthHelper.getsInstance().detachAuthStateListener();
    }
}