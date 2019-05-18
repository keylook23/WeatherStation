package com.zacharadamian.weatherstation;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {

    //    private static final String url = "jdbc:mysql://192.168.0.178:3306/weatherstation";
//    private static final String user = "pi";
//    private static final String pass = "zaq12wsx";
    private static final String url = "jdbc:mysql://85.10.205.173:3306/weatherstation";
    private static final String user = "weatherstationpi";
    private static final String pass = "weatherstationpi";

    Spinner spSensor, spQuantity, spUnit;
    Button btnGo;
    TextView txtData;
    ArrayAdapter<CharSequence> adapter;

    private void initView() {
        txtData = this.findViewById(R.id.txtData);
        btnGo = findViewById(R.id.btnGo);
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

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectMySql connectMySql = new ConnectMySql();
                connectMySql.execute("");
            }
        });
    }


    @SuppressLint("StaticFieldLeak")
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res = "";

//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            Toast.makeText(MainActivity.this, "Please wait...", Toast.LENGTH_SHORT)
//                    .show();
//        }

        @Override
        protected String doInBackground(String... params) {
            String sqlDate, sensorType, sensorQuantity, sensorUnit, sqlQuery;

            sensorType = spSensor.getSelectedItem().toString();
            sensorQuantity = spQuantity.getSelectedItem().toString();
            sensorUnit = spUnit.getSelectedItem().toString();

            sqlQuery = String.format("select %s from results where sensor = '%s' order by ID desc limit 1",
                    sensorQuantity, sensorType);

            sqlDate = String.format("select current_dt from results where sensor = '%s' order by ID desc limit 1",
                    sensorType);

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                StringBuilder result = new StringBuilder();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sqlQuery);
                Statement st2 = con.createStatement();
                ResultSet rs2 = st2.executeQuery(sqlDate);

                while (rs2.next() & rs.next()) {
                    result.append(rs2.getString(1)).append("\n").append(rs.getString(1)).append(sensorUnit);
                }
                res = result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            txtData.setText(result);
        }
    }
}