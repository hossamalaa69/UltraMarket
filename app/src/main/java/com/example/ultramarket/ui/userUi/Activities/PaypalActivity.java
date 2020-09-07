package com.example.ultramarket.ui.userUi.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.Order;
import com.example.ultramarket.database.Entities.Product;
import com.example.ultramarket.firebase.FirebaseAuthHelper;
import com.example.ultramarket.helpers.AppExecutors;
import com.example.ultramarket.helpers.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaypalActivity extends AppCompatActivity {
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
            .clientId("AYhCsHV0USaC3_KpU_BEwfGKYVrH6tSW2pcMQW7xXizxm3w914cmn4FbbAlHzdt8iZLMCIzlEJpcr9lt");
    @BindView(R.id.user_paypal_details)
    TextView response;
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal);
        ButterKnife.bind(this);
        //start service
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
        Intent orderIntent = getIntent();
        order = (Order) orderIntent.getSerializableExtra("order");
        String currency = orderIntent.getStringExtra("currency");
        String products = orderIntent.getStringExtra("products");
        payMoney(String.valueOf(order.getPrice()), currency, products);
    }

    private void payMoney(String totalPrice, String currency, String products) {
        // PAYMENT_INTENT_SALE will cause the payment to complete immediately.
        // Change PAYMENT_INTENT_SALE to
        //   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
        //   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
        //     later via calls from your server.
        PayPalPayment payment = new PayPalPayment(new BigDecimal(totalPrice), currency.matches("$") ? "USD" : "USD", products, PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Log.i("paymentExample", confirm.toJSONObject().toString(4));

                    String details = confirm.toJSONObject().getJSONObject("response")
                            .getString("state");

                    response.setText(details);
                    // TODO: send 'confirm' to your server for verification.
                    // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                    // for more details.
                    sendOrder();

                } catch (JSONException e) {
                    Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            Log.i("paymentExample", "The user canceled.");
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }
        finish();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendOrder() {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                .child(Order.class.getSimpleName()).child(FirebaseAuthHelper.getsInstance().getCurrUser().getUid())
                .push();
        order.setID(orderRef.getKey());
        order.setStatus(Order.STATUS_CONFIRMED);
        order.setOrder_date(Calendar.getInstance().getTimeInMillis());
        orderRef.setValue(order).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                deleteFromProducts(order.getProducts());
                removeCart();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                            Intent intent = new Intent(PaypalActivity.this, TrackOrderActivity.class);
                            intent.putExtra("order_id", order.getID());
                            startActivity(intent);
                            PaypalActivity.this.finish();

                    }
                });
            }
        });
    }

    private void removeCart() {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase.getInstance().getReference()
                        .child("Cart")
                        .child(FirebaseAuthHelper.getsInstance().getCurrUser().getUid())
                        .removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            }
                        });
            }
        });
    }

    private void deleteFromProducts(Map<String, Integer> products) {
        for (Map.Entry<String, Integer> product : products.entrySet()) {
            AppExecutors.getInstance().networkIO().execute(new Runnable() {
                @Override
                public void run() {
                    DatabaseReference prodRef = FirebaseDatabase.getInstance().getReference()
                            .child(Product.class.getSimpleName()).child(product.getKey()).child("count");
                    prodRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            prodRef.setValue(snapshot.getValue(Integer.class) - product.getValue());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
        }
        Utils.createToast(this, R.string.order_sent, Toast.LENGTH_SHORT);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

}