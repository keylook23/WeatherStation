package com.zacharadamian.weatherstation;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    Spinner spSensor, spQuantity, spUnit;
    Button btnGo;
    Button btnChart;
    TextView txtData;
    ArrayAdapter<CharSequence> adapter;
    private DatabaseReference mDatabase;

    private void initView() {
        txtData = this.findViewById(R.id.txtData);
        btnGo = findViewById(R.id.btnGo);
        btnChart = findViewById(R.id.btnBarChart);
        spSensor = findViewById(R.id.spSensor);
        spQuantity = findViewById(R.id.spQuantity);
        spUnit = findViewById(R.id.spUnit);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();


        adapter = ArrayAdapter.createFromResource(this,
                R.array.sensor_arrays, android.R.layout.simple_dropdown_item_1line);
        spSensor.setAdapter(adapter);
        spSensor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spSensor.getSelectedItem().equals("dht")) {
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
                } else if (spSensor.getSelectedItem().equals("bmp")) {
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

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sensorType, sensorQuantity, sensorUnit;
                sensorType = spSensor.getSelectedItem().toString();
                sensorQuantity = spQuantity.getSelectedItem().toString();
                sensorUnit = spUnit.getSelectedItem().toString();

                mDatabase = FirebaseDatabase.getInstance().getReference().child("sensors").child(sensorType);
                Query last = mDatabase.orderByKey().limitToLast(1);
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String key = mDatabase.getKey();
                        txtData.setText(key);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


        btnChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChartActivity();
            }
        });
    }

    public void openChartActivity() {
        Intent intent = new Intent(this, ChartActivity.class);
        startActivity(intent);
    }
}