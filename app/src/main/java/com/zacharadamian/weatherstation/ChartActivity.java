package com.zacharadamian.weatherstation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class ChartActivity extends AppCompatActivity {
    Spinner spSensor, spQuantity, spUnit;
    ArrayAdapter<CharSequence> adapter;

    private void initView() {
        spSensor = findViewById(R.id.spSensor);
        spQuantity = findViewById(R.id.spQuantity);
        spUnit = findViewById(R.id.spUnit);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        initView();

        adapter = ArrayAdapter.createFromResource(this,
                R.array.sensor_arrays, android.R.layout.simple_dropdown_item_1line);
        spSensor.setAdapter(adapter);
        spSensor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spSensor.getSelectedItem().equals("DHT")) {
                    adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.quantity_arrays_dht, android.R.layout.simple_dropdown_item_1line);
                    spQuantity.setAdapter(adapter);
                    spQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (spQuantity.getSelectedItem().equals("temperature")) {
                                adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                                        R.array.unit_arrays_temperature, android.R.layout.simple_dropdown_item_1line);
                            } else if (spQuantity.getSelectedItem().equals("humidity")) {
                                adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                                        R.array.unit_arrays_humidity, android.R.layout.simple_dropdown_item_1line);
                            }
                            spUnit.setAdapter(adapter);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                } else if (spSensor.getSelectedItem().equals("BMP")) {
                    adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.quantity_arrays_bmp, android.R.layout.simple_dropdown_item_1line);
                    spQuantity.setAdapter(adapter);
                    spQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (spQuantity.getSelectedItem().equals("temperature")) {
                                adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                                        R.array.unit_arrays_temperature, android.R.layout.simple_dropdown_item_1line);
                            } else if (spQuantity.getSelectedItem().equals("pressure")) {
                                adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                                        R.array.unit_arrays_pressure, android.R.layout.simple_dropdown_item_1line);
                            }
                            spUnit.setAdapter(adapter);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        LineChart mChart = findViewById(R.id.linechart);

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);

        ArrayList<Entry> yValues = new ArrayList<>();

        yValues.add(new Entry(1, 20f));
        yValues.add(new Entry(2, 20f));
        yValues.add(new Entry(3, 30f));
        yValues.add(new Entry(4, 40f));
        yValues.add(new Entry(5, 50f));
        yValues.add(new Entry(6, 60f));
        yValues.add(new Entry(7, 70f));
        yValues.add(new Entry(8, 80f));

        LineDataSet set1 = new LineDataSet(yValues, "Pomiar");

        set1.setFillAlpha(110);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData data = new LineData(dataSets);

        mChart.setData(data);
    }

}
