package de.alxmtzr.currencyconverter;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.alxmtzr.currencyconverter.adapter.CurrencyListAdapter;
import de.alxmtzr.currencyconverter.adapter.entry.CurrencyEntry;
import de.alxmtzr.currencyconverter.data.model.ExchangeRateDatabase;

public class MainActivity extends AppCompatActivity {
    private ExchangeRateDatabase exchangeRateDatabase;
    private Spinner spinnerFromValue;
    private Spinner spinnerToValue;
    private List<String> currencyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();
        initToolbar();
        setupSpinners();
        acceptMaxTwoDecimalPlaces();
        setupCalculateButton();
    }

    private void initializeComponents() {
        exchangeRateDatabase = new ExchangeRateDatabase();
        currencyList = new ArrayList<>(Arrays.asList(exchangeRateDatabase.getCurrencies()));
    }

    private void setupSpinners() {
        CurrencyEntry[] currencyEntries = new CurrencyEntry[currencyList.size()];

        // Populate the array with CurrencyEntry objects
        for (int i = 0; i < currencyEntries.length; i++) {
            String currentCurrency = currencyList.get(i);
            int flagImageId = getResources().getIdentifier("flag_" + currentCurrency.toLowerCase(),
                    "drawable", getPackageName());
            currencyEntries[i] = new CurrencyEntry(
                    flagImageId,
                    currentCurrency,
                    exchangeRateDatabase.getExchangeRate(currentCurrency));
        }

        CurrencyListAdapter adapter = new CurrencyListAdapter(Arrays.asList(currencyEntries));

        spinnerFromValue = findViewById(R.id.spinner_from_value);
        spinnerFromValue.setAdapter(adapter);
        spinnerToValue = findViewById(R.id.spinner_to_value);
        spinnerToValue.setAdapter(adapter);
    }

    private void setupCalculateButton() {
        FloatingActionButton fab = findViewById(R.id.button_calculate);
        fab.setOnClickListener(this::calculateConversion);
    }

    private void calculateConversion(View view) {
        CurrencyEntry currencyFromEntry = (CurrencyEntry) spinnerFromValue.getSelectedItem();
        CurrencyEntry currencyToEntry = (CurrencyEntry) spinnerToValue.getSelectedItem();
        EditText editTextValue = findViewById(R.id.edit_text_from_value);
        TextView textViewResult = findViewById(R.id.textView_calculated_value);

        if (!editTextValue.getText().toString().isEmpty()) {
            Snackbar.make(view, "Calculating...", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            double value = Double.parseDouble(editTextValue.getText().toString());
            double result = exchangeRateDatabase.convert(value, currencyFromEntry.currencyName, currencyToEntry.currencyName);
            textViewResult.setText(String.valueOf(result));
        } else {
            Snackbar.make(view, "Please enter a value you want to convert.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    private void acceptMaxTwoDecimalPlaces() {
        EditText editTextValue = findViewById(R.id.edit_text_from_value);
        editTextValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                if (input.contains(".")) {
                    String[] parts = input.split("\\.");
                    if (parts.length > 1 && parts[1].length() > 2) {
                        s.replace(0, s.length(), parts[0] + "." + parts[1].substring(0, 2));
                    }
                }
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Set the toolbar as the app bar for the activity
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
}