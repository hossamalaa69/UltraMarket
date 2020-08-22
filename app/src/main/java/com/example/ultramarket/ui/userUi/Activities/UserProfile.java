package com.example.ultramarket.ui.userUi.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.User;
import com.example.ultramarket.firebase.FirebaseAuthHelper;
import com.example.ultramarket.helpers.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

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

    @OnClick(R.id.location_view)
    public void onLocationEditClicked(View view) {
        showLocationDialog();
    }

    @OnClick(R.id.personal_info)
    public void onPersonalInfoEditClicked(View view) {
        showPersonalInfoDialog();
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
                        Toast.makeText(UserProfile.this, R.string.data_ignored, Toast.LENGTH_SHORT).show();
                    }
                });
        updatePersonalDialog();
        dialog.show();
    }

    private void updatePersonalData() {
        String email = mEmail.getText().toString().trim();
        String phone = mPhone.getText().toString().trim();
        if (checkIsValidMail(email) && checkIsValidPhone(phone)) {
            Utils.user.setEmail(email);
            Utils.user.setPhone(phone);
            updateUserFirebaseData();
            updateProfile();
        } else {
            Toast.makeText(this, R.string.invalid_mail_or_phone, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkIsValidPhone(String phone) {
        return Patterns.PHONE.matcher(phone).matches()&&phone.length()>6&&phone.length()<13;
    }

    private boolean checkIsValidMail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void updatePersonalDialog() {
        mEmail.setText(Utils.user.getEmail());
        mPhone.setText(Utils.user.getPhone());

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
                    Utils.user.setCountry(adapterView.getItemAtPosition(i).toString());
                else
                    Toast.makeText(UserProfile.this, "you should select your country", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(UserProfile.this, R.string.data_ignored, Toast.LENGTH_SHORT).show();
                    }
                });
        updateLocationDialog();
        dialog.show();
    }

    private void updateLocationDialog() {
        mFloor.setText(String.valueOf(Utils.user.getFloor()));
        mCity.setText(Utils.user.getCity());
        mRoad.setText(Utils.user.getRoad());
        if (adapter.getPosition(Utils.user.getCountry() != null ? Utils.user.getCountry() : "") > -1)
            mCountrySpinner.setSelection(
                    adapter.getPosition(Utils.user.getCountry() != null ? Utils.user.getCountry() : ""));

    }

    private OnSuccessListener<Void> onUpdateUserListener = new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            Toast.makeText(UserProfile.this, R.string.data_saved, Toast.LENGTH_SHORT).show();
        }
    };

    private void updateUserFirebaseData() {
        FirebaseAuthHelper.getsInstance().updateUserData(Utils.user, onUpdateUserListener);
    }

    private void updateLocationData() {
        String road = mRoad.getText().toString().trim();
        String city = mCity.getText().toString().trim();
        int floor = Integer.parseInt(mFloor.getText().toString().trim());
        if (road.matches("") || city.matches("") || floor < 0) {
            Toast.makeText(this, R.string.fields_cant_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        Utils.user.setRoad(road);
        Utils.user.setCity(city);
        Utils.user.setFloor(floor);
        FirebaseAuthHelper.getsInstance().updateUserData(Utils.user, onUpdateUserListener);
        updateProfile();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.profile);
            actionBar.setLogo(R.mipmap.ic_launcher_foreground);
        }
        FirebaseDatabase.getInstance().getReference().child(User.class.getSimpleName()).child(Utils.user.getID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Utils.user = snapshot.getValue(User.class);
                        updateProfile();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void updateProfile() {

        if (Utils.user != null) {
            mUserCity.setText(getString(R.string.city, Utils.user.getCity()));
            mUserFloor.setText(getString(R.string.floor, String.valueOf(Utils.user.getFloor())));
            mUserEmail.setText(getString(R.string.email_container, Utils.user.getEmail()));
            mUserPhone.setText(getString(R.string.phone_container, Utils.user.getPhone()));
            mUserRoad.setText(getString(R.string.road, Utils.user.getRoad()));
            mUserName.setText(Utils.user.getName());
            mUserCountry.setText(getString(R.string.country, Utils.user.getCountry()));
            Picasso.get().load(Utils.user.getImageUrl()).into(mUserImg);
        }
    }
}