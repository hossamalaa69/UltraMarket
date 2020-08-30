package com.example.ultramarket.ui.userUi.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.ultramarket.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutUsActivity extends AppCompatActivity {

    @BindView(R.id.user_about_us_tv)
    TextView about_us_message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_about_us);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        ActionBar actionBar = getSupportActionBar();
        if("about_us".matches(type)){
            if(actionBar!=null){
                actionBar.setTitle(R.string.about_us);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            about_us_message.setText(R.string.about_us_messaage);
        }else if("terms".matches(type)){
            if(actionBar!=null){
                actionBar.setTitle(R.string.terms);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            about_us_message.setText(R.string.terms_messaage);
        }
    }

}