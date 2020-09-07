package com.example.ultramarket.ui.adminLayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ultramarket.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DashBoardActivity extends AppCompatActivity {

    private DashBoardViewModel dashBoardViewModel;

    private LineChart lineChartDownFill;

    private ProgressBar progressBar;

    private TextView totalProfits;

    private TextView todayProfits;

    private TextView bestProdName;

    private ImageView bestProdImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        initViews();
        setupLiveData();

    }

    private void initViews() {
        progressBar = findViewById(R.id.progress_bar_stats);
        todayProfits = (TextView) findViewById(R.id.stats_today_profits);
        totalProfits = (TextView) findViewById(R.id.stats_total_profts);
        bestProdName = (TextView) findViewById(R.id.stats_best_selling);
        bestProdImg = (ImageView) findViewById(R.id.image_best_prod);
    }

    private void setupLiveData() {
        dashBoardViewModel  = new ViewModelProvider(this).get(DashBoardViewModel.class);
        dashBoardViewModel.loadAllOrders();
        dashBoardViewModel.loadAllOrders().observe(this, new Observer<Map<String, Double>>() {
            @Override
            public void onChanged(Map<String, Double> stringDoubleMap) {
                List<String> sortedDates = getSortedDates(stringDoubleMap);
                setTodayTotalProfits(stringDoubleMap);
                initLineChartDownFill(sortedDates, stringDoubleMap);
            }
        });

        dashBoardViewModel.loadBestProduct();
        dashBoardViewModel.loadBestProduct().observe(this, new Observer<Map<String, String>>() {
            @Override
            public void onChanged(Map<String,String> map) {
                progressBar.setVisibility(View.GONE);
                for(Map.Entry<String, String> entry:map.entrySet()){
                    bestProdName.setText(entry.getKey());
                    Glide.with(DashBoardActivity.this)
                            .load(entry.getValue())
                            .placeholder(R.drawable.ic_products)
                            .into(bestProdImg);
                }
            }
        });
    }

    private void setTodayTotalProfits(Map<String, Double> sortedMap) {
        double total_profits = 0.00;
        double today_profits = 0.00;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String today_date = simpleDateFormat.format(new Date().getTime());

        for (Map.Entry<String,Double> entry : sortedMap.entrySet()) {
            total_profits += entry.getValue();
            if(entry.getKey().equals(today_date))
                today_profits = entry.getValue();
        }

        total_profits = BigDecimal.valueOf(total_profits)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        today_profits = BigDecimal.valueOf(today_profits)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        todayProfits.setText("" + today_profits + " LE");
        totalProfits.setText("" + total_profits + " LE");
    }


    private List<String> getSortedDates(Map<String, Double> oldMap){

        List<String> stringList = new ArrayList<>(oldMap.keySet());
        ArrayList<Date> dateList = new ArrayList<Date>();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        for (String dateString : stringList) {
            try {
                dateList.add(simpleDateFormat.parse(dateString));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(dateList);
        List<String> newDates = new ArrayList<>();
        newDates.clear();
        for (Date date : dateList) {
            String newDate = simpleDateFormat.format(date);
            newDates.add(newDate);
        }
        return newDates;
    }

    private void initLineChartDownFill(List<String> dates, Map<String, Double> map) {

        lineChartDownFill = findViewById(R.id.graph_chart);

        lineChartDownFill.getXAxis().setDrawGridLines(false);
        lineChartDownFill.getXAxis().setDrawAxisLine(true);

        lineChartDownFill.getAxisLeft().setDrawGridLines(false);
        lineChartDownFill.getAxisLeft().setAxisLineColor(Color.BLACK);
        lineChartDownFill.getAxisRight().setDrawGridLines(false);
        lineChartDownFill.getAxisRight().setDrawAxisLine(false);
        lineChartDownFill.getAxisRight().setDrawLabels(false);
        lineChartDownFill.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        lineChartDownFill.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.1f", value)+"k";
            }
        });
        lineChartDownFill.getXAxis().setTextColor(Color.WHITE);

        lineChartDownFill.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return dates.get((int)value);
            }
        });

        lineChartDownFill.getXAxis().setLabelRotationAngle(-60);
        lineChartDownFill.getXAxis().setLabelCount(dates.size(), true);
        lineChartDownFill.setDrawBorders(false);
        lineChartDownFill.setTouchEnabled(false);
        lineChartDownFill.setDragEnabled(false);
        lineChartDownFill.setScaleEnabled(false);
        lineChartDownFill.setPinchZoom(false);
        lineChartDownFill.setDrawGridBackground(false);
        lineChartDownFill.setMaxHighlightDistance(200);


        Description description = new Description();
        description.setText("Date");
        description.setTextColor(Color.WHITE);
        description.setTextSize(15);
        description.setTextAlign(Paint.Align.RIGHT);
        lineChartDownFill.setDescription(description);

        ArrayList<Entry> entryArrayList = new ArrayList<>();
        for(int i=0;i<dates.size();i++){
            double yValue = map.get(dates.get(i))/1000.0;
            double approxYVal = BigDecimal.valueOf(yValue)
                    .setScale(1, RoundingMode.HALF_UP)
                    .doubleValue();
            entryArrayList.add(new Entry(i, (float)approxYVal));
        }

        //LineDataSet is the line on the graph
        LineDataSet lineDataSet = new LineDataSet(entryArrayList, "Total Profits");

        lineDataSet.setLineWidth(0f);
        lineDataSet.setColor(Color.GRAY);
        lineDataSet.setHighLightColor(Color.RED);
        lineDataSet.setDrawValues(true);
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
        lineDataSet.setColor(getResources().getColor(R.color.pink2));
        //set the gradiant then the above draw fill color will be replace
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.gradient_pink_background);
        lineDataSet.setFillDrawable(drawable);


        //to remove the cricle from the graph
        lineDataSet.setDrawCircles(true);

        //lineDataSet.setColor(ColorTemplate.COLORFUL_COLORS);


        ArrayList<ILineDataSet> iLineDataSetArrayList = new ArrayList<>();
        iLineDataSetArrayList.add(lineDataSet);

        //LineData is the data accord
        LineData lineData = new LineData(iLineDataSetArrayList);
        lineData.setValueTextSize(10);
        lineData.setValueTextColor(Color.WHITE);
        lineData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return value + "k LE";
            }
        });

        lineChartDownFill.setData(lineData);
        lineChartDownFill.invalidate();


    }

    public void openBrandsDashboard(View view) {
        Intent i = new Intent(this, BrandsDashboardActivity.class);
        startActivity(i);
    }

    public void openCategoriesDashboard(View view) {
        Intent i = new Intent(this, CategoriesDashboardActivity.class);
        startActivity(i);
    }
}