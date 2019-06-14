package com.zacharadamian.weatherstation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
    Spinner spSensor;
    Spinner spQuantity;
    static Spinner spUnit;
    Button btnGo, btnChart;
    static TextView txtData;
    TextView txtTime;
    ArrayAdapter<CharSequence> adapter;
    private DatabaseReference mDatabase;


    public void initView() {
        txtData = this.findViewById(R.id.txtData);
        txtTime = this.findViewById(R.id.txtTime);
        btnGo = findViewById(R.id.btnGo);
        btnChart = findViewById(R.id.btnChart);
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
                last.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String result = ds.child(sensorQuantity).getValue().toString();
                            String time = ds.child("time").getValue().toString();
                            String convert = null;
//                            convert();

                            if (spUnit.getSelectedItem().equals("°C")) {
                                double convertResult = Double.parseDouble(result);
                                convert = String.valueOf(convertResult);
                            } else if (spUnit.getSelectedItem().equals("°F")) {
                                double convertResult = Double.parseDouble(result) * 9 / 5 + 32;
                                convert = String.valueOf(convertResult);
                            } else if (spUnit.getSelectedItem().equals("K")) {
                                double convertResult = Double.parseDouble(result) + 273.15;
                                convert = String.valueOf(convertResult);
                            } else if (spUnit.getSelectedItem().equals("Pa")) {
                                double convertResult = Double.parseDouble(result);
                                convert = String.valueOf(convertResult);
                            } else if (spUnit.getSelectedItem().equals("hPa")) {
                                double convertResult = Double.parseDouble(result) / 100;
                                convert = String.valueOf(convertResult);
                            } else if (spUnit.getSelectedItem().equals("Psi")) {
                                double convertResult = Double.parseDouble(result) / 6894.75729;
                                convert = String.valueOf(convertResult);
                            } else if (spUnit.getSelectedItem().equals("%")) {
                                double convertResult = Double.parseDouble(result);
                                convert = String.valueOf(convertResult);
                            }
                            txtData.setText(convert + " " + sensorUnit);
                            txtTime.setText(time);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
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
    }

//    public void convert() {
//        if (spUnit.getSelectedItem().equals("°C")) {
//            double convertResult = Double.parseDouble(result);
//            convert = String.valueOf(convertResult);
//        } else if (spUnit.getSelectedItem().equals("°F")) {
//            double convertResult = Double.parseDouble(result) * 9 / 5 + 32;
//            convert = String.valueOf(convertResult);
//        } else if (spUnit.getSelectedItem().equals("K")) {
//            double convertResult = Double.parseDouble(result) + 273.15;
//            convert = String.valueOf(convertResult);
//        } else if (spUnit.getSelectedItem().equals("Pa")) {
//            double convertResult = Double.parseDouble(result);
//            convert = String.valueOf(convertResult);
//        } else if (spUnit.getSelectedItem().equals("hPa")) {
//            double convertResult = Double.parseDouble(result) / 100;
//            convert = String.valueOf(convertResult);
//        } else if (spUnit.getSelectedItem().equals("Psi")) {
//            double convertResult = Double.parseDouble(result) / 6894.75729;
//            convert = String.valueOf(convertResult);
//        } else if (spUnit.getSelectedItem().equals("%")) {
//            double convertResult = Double.parseDouble(result);
//            convert = String.valueOf(convertResult);
//        }
//    }

    public void openChartActivity() {
        String sensorType = spSensor.getSelectedItem().toString();
        String sensorQuantity = spQuantity.getSelectedItem().toString();
        String sensorUnit = spUnit.getSelectedItem().toString();
        Intent intent = new Intent(this, ChartActivity.class);
        intent.putExtra("sensorType", sensorType);
        intent.putExtra("sensorQuantity", sensorQuantity);
        intent.putExtra("sensorUnit", sensorUnit);
        startActivity(intent);
    }
}