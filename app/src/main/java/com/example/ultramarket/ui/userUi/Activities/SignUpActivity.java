package com.example.ultramarket.ui.userUi.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.User;
import com.example.ultramarket.firebase.FirebaseAuthHelper;
import com.example.ultramarket.helpers.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {

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
    EditText mCountry;

    @OnClick(R.id.user_sign_up_create_email_btn)
    public void onCreateEmailClicked(View View) {
        // TODO create Email
        if (isValidInputs()) {
            User user = getUserDataFromUi();
            String password = mPass.getText().toString().trim();
            FirebaseAuthHelper.getsInstance().createUserWithEmailAndPassword(user, password, new FirebaseAuthHelper.FirebaseAuthCallBacks() {
                @Override
                public void onLoginStateChanges(User user) {

                }

                @Override
                public void onCheckAdminResult(boolean isAdmin) {

                }

                @Override
                public void onLoggedOutStateChanges() {

                }

                @Override
                public void onSignedInSuccessfully(User user) {
                    Utils.user = user;
                    startActivity(new Intent(SignUpActivity.this,HomeActivity.class));
                }
            });
        } else {
            Toast.makeText(this,R.string.invalid_data,Toast.LENGTH_SHORT).show();
        }
    }

    private User getUserDataFromUi() {
        User user = new User();
        user.setPhone(mPhone.getText().toString());
        user.setEmail(mEmail.getText().toString());
        user.setName(mUserName.getText().toString());
        user.setCountry(mCountry.getText().toString());
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
                && mConfirmPass.getText().toString().matches(mPass.getText().toString());

    }

    @OnClick(R.id.user_sign_up_create_sign_in_btn)
    public void onSignInClicked(View view) {
        signInWithEmailOrGoogle();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
    }

    private void signInWithEmailOrGoogle() {
        FirebaseAuthHelper.getsInstance().loginToFirebase(this);
    }
}