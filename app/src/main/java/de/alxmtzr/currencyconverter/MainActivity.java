package de.alxmtzr.currencyconverter;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.alxmtzr.currencyconverter.data.model.ExchangeRateDatabase;

public class MainActivity extends AppCompatActivity {
    // currency db
    private ExchangeRateDatabase exchangeRateDatabase;

    // spinner
    private Spinner spinnerFromValue;
    private Spinner spinnerToValue;
    private List<String> currencyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        exchangeRateDatabase = new ExchangeRateDatabase();
        currencyList = new ArrayList<>(Arrays.asList(exchangeRateDatabase.getCurrencies()));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, currencyList);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.select_dialog_singlechoice_material);


        // set adapter for spinner
        spinnerFromValue = findViewById(R.id.spinner_from_value);
        spinnerFromValue.setAdapter(adapter);
        spinnerToValue = findViewById(R.id.spinner_to_value);
        spinnerToValue.setAdapter(adapter);

        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
}