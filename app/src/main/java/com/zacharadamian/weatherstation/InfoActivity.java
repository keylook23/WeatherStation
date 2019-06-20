package com.zacharadamian.weatherstation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InfoActivity extends AppCompatActivity {
    TextView txtInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        txtInfo = this.findViewById(R.id.txtInfo);
        StringBuffer stringBuffer = new StringBuffer();
        InputStream is = this.getResources().openRawResource(R.raw.info);
        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(is));
        try {
            String data;
            while ((data = bufferedreader.readLine()) != null) {
                stringBuffer.append(data);
            }
            txtInfo.setText(stringBuffer);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}