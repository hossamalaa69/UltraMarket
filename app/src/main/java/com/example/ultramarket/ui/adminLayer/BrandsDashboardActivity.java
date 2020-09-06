package com.example.ultramarket.ui.adminLayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ultramarket.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BrandsDashboardActivity extends AppCompatActivity {

    private BrandsDashboardViewModel brandsDashboardViewModel;

    private ProgressBar progressBar;

    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brands_dashboard);

        initViews();
        initViewModel();

    }

    private void initViews() {
        progressBar = findViewById(R.id.progress_bar_brands_stats);
    }

    private void initViewModel() {
        brandsDashboardViewModel = new ViewModelProvider(this).get(BrandsDashboardViewModel.class);
        brandsDashboardViewModel.loadAllBrandsStats();
        brandsDashboardViewModel.loadAllBrandsStats().observe(this, new Observer<Map<String, Integer>>() {
            @Override
            public void onChanged(Map<String, Integer> stringIntegerMap) {
                progressBar.setVisibility(View.GONE);
                drawBarChart(stringIntegerMap);
            }
        });
    }

    private void drawBarChart(Map<String, Integer> brandsMap) {

        List<String> brandsNames = new ArrayList<>();
        brandsNames.clear();
        for (Map.Entry<String,Integer> entry : brandsMap.entrySet()) {
            brandsNames.add(entry.getKey());
        }

        barChart = findViewById(R.id.bar_chart_brands);

        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setDrawAxisLine(true);

        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisLeft().setAxisLineColor(Color.BLACK);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisRight().setDrawAxisLine(false);
        barChart.getAxisRight().setDrawLabels(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        barChart.getXAxis().setTextColor(Color.WHITE);

        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return brandsNames.get((int)value);
            }
        });

        barChart.getXAxis().setLabelRotationAngle(-60);
        barChart.setDrawBorders(false);

        barChart.setTouchEnabled(false);
        barChart.setDragEnabled(false);
        barChart.setScaleEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);

        Description description = new Description();
        description.setText("Brand");
        description.setTextColor(Color.WHITE);
        description.setTextSize(15);
        description.setTextAlign(Paint.Align.RIGHT);
        barChart.setDescription(description);


        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for(int i=0;i<brandsNames.size();i++){
            barEntries.add(new BarEntry(i,brandsMap.get(brandsNames.get(i))));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Sold Products of Brands");
        barDataSet.setColor(getResources().getColor(R.color.pink2));
        barDataSet.setGradientColor(getResources().getColor(R.color.pink2), getResources().getColor(R.color.pink1));
        BarData barData = new BarData();
        barData.addDataSet(barDataSet);
        barData.setValueTextSize(10);
        barData.setValueTextColor(Color.WHITE);
        barData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return ""+((int)(value));
            }
        });


        barChart.setData(barData);
        barChart.invalidate();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}