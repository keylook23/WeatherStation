package com.zacharadamian.weatherstation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    Spinner spSensor;
    Spinner spQuantity;
    Spinner spUnit;
    Button btnGo, btnChart;
    ImageButton btnInfo;
    TextView txtData;
    TextView txtTime;
    ArrayAdapter<CharSequence> adapter;
    private DatabaseReference mDatabase;

    public void initView() {
        txtData = this.findViewById(R.id.txtData);
        txtTime = this.findViewById(R.id.txtTime);
        btnGo = findViewById(R.id.btnGo);
        btnChart = findViewById(R.id.btnChart);
        btnInfo = findViewById(R.id.btnInfo);
        spSensor = findViewById(R.id.spSensor);
        spQuantity = findViewById(R.id.spQuantity);
        spUnit = findViewById(R.id.spUnit);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
                            if (spQuantity.getSelectedItemPosition() == 0) {
                                adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                                        R.array.unit_arrays_temperature, android.R.layout.simple_dropdown_item_1line);
                            } else if (spQuantity.getSelectedItemPosition() == 1) {
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
                            if (spQuantity.getSelectedItemPosition() == 0) {
                                adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                                        R.array.unit_arrays_temperature, android.R.layout.simple_dropdown_item_1line);
                            } else if (spQuantity.getSelectedItemPosition() == 1) {
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

                final String sensorType;
                final String[] sensorQuantity = new String[1];
                final String sensorUnit;

                sensorType = spSensor.getSelectedItem().toString();
                sensorUnit = spUnit.getSelectedItem().toString();

                mDatabase = FirebaseDatabase.getInstance().getReference().child("sensors").child(sensorType);
                Query last = mDatabase.orderByKey().limitToLast(1);
                last.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (spQuantity.getSelectedItemPosition() == 0){
                            sensorQuantity[0] = "temperature";
                        } else if  (spSensor.getSelectedItemPosition() == 0 && spQuantity.getSelectedItemPosition() == 1){
                            sensorQuantity[0] = "humidity";
                        } else {
                            sensorQuantity[0] = "pressure";
                        }

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String result = ds.child(sensorQuantity[0]).getValue().toString();
                            String time = ds.child("time").getValue().toString();
                            double convert;
                            String convertResult;

                            switch (sensorUnit) {
                                case "°F":
                                    convert = Double.parseDouble(result) * 9 / 5 + 32;
                                    convertResult = String.valueOf(convert);
                                    break;
                                case "K":
                                    convert = Double.parseDouble(result) + 273.15;
                                    convertResult = String.valueOf(convert);
                                    break;
                                case "hPa":
                                    convert = Double.parseDouble(result) / 100;
                                    convertResult = String.valueOf(convert);
                                    break;
                                case "Psi":
                                    convert = Double.parseDouble(result) / 6894.75729;
                                    convertResult = String.valueOf(convert);
                                    break;
                                default:
                                    convert = Double.parseDouble(result);
                                    convertResult = String.valueOf(convert);
                                    break;
                            }
                            txtData.setText(convertResult + " " + sensorUnit);
                            txtTime.setText(time);
                        }
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        txtData.setText("error");
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

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInfoActivity();
            }
        });
    }

    public void openChartActivity() {
        String[] sensorQuantity = new String[1];
        if (spQuantity.getSelectedItemPosition() == 0){
            sensorQuantity[0] = "temperature";
        } else if  (spSensor.getSelectedItemPosition() == 0 && spQuantity.getSelectedItemPosition() == 1){
            sensorQuantity[0] = "humidity";
        } else {
            sensorQuantity[0] = "pressure";
        }
        String sensorType = spSensor.getSelectedItem().toString();
        String sensorUnit = spUnit.getSelectedItem().toString();
        Intent intent = new Intent(this, ChartActivity.class);
        intent.putExtra("sensorType", sensorType);
        intent.putExtra("sensorQuantity", sensorQuantity[0]);
        intent.putExtra("sensorUnit", sensorUnit);
        startActivity(intent);
    }

    public void openInfoActivity() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }
}