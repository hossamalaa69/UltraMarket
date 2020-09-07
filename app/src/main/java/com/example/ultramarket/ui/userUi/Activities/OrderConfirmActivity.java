package com.example.ultramarket.ui.userUi.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.Order;
import com.example.ultramarket.firebase.FirebaseAuthHelper;
import com.example.ultramarket.helpers.Utils;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderConfirmActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Order order;
    private Calendar calendar;
    private ArrayList<String> productNames;
    private String currency;

    @BindView(R.id.user_order_confirm_btn)
    Button confirmBtn;
    @BindView(R.id.user_order_confirm_order_details)
    TextView orderDetails;

    @OnClick(R.id.user_order_confirm_choose_date)
    public void onChooseTimeClicked(View view) {
        openDateBicker();
    }

    @OnClick(R.id.user_order_confirm_btn)
    public void onConfirmOrderClicked(View view) {
        if (order.getReceiving_date() < Calendar.getInstance().getTimeInMillis()) {
            Toast.makeText(this, R.string.invalid_date, Toast.LENGTH_SHORT).show();
            return;
        }
        confirmBtn.setEnabled(false);
        if (order.getID() != null) {
            Utils.createToast(this, R.string.already_ordered, Toast.LENGTH_SHORT);
            return;
        }
        if (FirebaseAuthHelper.getsInstance().getCurrUser() != null) {

            //start paypal activity
            Intent intent = new Intent(OrderConfirmActivity.this, PaypalActivity.class);
            intent.putExtra("order", order);
            intent.putExtra("currency", currency);
            intent.putExtra("products", getProductsDetails());
            startActivity(intent);
            OrderConfirmActivity.this.finish();
        } else {
            Utils.createToast(this, R.string.you_must_signin_first, Toast.LENGTH_SHORT);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateOrderDetails();
    }

    private void updateOrderDetails() {
        String productsDetails = getProductsDetails();
        FirebaseUser user = FirebaseAuthHelper.getsInstance().getCurrUser();
        orderDetails.setText(OrderConfirmActivity.this.getString(
                R.string.confirm_order_details,
                order.getID() == null ? getString(R.string.not_ordered_yet) : order.getID(),
                user == null ? getString(R.string.not_logged_in) : user.getDisplayName(),
                order.getPrice(),
                productsDetails,
                order.getReceiving_date() > 0 ?
                        new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss")
                                .format(new Date(order.getReceiving_date())) : "Not selected yet",
                order.getOrder_date() > 0 ?
                        new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss")
                                .format(new Date(order.getOrder_date())) : "Not ordered yet"));
    }

    private String getProductsDetails() {
        String productsDetails = "";
        int i = 0;
        for (Map.Entry<String, Integer> entry : order.getProducts().entrySet()) {
            productsDetails = productsDetails.concat("(" + entry.getValue() + ") ").concat(productNames.get(i) + "\n");
            i++;
        }
        return productsDetails;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        openTimePicker();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        order.setReceiving_date(calendar.getTime().getTime());
        updateOrderDetails();
        confirmBtn.setEnabled(true);
    }

    private void openDateBicker() {
        DatePickerDialog dateDialog = new DatePickerDialog(this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        dateDialog.show();
    }

    private void openTimePicker() {
        TimePickerDialog timeDialog = new TimePickerDialog(this, this,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE), false);
        timeDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_order_confirm);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.confirm_order);
        }
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        productNames = intent.getStringArrayListExtra("products");
        currency = intent.getStringExtra("currency");
        if (bundle != null) {
            order = (Order) bundle.getSerializable("order");
        }
        openDateBicker();
        updateOrderDetails();
    }
}