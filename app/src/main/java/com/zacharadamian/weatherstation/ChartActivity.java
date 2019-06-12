package com.zacharadamian.weatherstation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        String sensorType = getIntent().getStringExtra("sensorType");
        final String sensorQuantity = getIntent().getStringExtra("sensorQuantity");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("sensors").child(sensorType);
        Query last = mDatabase.orderByKey().limitToLast(5);
        last.addValueEventListener(new ValueEventListener() {
            ArrayList<Entry> yValues = new ArrayList<>();

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                float i = 0;
                yValues.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    i = i + 1;
                    String result = ds.child(sensorQuantity).getValue().toString();
                    String time = ds.child("time").getValue().toString();

                    LineChart mChart = findViewById(R.id.linechart);

                    mChart.setDragEnabled(true);
                    mChart.setScaleEnabled(false);

                    yValues.add(new Entry(i, Float.parseFloat(result)));

                    LineDataSet set1 = new LineDataSet(yValues, "Pomiar");
                    set1.setFillAlpha(110);
                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(set1);
                    LineData data = new LineData(dataSets);
                    mChart.setData(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}