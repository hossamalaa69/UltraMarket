package com.example.ultramarket.ui.userUi.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.User;
import com.example.ultramarket.firebase.FirebaseAuthHelper;
import com.example.ultramarket.firebase.firebase_storage.FirebaseStorageHelper;
import com.example.ultramarket.helpers.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    @BindView(R.id.user_profile_user_name)
    TextView mUserName;
    @BindView(R.id.user_profile_country)
    TextView mUserCountry;
    @BindView(R.id.user_profile_city)
    TextView mUserCity;
    @BindView(R.id.user_profile_road)
    TextView mUserRoad;
    @BindView(R.id.user_profile_floor)
    TextView mUserFloor;
    @BindView(R.id.user_profile_email)
    TextView mUserEmail;
    @BindView(R.id.user_profile_phone)
    TextView mUserPhone;
    @BindView(R.id.user_profile_img)
    CircleImageView mUserImg;
    private TextView mEmail;
    private TextView mPhone;
    private EditText mCity;
    private EditText mRoad;
    private EditText mFloor;
    private Spinner mCountrySpinner;
    private ArrayAdapter<String> adapter;
    @BindView(R.id.user_profile_progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.user_profile_iamge_progress_bar)
    ProgressBar mImageProgressBar;
    private User currUser;

    @OnClick(R.id.location_view)
    public void onLocationEditClicked(View view) {
        showLocationDialog();
    }

    @OnClick(R.id.personal_info)
    public void onPersonalInfoEditClicked(View view) {
        showPersonalInfoDialog();
    }

    @OnClick(R.id.user_profile_logout)
    public void onLogOutPressed(View view) {
        showProgressBar();
        FirebaseAuthHelper.getsInstance().logOut(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                hideProgressBar();
                startActivity(new Intent(UserProfile.this, HomeActivity.class));
            }
        });
    }

    @OnClick(R.id.user_profile_upload_img)
    public void onUploadImageClicked(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/jpg");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_user_profile);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.profile);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setLogo(R.mipmap.ic_launcher_foreground);
        }
        hideProgressBar();
        mImageProgressBar.setVisibility(View.GONE);
        FirebaseDatabase.getInstance().getReference().
                child(User.class.getSimpleName()).child(FirebaseAuthHelper.getsInstance().getCurrUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        currUser = snapshot.getValue(User.class);
                        updateProfile();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri selectedImage = data.getData();
                    mImageProgressBar.setVisibility(View.VISIBLE);
                    uploadImageToFirebaseStorage(selectedImage);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        FirebaseStorageHelper.getsInstance().uploadImage(imageUri, new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Uri downloadUri = task.getResult();
                if (downloadUri != null)
                    FirebaseAuthHelper.getsInstance().addUserImageUri(downloadUri.toString(), new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Picasso.get().load(currUser.getImageUrl()).resize(300,300).into(mUserImg);
                            mImageProgressBar.setVisibility(View.GONE);
                        }
                    });
            }
        });
    }

    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    private void showPersonalInfoDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.personal_info_dialog, null);
        mEmail = view.findViewById(R.id.user_personal_dialog_email);
        mPhone = view.findViewById(R.id.user_personal_dialog_phone);
        dialog.setView(view);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE,
                getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updatePersonalData();
                    }
                });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.createToast(UserProfile.this, R.string.data_ignored, Toast.LENGTH_SHORT);
                    }
                });
        updatePersonalDialog();
        dialog.show();
    }

    private void updatePersonalData() {
        String email = mEmail.getText().toString().trim();
        String phone = mPhone.getText().toString().trim();
        if (checkIsValidMail(email) && checkIsValidPhone(phone)) {
            currUser.setEmail(email);
            currUser.setPhone(phone);
            updateUserFirebaseData();
            updateProfile();
        } else {
            Utils.createToast(this, R.string.invalid_mail_or_phone, Toast.LENGTH_SHORT);
        }
    }

    private boolean checkIsValidPhone(String phone) {
        return Patterns.PHONE.matcher(phone).matches() && phone.length() > 6 && phone.length() < 14;
    }

    private boolean checkIsValidMail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void updatePersonalDialog() {
        mEmail.setText(currUser.getEmail());
        mPhone.setText(currUser.getPhone());

    }

    private void showLocationDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.location_dialog, null);
        mCity = view.findViewById(R.id.user_location_dialog_city);
        mRoad = view.findViewById(R.id.user_location_dialog_road);
        mFloor = view.findViewById(R.id.user_location_dialog_floor);
        mCountrySpinner = view.findViewById(R.id.user_location_dialog_country_spinner);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, Utils.availableCountries);
        mCountrySpinner.setAdapter(adapter);
        mCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0)
                    currUser.getLocation().setCountry_name(adapterView.getItemAtPosition(i).toString());
                else
                    Utils.createToast(UserProfile.this, R.string.select_country, Toast.LENGTH_LONG);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        dialog.setView(view);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE,
                getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateLocationData();
                    }
                });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.createToast(UserProfile.this, R.string.data_ignored, Toast.LENGTH_SHORT);
                    }
                });
        updateLocationDialog();
        dialog.show();
    }

    private void updateLocationDialog() {
        mFloor.setText(String.valueOf(currUser.getFloor()));
        mCity.setText(currUser.getLocation().getCity_name());
        mRoad.setText(currUser.getLocation().getRoad_name());
        if (adapter.getPosition(currUser.getLocation().getCountry_name() != null ? currUser.getLocation().getCountry_name() : "") > -1)
            mCountrySpinner.setSelection(
                    adapter.getPosition(currUser.getLocation().getCountry_name() != null ? currUser.getLocation().getCountry_name() : ""));

    }

    private OnSuccessListener<Void> onUpdateUserListener = new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            Utils.createToast(UserProfile.this, R.string.data_saved, Toast.LENGTH_SHORT);
        }
    };

    private void updateUserFirebaseData() {
        FirebaseAuthHelper.getsInstance().updateUserData(currUser, onUpdateUserListener);
    }

    private void updateLocationData() {
        String road = mRoad.getText().toString().trim();
        String city = mCity.getText().toString().trim();
        int floor = Integer.parseInt(mFloor.getText().toString().trim());
        if (road.matches("") || city.matches("") || floor < 0) {
            Utils.createToast(this, R.string.fields_cant_be_empty, Toast.LENGTH_SHORT);
            return;
        }
        currUser.getLocation().setRoad_name(road);
        currUser.getLocation().setCity_name(city);
        currUser.setFloor(floor);
        FirebaseAuthHelper.getsInstance().updateUserData(currUser, onUpdateUserListener);
        updateProfile();
    }


    private void updateProfile() {
        if (currUser != null) {
            mUserCity.setText(getString(R.string.city, currUser.getLocation().getCity_name()));
            mUserFloor.setText(getString(R.string.floor, String.valueOf(currUser.getFloor())));
            mUserEmail.setText(getString(R.string.email_container, currUser.getEmail()));
            mUserPhone.setText(getString(R.string.phone_container, currUser.getPhone()));
            mUserRoad.setText(getString(R.string.road, currUser.getLocation().getRoad_name()));
            mUserName.setText(currUser.getName());
            mUserCountry.setText(getString(R.string.country, currUser.getLocation().getCountry_name()));
            Picasso.get().load(currUser.getImageUrl()).resize(300,300).into(mUserImg);
        }
    }
}