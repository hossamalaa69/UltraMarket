package com.example.ultramarket.ui.adminLayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ultramarket.R;

public class ProductActivity extends AppCompatActivity  {

    private Spinner spinner_currency;
    private Spinner spinner_unit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        setupSpinners();
    }

    private void setupSpinners() {
        spinner_currency = (Spinner) findViewById(R.id.currency_spinner);
        spinner_unit = (Spinner) findViewById(R.id.unit_spinner);
        //spinner.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.currency_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.units_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_currency.setAdapter(adapter1);
        spinner_unit.setAdapter(adapter2);
    }

    public void uploadFromGallery(View view) {
    }

    public void saveProduct(View view) {
        Toast.makeText(this, "" + spinner_currency.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "" + spinner_unit.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

    }

}