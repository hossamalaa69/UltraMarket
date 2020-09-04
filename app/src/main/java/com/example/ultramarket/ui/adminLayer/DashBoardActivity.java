package com.example.ultramarket.ui.adminLayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.User;

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
                Toast.makeText(DashBoardActivity.this, "Got All Profits", Toast.LENGTH_SHORT).show();
                Map<String, Double> sortedMap = sortMap(stringDoubleMap);
                drawGraph(sortedMap);
            }
        });
    }

    private void drawGraph(Map<String, Double> sortedMap) {

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
}