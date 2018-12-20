package org.angmarc.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NiceSpinner niceSpinner = findViewById(R.id.nice_spinner);
        List<String> dataset = new LinkedList<>(Arrays.asList("One", "Two", "Three", "Four", "Five"));
        niceSpinner.attachDataSource(dataset);

        NiceSpinner tintedSpinner = findViewById(R.id.tinted_nice_spinner);
        tintedSpinner.attachDataSource(dataset);

        NiceSpinner bottomSpinner = findViewById(R.id.bottom_nice_spinner);
        bottomSpinner.attachDataSource(dataset);
    }
}
