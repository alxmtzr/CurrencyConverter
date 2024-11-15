package de.alxmtzr.currencyconverter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
    private boolean isEditingModeEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list);

        initToolbar();

        // init database
        exchangeRateDatabase = new ExchangeRateDatabase();

        updateCurrencyList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCurrencyList();
    }

    private void updateCurrencyList() {
        String[] currencies = exchangeRateDatabase.getCurrencies();
        CurrencyEntry[] currencyEntries = new CurrencyEntry[currencies.length];

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CurrencyEntry currencyEntry = (CurrencyEntry) adapterView.getItemAtPosition(i);
                String currencyName = currencyEntry.currencyName;
                String searchQuery = exchangeRateDatabase.getCapital(currencyName);
                if (!isEditingModeEnabled) {
                    // navigate to maps activity
                    Intent mapIntent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("geo:0,0?q=" + searchQuery)
                    );
                    startActivity(mapIntent);
                } else {
                    // navigate to edit currency activity
                    Intent editCurrencyIntent = new Intent(CurrencyListActivity.this, EditCurrencyActivity.class);
                    editCurrencyIntent.putExtra("currencyName", currencyName);
                    editCurrencyIntent.putExtra("exchangeRate", currencyEntry.exchangeRate);

                    startActivity(editCurrencyIntent);
                }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_currency_list_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            // navigate to the edit list activity
            isEditingModeEnabled = !isEditingModeEnabled;
            Toast.makeText(this, getString(R.string.editing_mode) +
                    (isEditingModeEnabled ? getString(R.string.enabled) : getString(R.string.disabled)), Toast.LENGTH_SHORT).show();
            return true;
        } else {
            // the user's action was not recognized. Invoke the superclass to handle it.
            return super.onOptionsItemSelected(item);
        }
    }
}