package de.alxmtzr.currencyconverter.data;

import android.content.SharedPreferences;
import android.widget.Spinner;

import java.util.List;

import de.alxmtzr.currencyconverter.adapter.CurrencyListAdapter;
import de.alxmtzr.currencyconverter.adapter.entry.CurrencyEntry;
import de.alxmtzr.currencyconverter.data.local.db.ExchangeRateDatabase;
import de.alxmtzr.currencyconverter.data.model.CurrencyDTO;
import de.alxmtzr.currencyconverter.data.remote.FloatRatesApi;

public class CurrencyUpdateRunnable implements Runnable {

    private final FloatRatesApi floatRatesApi;
    private final ExchangeRateDatabase exchangeRateDatabase;
    private final SharedPreferences prefs;
    private final List<String> currencyList;
    private final CurrencyListAdapter adapter;
    private final Spinner spinnerFromValue;

    public CurrencyUpdateRunnable(SharedPreferences prefs, List<String> currencyList, CurrencyListAdapter adapter, Spinner spinnerFromValue) {
        this.floatRatesApi = new FloatRatesApi();
        this.exchangeRateDatabase = new ExchangeRateDatabase();

        this.prefs = prefs;
        this.currencyList = currencyList;
        this.adapter = adapter;
        this.spinnerFromValue = spinnerFromValue;
    }

    @Override
    public void run() {
        updateCurrencies();
    }

    private synchronized void updateCurrencies() {
        // query currencies from api
        List<CurrencyDTO> currencyDTOList = floatRatesApi.queryCurrencies();

        SharedPreferences.Editor editor = prefs.edit();

        // update the exchange rates in the database
        for (CurrencyDTO entry : currencyDTOList) {
            String currencyName = entry.code.toUpperCase();
            double exchangeRate = roundToFourDecimalPlaces(entry.rate);

            // only update the exchange rate if the currency is in the list
            if (currencyList.contains(currencyName)) {
                exchangeRateDatabase.setExchangeRate(currencyName, exchangeRate);

                // save the exchange rate to shared preferences
                editor.putString(currencyName, String.valueOf(exchangeRate));
                editor.apply();
            }
        }
        updateUI();
    }

    private void updateUI() {
        // update the exchange rates in the adapter
        spinnerFromValue.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < adapter.getCount(); i++) {
                    CurrencyEntry currencyEntry = (CurrencyEntry) adapter.getItem(i);
                    String currencyName = currencyEntry.currencyName;
                    currencyEntry.exchangeRate = getExchangeRate(prefs, currencyName);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    // helper method to round a double to four decimal places
    private double roundToFourDecimalPlaces(double value) {
        return Math.round(value * 10000.0) / 10000.0;
    }

    private double getExchangeRate(SharedPreferences prefs, String currentCurrency) {
        // return either the saved exchange rate in prefs or the default exchange rate from ExchangeRateDatabase
        double rate = Double.parseDouble(prefs.getString(currentCurrency, "0"));
        if (rate == 0) {
            rate = exchangeRateDatabase.getExchangeRate(currentCurrency);
        }
        return rate;
    }
}
