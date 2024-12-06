package de.alxmtzr.currencyconverter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import java.util.Arrays;

import de.alxmtzr.currencyconverter.adapter.CurrencyListAdapter;
import de.alxmtzr.currencyconverter.adapter.entry.CurrencyEntry;
import de.alxmtzr.currencyconverter.data.local.db.ExchangeRateDatabase;

public class CurrencyListActivity extends AppCompatActivity {
    private ExchangeRateDatabase exchangeRateDatabase;
    private boolean isEditingModeEnabled = false;
    private CurrencyListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list);

        initToolbar();

        // init database
        exchangeRateDatabase = new ExchangeRateDatabase();

        initializeCurrencyList();
    }

    private void initializeCurrencyList() {
        String[] currencies = exchangeRateDatabase.getCurrencies();
        CurrencyEntry[] currencyEntries = new CurrencyEntry[currencies.length];

        // Populate the array with CurrencyEntry objects
        for (int i = 0; i < currencyEntries.length; i++) {
            String currentCurrency = currencies[i];
            int flagImageId = getResources().getIdentifier("flag_" + currentCurrency.toLowerCase(),
                    "drawable", getPackageName());
            currencyEntries[i] = new CurrencyEntry(
                    flagImageId,
                    currentCurrency,
                    getExchangeRate(currentCurrency));
        }

        // Create the adapter
        adapter = new CurrencyListAdapter(Arrays.asList(currencyEntries));
        ListView listView = findViewById(R.id.currency_list_view);
        listView.setAdapter(adapter);

        // handle on item click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                CurrencyEntry currencyEntry = (CurrencyEntry) adapterView.getItemAtPosition(position);
                String currencyName = currencyEntry.currencyName;
                if (!isEditingModeEnabled) {
                    // get the capital of the country
                    String searchQuery = exchangeRateDatabase.getCapital(currencyName);
                    // navigate to maps activity
                    Intent mapIntent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("geo:0,0?q=" + searchQuery)
                    );
                    startActivity(mapIntent);
                } else {
                    // editing mode enabled -> navigate to edit currency activity
                    Intent editCurrencyIntent = new Intent(CurrencyListActivity.this, EditCurrencyActivity.class);
                    editCurrencyIntent.putExtra("currencyName", currencyName);
                    editCurrencyIntent.putExtra("exchangeRate", currencyEntry.exchangeRate);
                    editCurrencyIntent.putExtra("listPosition", position);

                    editCurrencyActivityResultLauncher.launch(editCurrencyIntent);
                }
            }
        });
    }

    private double getExchangeRate(String currentCurrency) {
        SharedPreferences prefs = getSharedPreferences("shared_prefs_rates", Context.MODE_PRIVATE);

        // return either the saved exchange rate in prefs or the default exchange rate from ExchangeRateDatabase
        double rate = Double.parseDouble(prefs.getString(currentCurrency, "0"));
        if (rate == 0) {
            rate = exchangeRateDatabase.getExchangeRate(currentCurrency);
        }
        return rate;
    }

    private final ActivityResultLauncher<Intent> editCurrencyActivityResultLauncher = registerForActivityResult(
            // give an intent to start
            new ActivityResultContracts.StartActivityForResult(),
            // get an activity result consisting of an intent
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // process the result
                    double newExchangeRate = result.getData().getDoubleExtra("newExchangeRate", -1);
                    int position = result.getData().getIntExtra("listPosition", -1);
                    SharedPreferences prefs = getSharedPreferences("shared_prefs_rates", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    if (position > -1 && newExchangeRate > -1) {
                        CurrencyEntry currencyEntry = (CurrencyEntry) adapter.getItem(position);
                        currencyEntry.exchangeRate = newExchangeRate;

                        // save the exchange rate to shared preferences
                        editor.putString(currencyEntry.currencyName, String.valueOf(newExchangeRate));
                        editor.apply();

                        // notify the adapter that the data has changed
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(
                                CurrencyListActivity.this,
                                "Error while updating the exchange rate",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_currency_list);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            Drawable backArrow = AppCompatResources.getDrawable(this, R.drawable.ic_arrow_back);
            if (backArrow != null) {
                backArrow.setTint(ContextCompat.getColor(this, R.color.md_theme_onPrimary)); // change color
                getSupportActionBar().setHomeAsUpIndicator(backArrow);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (!getOnBackPressedDispatcher().hasEnabledCallbacks()) {
            // No custom back-pressed logic present, execute default behavior
            finish(); // cancel current activity
        } else {
            // active custom logic
            getOnBackPressedDispatcher().onBackPressed();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_currency_list_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            // enable or disable editing mode
            isEditingModeEnabled = !isEditingModeEnabled;

            // Get the icon and change its color
            Drawable icon = item.getIcon();
            if (icon != null) {
                int color = isEditingModeEnabled
                        ? ContextCompat.getColor(this, R.color.ic_app_logo_background) // Color for enabled mode
                        : ContextCompat.getColor(this, R.color.md_theme_onPrimary); // Color for disabled mode
                icon.setTint(color); // Set the new color
            }

            Toast.makeText(this, getString(R.string.editing_mode) +
                    (isEditingModeEnabled ? getString(R.string.enabled) : getString(R.string.disabled)), Toast.LENGTH_SHORT).show();
            return true;
        } else {
            // the user's action was not recognized. Invoke the superclass to handle it.
            return super.onOptionsItemSelected(item);
        }
    }
}