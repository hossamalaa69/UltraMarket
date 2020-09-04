package com.example.ultramarket.ui.adminLayer;

import android.app.VoiceInteractor;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ultramarket.R;
import com.example.ultramarket.adapters.StatsAdapter;
import com.example.ultramarket.firebase.FirebaseAuthHelper;
import com.example.ultramarket.ui.SplashActivity;
import com.example.ultramarket.ui.userUi.Activities.HomeActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminHomeActivity extends AppCompatActivity {

    private LineChart lineChartDownFill;

    private RecyclerView recyclerView;

    private StatsAdapter statsAdapter;

    private ArrayList<Integer> images;

    private ArrayList<String> descriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        unsubscribeFromNotifications();
        
        //initMarketNotification();

        setupRecyclerView();

        initLineChartDownFill();

        //holds listener for clicking the notification item
        statsAdapter.setOnItemClickListener(new StatsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //gets index of item pressed, then send it to its target
                sendToTarget(position);
            }
        });

    }

    private void sendToTarget(int position){

        switch (position) {
            case 0:
                Intent i0 = new Intent(this, DashBoardActivity.class);
                startActivity(i0);
                break;
            case 1:
                Intent i = new Intent(this, WarehouseActivity.class);
                startActivity(i);
                break;
            case 2:
                Intent intent = new Intent(this, CustomersActivity.class);
                startActivity(intent);
                break;
            case 3:
                Intent intent3 = new Intent(this, OrdersActivity.class);
                startActivity(intent3);
                break;

        }
    }

    private void setupRecyclerView() {

        fillData();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        //initializes adapter with the list
        statsAdapter = new StatsAdapter(descriptions, images, this);
        //sets adapter for the recycler view
        recyclerView.setAdapter(statsAdapter);
    }

    private void fillData() {
        images = new ArrayList<>();
        descriptions = new ArrayList<>();

        images.add(R.drawable.ic_dashboard);
        descriptions.add(getString(R.string.dashboard));

        images.add(R.drawable.ic_products);
        descriptions.add(getString(R.string.warehouse));

        images.add(R.drawable.ic_customers);
        descriptions.add(getString(R.string.customers));

        images.add(R.drawable.ic_orders);
        descriptions.add(getString(R.string.orders));

    }

    private void initLineChartDownFill() {

        lineChartDownFill = findViewById(R.id.line_chart);

        lineChartDownFill.getXAxis().setDrawGridLines(false);
        lineChartDownFill.getXAxis().setDrawAxisLine(false);

        lineChartDownFill.getAxisLeft().setDrawGridLines(false);
        lineChartDownFill.getAxisRight().setDrawGridLines(false);
        lineChartDownFill.setDrawBorders(false);
        lineChartDownFill.setTouchEnabled(false);
        lineChartDownFill.setDragEnabled(false);
        lineChartDownFill.setScaleEnabled(false);
        lineChartDownFill.setPinchZoom(false);
        lineChartDownFill.setDrawGridBackground(false);
        lineChartDownFill.setMaxHighlightDistance(200);
        lineChartDownFill.setViewPortOffsets(0, 0, 0, 0);
        lineChartDownFillWithData();
    }

    private void lineChartDownFillWithData() {


        Description description = new Description();
        description.setText(" ");

        lineChartDownFill.setDescription(description);


        float up = 60f;
        float down = 55f;

        ArrayList<Entry> entryArrayList = new ArrayList<>();
        entryArrayList.add(new Entry(0, 60f, "1"));
        entryArrayList.add(new Entry(1, 55f, "2"));
        entryArrayList.add(new Entry(2, 60f, "3"));
        entryArrayList.add(new Entry(3, 40f, "4"));
        entryArrayList.add(new Entry(4, 45f, "5"));
        entryArrayList.add(new Entry(5, 36f, "6"));
        entryArrayList.add(new Entry(6, 40f, "7"));
        entryArrayList.add(new Entry(7, 40f, "8"));
        entryArrayList.add(new Entry(8, 45f, "9"));
        entryArrayList.add(new Entry(9, 60f, "10"));
        entryArrayList.add(new Entry(10, 45f, "10"));
        entryArrayList.add(new Entry(11, 30f, "10"));
        //LineDataSet is the line on the graph
        LineDataSet lineDataSet = new LineDataSet(entryArrayList, "");

        lineDataSet.setLineWidth(0f);
        lineDataSet.setColor(Color.GRAY);
        lineDataSet.setHighLightColor(Color.RED);
        lineDataSet.setDrawValues(false);
        lineDataSet.setCircleRadius(4f);
        lineDataSet.setCircleColor(Color.GRAY);

        //to make the smooth line as the graph is adrapt change so smooth curve
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        //to enable the cubic density : if 1 then it will be sharp curve
        lineDataSet.setCubicIntensity(0.2f);

        //to fill the below of smooth line in graph
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(Color.BLACK);
        //set the transparency
        lineDataSet.setFillAlpha(80);

        //set the gradiant then the above draw fill color will be replace
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.gradient_pink_background);
        lineDataSet.setFillDrawable(drawable);

        //set legend disable or enable to hide {the left down corner name of graph}
        Legend legend = lineChartDownFill.getLegend();
        legend.setEnabled(false);

        //to remove the cricle from the graph
        lineDataSet.setDrawCircles(true);

        //lineDataSet.setColor(ColorTemplate.COLORFUL_COLORS);


        ArrayList<ILineDataSet> iLineDataSetArrayList = new ArrayList<>();
        iLineDataSetArrayList.add(lineDataSet);

        //LineData is the data accord
        LineData lineData = new LineData(iLineDataSetArrayList);
        lineData.setValueTextSize(13f);
        lineData.setValueTextColor(Color.BLACK);

        lineChartDownFill.setData(lineData);
        lineChartDownFill.invalidate();

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }

    public void logout_admin(View view) {
        FirebaseAuthHelper.getsInstance().logOut(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent i = new Intent(AdminHomeActivity.this, SplashActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void initMarketNotification() {
        FirebaseMessaging.getInstance().subscribeToTopic("offers")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed in notifications successfully";
                        if (!task.isSuccessful()) {
                            msg = "Failed Subscription in notifications";
                        }
                        Toast.makeText(AdminHomeActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void unsubscribeFromNotifications() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}