package com.example.basicoperations;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {
    public static final int MEAN_BLUR = 1;
    public static final int GAUSSIAN_BLUR = 2;
    public static final int MEDIAN_BLUR = 3;
    public static final int DILATION = 4;
    public static final int EROSION = 5;
    public static final int THRESHOLDING = 6;
    public static final int ADAPTIVE = 7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Button bMean = (Button)findViewById(R.id.bMean);
        Button bGaussian = (Button) findViewById(R.id.bGaussian);
        Button bMedian = (Button) findViewById(R.id.bMedian);
        Button bDilation = (Button) findViewById(R.id.bDilation);
        Button bErosion = (Button) findViewById(R.id.bErosion);
        Button bThresholding = (Button) findViewById(R.id.bThresholding);
        Button bAdaptive = (Button) findViewById(R.id.bAdaptiveThresholding);
        bMean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        MainActivity.class);
                i.putExtra("ACTION_MODE", MEAN_BLUR);
                startActivity(i);
            }
        });
        bGaussian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        MainActivity.class);
                i.putExtra("ACTION_MODE", GAUSSIAN_BLUR);
                startActivity(i);
            }
        });
        bMedian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        MainActivity.class);
                i.putExtra("ACTION_MODE", MEDIAN_BLUR);
                startActivity(i);
            }
        });
        bDilation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        MainActivity.class);
                i.putExtra("ACTION_MODE", DILATION);
                startActivity(i);
            }
        });
        bErosion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        MainActivity.class);
                i.putExtra("ACTION_MODE", EROSION);
                startActivity(i);
            }
        });
        bThresholding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        MainActivity.class);
                i.putExtra("ACTION_MODE", THRESHOLDING);
                startActivity(i);
            }
        });
        bAdaptive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        MainActivity.class);
                i.putExtra("ACTION_MODE", ADAPTIVE);
                startActivity(i);
            }
        });
    }
}
