package de.alxmtzr.currencyconverter.data.remote;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.alxmtzr.currencyconverter.data.model.CurrencyDTO;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FloatRatesApi {
    private final OkHttpClient client = new OkHttpClient();

    public List<CurrencyDTO> queryCurrencies() {
        String queryString = "https://www.floatrates.com/daily/eur.json";
        List<CurrencyDTO> currencies = new ArrayList<>();

        try {
            Request request = new Request.Builder().url(queryString).build();
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            JSONObject root = new JSONObject(responseBody);

            // map JSON data to CurrencyDTO objects
            Iterator<String> keys = root.keys();

            while (keys.hasNext()) {
                String key = keys.next(); // currency code like "aud", "usd" ..
                // get currency object of the current currency code
                JSONObject currencyJson = root.getJSONObject(key);
                // create a CurrencyDTO object from the JSON data
                CurrencyDTO currency = new CurrencyDTO(
                        currencyJson.get("code").toString(),
                        currencyJson.get("alphaCode").toString(),
                        currencyJson.get("numericCode").toString(),
                        currencyJson.get("name").toString(),
                        currencyJson.getDouble("rate"),
                        currencyJson.get("date").toString(),
                        currencyJson.getDouble("inverseRate")
                );
                currencies.add(currency);
            }

        } catch (IOException exception) {
            Log.e("FloatRatesApi", "Error while fetching data from FloatRates API", exception);
            throw new RuntimeException("Error while fetching data from FloatRates API", exception);
        } catch (JSONException exception) {
            Log.e("FloatRatesApi", "Error while parsing JSON data", exception);
        }
        return currencies;
    }
}
