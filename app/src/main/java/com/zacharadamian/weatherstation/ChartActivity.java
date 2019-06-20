package com.zacharadamian.weatherstation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChartActivity extends AppCompatActivity {
    DatabaseReference mDatabase;
    Spinner spResults;
    ArrayAdapter<CharSequence> adapter;
    Button btnGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        btnGo = findViewById(R.id.btnGo);
        spResults = findViewById(R.id.spResults);
        final LineChart mChart = findViewById(R.id.linechart);
        final ArrayList<Entry> yValues = new ArrayList<>();
        adapter = ArrayAdapter.createFromResource(this, R.array.results_arrays, android.R.layout.simple_dropdown_item_1line);
        spResults.setAdapter(adapter);

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sensorType = getIntent().getStringExtra("sensorType");
                mDatabase = FirebaseDatabase.getInstance().getReference().child("sensors").child(sensorType);
                String sensorResults = spResults.getSelectedItem().toString();
                Query last = mDatabase.orderByKey().limitToLast(Integer.parseInt(sensorResults));
                last.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        float i = 0;
                        yValues.clear();
                        String sensorQuantity = getIntent().getStringExtra("sensorQuantity");
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            i = i + 1;
                            String result = ds.child(sensorQuantity).getValue().toString();
//                            String time = ds.child("time").getValue().toString();
                            String sensorUnit = getIntent().getStringExtra("sensorUnit");
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
                            mChart.setDragEnabled(true);
                            mChart.setScaleEnabled(false);

                            yValues.add(new Entry(i, Float.parseFloat(convertResult)));


                            LineDataSet set1 = new LineDataSet(yValues, sensorQuantity+" "+sensorUnit);
                            set1.setFillAlpha(110);

                            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                            dataSets.add(set1);

                            LineData data = new LineData(dataSets);
                            mChart.setData(data);

                        }
                        mChart.invalidate();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
    }
}
