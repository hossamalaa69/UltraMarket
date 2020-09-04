package com.example.ultramarket.ui.adminLayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.User;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashBoardActivity extends AppCompatActivity {

    private DashBoardViewModel dashBoardViewModel;

    private LineChart lineChartDownFill;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        setupLiveData();

    }

    private void setupLiveData() {
        dashBoardViewModel  = new ViewModelProvider(this).get(DashBoardViewModel.class);
        dashBoardViewModel.loadAllOrders();
        dashBoardViewModel.loadAllOrders().observe(this, new Observer<Map<String, Double>>() {
            @Override
            public void onChanged(Map<String, Double> stringDoubleMap) {
                progressBar = findViewById(R.id.progress_bar_stats);
                progressBar.setVisibility(View.GONE);
                Map<String, Double> sortedMap = sortMap(stringDoubleMap);
                drawGraph(sortedMap);
            }
        });
    }

    private void drawGraph(Map<String, Double> sortedMap) {
            List<String> list = new ArrayList<>(sortedMap.keySet());
            initLineChartDownFill(list, sortedMap);
    }

    private Map<String, Double> sortMap(Map<String, Double> oldMap){

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

        Map<String, Double> newMap = new HashMap<>();
        for (Date date : dateList) {
            String newDate = simpleDateFormat.format(date);
            newMap.put(newDate, oldMap.get(newDate));
        }

        return newMap;
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
        lineData.setValueTextSize(10);
        lineData.setValueTextColor(Color.WHITE);
        lineData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return value + "k";
            }
        });

        lineChartDownFill.setData(lineData);
        lineChartDownFill.invalidate();


    }

}