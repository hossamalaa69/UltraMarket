package com.example.ultramarket.ui.userUi.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.Order;
import com.example.ultramarket.firebase.FirebaseAuthHelper;
import com.example.ultramarket.helpers.AppExecutors;
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

    @BindView(R.id.user_track_order_activity_link1)
    View receivedLink;
    @BindView(R.id.user_track_order_activity_link2)
    View confirmedLink;
    @BindView(R.id.user_track_order_activity_link3)
    View readyLink;
    @BindView(R.id.user_track_order_activity_spot1)
    View receivedSpot;
    @BindView(R.id.user_track_order_activity_spot2)
    View confirmedSpot;
    @BindView(R.id.user_track_order_activity_spot3)
    View readySpot;
    @BindView(R.id.user_track_order_activity_spot4)
    View onWaySpot;
    @BindView(R.id.user_track_order_activity_remaining_time)
    TextView remainingTime;
    @BindView(R.id.user_track_order_activity_order_id)
    TextView orderId;
    @BindView(R.id.user_track_order_activity_cancel_btn)
    Button cancelOrderBtn;
    Thread timeTread;
    int status;
    String orderIDStr;

    @OnClick(R.id.user_track_order_activity_cancel_btn)
    public void onCancelBtnClicked(View view) {
        if(status<Order.STATUS_ON_WAY) {
            AppExecutors.getInstance().networkIO().execute(new Runnable() {
                @Override
                public void run() {
                    FirebaseDatabase.getInstance().getReference()
                            .child(Order.class.getSimpleName()).child(FirebaseAuthHelper.getsInstance().getCurrUser().getUid())
                            .child(orderIDStr).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            Toast.makeText(TrackOrderActivity.this,R.string.order_canceled,Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(TrackOrderActivity.this,HomeActivity.class));
                            finish();
                        }
                    });
                }
            });
        }
        //TODO remove order
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_track_order);
        ButterKnife.bind(this);
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
        startTimeThread(order.getReceiving_date());
        orderId.setText(order.getID());
        updateStatus(order.getStatus());

    }

    private void updateStatus(int status) {
        if (status > Order.STATUS_RECEIVED) {
            receivedLink.setBackgroundColor(ContextCompat.getColor(this, R.color.purple1));
            receivedSpot.setBackgroundColor(ContextCompat.getColor(this, R.color.purple1));
        }
        if (status > Order.STATUS_CONFIRMED) {
            confirmedLink.setBackgroundColor(ContextCompat.getColor(this, R.color.purple1));
            confirmedSpot.setBackgroundColor(ContextCompat.getColor(this, R.color.purple1));
        }
        if (status > Order.STATUS_READY) {
            readyLink.setBackgroundColor(ContextCompat.getColor(this, R.color.purple1));
            readyLink.setBackgroundColor(ContextCompat.getColor(this, R.color.purple1));
        }
        if (status > Order.STATUS_ON_WAY) {
            onWaySpot.setBackgroundColor(ContextCompat.getColor(this, R.color.purple1));
        }
    }

    private void startTimeThread(long time) {
        timeTread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
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
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        timeTread.start();
    }

    @Override
    protected void onDestroy() {
        timeTread.interrupt();
        super.onDestroy();
    }
}