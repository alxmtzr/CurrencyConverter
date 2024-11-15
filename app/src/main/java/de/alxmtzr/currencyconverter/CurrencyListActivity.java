package de.alxmtzr.currencyconverter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import java.util.Arrays;

import de.alxmtzr.currencyconverter.adapter.CurrencyListAdapter;
import de.alxmtzr.currencyconverter.adapter.entry.CurrencyEntry;
import de.alxmtzr.currencyconverter.data.model.ExchangeRateDatabase;

public class CurrencyListActivity extends AppCompatActivity {
    private ExchangeRateDatabase exchangeRateDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list);

        initToolbar();

        // init database
        exchangeRateDatabase = new ExchangeRateDatabase();

        String[] currencies = exchangeRateDatabase.getCurrencies();

        // Create an array to hold CurrencyEntry objects
        CurrencyEntry[] currencyEntries = new CurrencyEntry[currencies.length];

        // Populate the array with CurrencyEntry objects
        for (int i = 0; i < currencyEntries.length; i++) {
            String currentCurrency = currencies[i];
            int flagImageId = getResources().getIdentifier("flag_" + currentCurrency.toLowerCase(),
                    "drawable", getPackageName());
            currencyEntries[i] = new CurrencyEntry(
                    flagImageId,
                    currentCurrency,
                    exchangeRateDatabase.getExchangeRate(currentCurrency));
        }

        CurrencyListAdapter adapter = new CurrencyListAdapter(Arrays.asList(currencyEntries));

        ListView listView = findViewById(R.id.currency_list_view);
        listView.setAdapter(adapter);

        // set on item click listener for list view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CurrencyEntry currencyEntry = (CurrencyEntry) adapterView.getItemAtPosition(i);
                String currencyName = currencyEntry.currencyName;
                String searchQuery = exchangeRateDatabase.getCapital(currencyName);

                Intent mapIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("geo:0,0`?q=" + searchQuery)
                );
                startActivity(mapIntent);
            }
        });

    }

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

}