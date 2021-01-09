package com.munifrog.design.tetheredtenttriangulator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spin = findViewById(R.id.sp_models);
        String [] array = getResources().getStringArray(R.array.tent_models);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                R.layout.text_spinner,
                array
        );
        arrayAdapter.setDropDownViewResource(R.layout.text_spinner);
        spin.setAdapter(arrayAdapter);
    }
}