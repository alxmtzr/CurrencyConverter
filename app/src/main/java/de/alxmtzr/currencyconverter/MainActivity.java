package de.alxmtzr.currencyconverter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.alxmtzr.currencyconverter.adapter.CurrencyListAdapter;
import de.alxmtzr.currencyconverter.adapter.entry.CurrencyEntry;
import de.alxmtzr.currencyconverter.data.local.db.ExchangeRateDatabase;
import de.alxmtzr.currencyconverter.data.model.CurrencyDTO;
import de.alxmtzr.currencyconverter.data.remote.FloatRatesApi;

public class MainActivity extends AppCompatActivity {
    private ExchangeRateDatabase exchangeRateDatabase;
    private ProgressBar progressBar;
    private TextView textViewProgress;
    private Spinner spinnerFromValue;
    private Spinner spinnerToValue;
    private List<String> currencyList;
    CurrencyListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // temporary deactivate control mechanism
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

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

        adapter = new CurrencyListAdapter(Arrays.asList(currencyEntries));

        spinnerFromValue = findViewById(R.id.spinner_from_value);
        spinnerFromValue.setAdapter(adapter);
        spinnerToValue = findViewById(R.id.spinner_to_value);
        spinnerToValue.setAdapter(adapter);
    }

    private void setupCalculateButton() {
        Button fab = findViewById(R.id.button_calculate);
        fab.setOnClickListener(this::calculateConversion);
    }

    private void calculateConversion(View view) {
        CurrencyEntry currencyFromEntry = (CurrencyEntry) spinnerFromValue.getSelectedItem();
        CurrencyEntry currencyToEntry = (CurrencyEntry) spinnerToValue.getSelectedItem();
        EditText editTextValue = findViewById(R.id.edit_text_from_value);
        TextView textViewResult = findViewById(R.id.textView_calculated_value);

        if (!editTextValue.getText().toString().isEmpty()) {
            double value = Double.parseDouble(editTextValue.getText().toString());
            double result = exchangeRateDatabase.convert(value, currencyFromEntry.currencyName, currencyToEntry.currencyName);
            textViewResult.setText(String.valueOf(result));
        } else {
            Snackbar.make(view, R.string.please_enter_a_value_you_want_to_convert, Snackbar.LENGTH_LONG).setAction("Action", null).show();
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

        // set overflow icon color
        Drawable overflowIcon = toolbar.getOverflowIcon();
        if (overflowIcon != null) {
            overflowIcon.setTint(getResources().getColor(R.color.md_theme_onPrimary));
        }

        // Set the toolbar as the app bar for the activity
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        menu.findItem(R.id.action_share).setOnMenuItemClickListener(item -> {
            // get conversion results for sharing
            CurrencyEntry currencyFromEntry = (CurrencyEntry) spinnerFromValue.getSelectedItem();
            CurrencyEntry currencyToEntry = (CurrencyEntry) spinnerToValue.getSelectedItem();
            EditText editTextFromValue = findViewById(R.id.edit_text_from_value);
            String fromValue = editTextFromValue.getText().toString().isBlank() ? "0" : editTextFromValue.getText().toString();
            String toValue = ((TextView) findViewById(R.id.textView_calculated_value)).getText().toString();

            String sharedText = getString(R.string.currency_converter_says) +
                    fromValue + " " + currencyFromEntry.currencyName +
                    getString(R.string.are) +
                    toValue + " " + currencyToEntry.currencyName;

            // share the conversion results
            shareText(sharedText);
            return true;
        });


        return true;
    }

    private void shareText(String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);

        startActivity(Intent.createChooser(shareIntent, "Share using"));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_currency_list) {
            // navigate to the currency list activity
            Intent currencyIntent = new Intent(this, CurrencyListActivity.class);
            startActivity(currencyIntent);
            return true;
        } else if (item.getItemId() == R.id.action_refresh_rates) {
            // refresh exchange rates from the API
            updateCurrencies();
            return true;
        } else {
            // the user's action was not recognized. Invoke the superclass to handle it.
            return super.onOptionsItemSelected(item);
        }
    }

    private void updateCurrencies() {
        Toast.makeText(this, R.string.updating_rates, Toast.LENGTH_SHORT).show();
        FloatRatesApi floatRatesApi = new FloatRatesApi();
        List<CurrencyDTO> currencyDTOList;
        try {
            currencyDTOList = floatRatesApi.queryCurrencies();
        } catch (RuntimeException e) {
            Toast.makeText(this, R.string.error_while_updating_data, Toast.LENGTH_SHORT).show();
            return;
        }

        // update the exchange rates in the database
        for (CurrencyDTO entry : currencyDTOList) {
            String currencyName = entry.code.toUpperCase();
            double exchangeRate = roundToFourDecimalPlaces(entry.rate);

            // only update the exchange rate if the currency is in the list
            if (currencyList.contains(currencyName)) {
                exchangeRateDatabase.setExchangeRate(currencyName, exchangeRate);
            }
        }

        // update the exchange rates in the adapter
        for (int i = 0; i < adapter.getCount(); i++) {
            CurrencyEntry currencyEntry = (CurrencyEntry) adapter.getItem(i);
            String currencyName = currencyEntry.currencyName;
            currencyEntry.exchangeRate = exchangeRateDatabase.getExchangeRate(currencyName);
        }
        adapter.notifyDataSetChanged();
        Toast.makeText(this, R.string.rates_updated, Toast.LENGTH_SHORT).show();
    }

    // helper method to round a double to four decimal places
    private double roundToFourDecimalPlaces(double value) {
        return Math.round(value * 10000.0) / 10000.0;
    }
}