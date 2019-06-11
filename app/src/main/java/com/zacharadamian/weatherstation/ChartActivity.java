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

        mDatabase = FirebaseDatabase.getInstance().getReference().child("sensors").child("bmp");
        Query last = mDatabase.orderByKey().limitToFirst(5);
        last.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Entry> yValues = new ArrayList<>();
                float i=0;
                yValues.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    i=i+1;
                    String result = ds.child("pressure").getValue().toString();
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