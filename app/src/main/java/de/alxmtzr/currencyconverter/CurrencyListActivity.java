package de.alxmtzr.currencyconverter;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;

import de.alxmtzr.currencyconverter.data.model.CurrencyEntry;
import de.alxmtzr.currencyconverter.data.model.ExchangeRateDatabase;

public class CurrencyListActivity extends AppCompatActivity {
    private ExchangeRateDatabase exchangeRateDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list);

        // init database
        exchangeRateDatabase = new ExchangeRateDatabase();

        String[] currencies = exchangeRateDatabase.getCurrencies();

        // Create an array to hold CurrencyEntry objects
        CurrencyEntry[] currencyEntries = new CurrencyEntry[currencies.length];

        // Populate the array with CurrencyEntry objects
        for (int i = 0; i < currencyEntries.length; i++) {
            String currentCurrency = currencies[i];
            currencyEntries[i] = new CurrencyEntry(
                    currentCurrency,
                    exchangeRateDatabase.getExchangeRate(currentCurrency));
        }

        CurrencyListAdapter adapter = new CurrencyListAdapter(Arrays.asList(currencyEntries));

        ListView listView = (ListView) findViewById(R.id.currency_list_view);
        listView.setAdapter(adapter);


//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
//                R.layout.list_view_item,
//                R.id.list_text_view_item,
//                currencies);
//
//        ListView listView = (ListView) findViewById(R.id.currency_list_view);
//        listView.setAdapter(adapter);

    }
}