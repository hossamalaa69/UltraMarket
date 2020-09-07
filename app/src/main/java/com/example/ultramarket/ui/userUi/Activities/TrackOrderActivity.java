package com.example.ultramarket.ui.userUi.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.Order;
import com.example.ultramarket.firebase.FirebaseAuthHelper;
import com.example.ultramarket.helpers.AppExecutors;
import com.example.ultramarket.helpers.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TrackOrderActivity extends AppCompatActivity {

    @BindView(R.id.user_track_order_activity_link2)
    View confirmedLink;
    @BindView(R.id.user_track_order_activity_link3)
    View readyLink;
    @BindView(R.id.user_track_order_activity_link4)
    View deliveredLink;
    @BindView(R.id.user_track_order_activity_spot2)
    View confirmedSpot;
    @BindView(R.id.user_track_order_activity_spot3)
    View readySpot;
    @BindView(R.id.user_track_order_activity_spot4)
    View onWaySpot;
    @BindView(R.id.user_track_order_activity_spot5)
    View deliveredSpot;
    @BindView(R.id.user_track_order_activity_remaining_time)
    TextView remainingTime;
    @BindView(R.id.user_track_order_activity_order_id)
    TextView orderId;
    @BindView(R.id.user_track_order_activity_cancel_btn)
    Button cancelOrderBtn;
    Handler timeHandler = new Handler();
    Runnable runnable;
    int status;
    String orderIDStr;

    @OnClick(R.id.user_track_order_activity_cancel_btn)
    public void onCancelBtnClicked(View view) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.cancel_order)
                .setMessage(R.string.are_you_sure_cancel_order)
                .setPositiveButton(R.string.cancel_order, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cancelOrder();
                    }
                }).setNegativeButton(R.string.cancel, null).create();
        dialog.show();
    }

    private void cancelOrder() {
        if (status < Order.STATUS_ON_WAY) {
            AppExecutors.getInstance().networkIO().execute(new Runnable() {
                @Override
                public void run() {
                    FirebaseDatabase.getInstance().getReference()
                            .child(Order.class.getSimpleName()).child(FirebaseAuthHelper.getsInstance().getCurrUser().getUid())
                            .child(orderIDStr).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            Utils.createToast(TrackOrderActivity.this, R.string.order_canceled, Toast.LENGTH_SHORT);
                            startActivity(new Intent(TrackOrderActivity.this, HomeActivity.class));
                            finish();
                        }
                    });
                }
            });
        } else {
            Utils.createToast(this, R.string.order_is_on_way, Toast.LENGTH_SHORT);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_track_order);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.track_order);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        orderIDStr = intent.getStringExtra("order_id");
        loadOrder(orderIDStr);
    }

    private void loadOrder(String orderId) {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase.getInstance().getReference()
                        .child(Order.class.getSimpleName()).child(FirebaseAuthHelper.getsInstance().getCurrUser().getUid())
                        .child(orderId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                updateUI(snapshot.getValue(Order.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

            }
        });
    }

    private void updateUI(Order order) {
        status = order.getStatus();
        if (status < Order.STATUS_DELIVERED)
            startTimeThread(order.getReceiving_date());
        else
            remainingTime.setText(R.string.delivered);
        orderId.setText(order.getID());
        updateStatus(order.getStatus());

    }

    private void updateStatus(int status) {
        cancelOrderBtn.setEnabled(true);
        if (status >= Order.STATUS_CONFIRMED) {
            confirmedLink.setBackgroundColor(ContextCompat.getColor(this, R.color.purple1));
            confirmedSpot.setBackgroundColor(ContextCompat.getColor(this, R.color.purple1));
        }
        if (status >= Order.STATUS_READY) {
            readyLink.setBackgroundColor(ContextCompat.getColor(this, R.color.purple1));
            readySpot.setBackgroundColor(ContextCompat.getColor(this, R.color.purple1));
        }
        if (status >= Order.STATUS_ON_WAY) {
            onWaySpot.setBackgroundColor(ContextCompat.getColor(this, R.color.purple1));
        }
        if (status >= Order.STATUS_DELIVERED) {
            deliveredLink.setBackgroundColor(ContextCompat.getColor(this, R.color.purple1));
            deliveredSpot.setBackgroundColor(ContextCompat.getColor(this, R.color.purple1));
            cancelOrderBtn.setEnabled(false);
        }
    }

    private void startTimeThread(long time) {
        runnable = new Runnable() {
            @Override
            public void run() {
                CharSequence date = DateUtils
                        .getRelativeTimeSpanString(time,
                                Calendar.getInstance().getTimeInMillis(),
                                0);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        remainingTime.setText(date);
                    }
                });
                timeHandler.postDelayed(runnable, 1000 * 60);
            }
        };
        timeHandler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        if (runnable != null)
            timeHandler.removeCallbacks(runnable);
        super.onDestroy();
    }
}