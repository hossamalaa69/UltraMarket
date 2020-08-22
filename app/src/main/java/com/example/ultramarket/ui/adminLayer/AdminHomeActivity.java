package com.example.ultramarket.ui.adminLayer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultramarket.R;
import com.example.ultramarket.adapters.StatsAdapter;
import com.example.ultramarket.firebase.FirebaseAuthHelper;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

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

        if(position == 2){
            Intent i = new Intent(this, CustomersActivity.class);
            startActivity(i);
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
        descriptions.add(getString(R.string.products));

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
        Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();
    }
}