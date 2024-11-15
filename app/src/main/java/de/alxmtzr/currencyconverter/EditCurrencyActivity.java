package de.alxmtzr.currencyconverter;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import de.alxmtzr.currencyconverter.adapter.entry.CurrencyEntry;
import de.alxmtzr.currencyconverter.data.model.ExchangeRateDatabase;

public class EditCurrencyActivity extends AppCompatActivity {
    String currencyName;
    String exchangeRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_currency);

        currencyName = getIntent().getStringExtra("currencyName");
        exchangeRate = getIntent().getStringExtra("exchangeRate");

        TextView textViewCurrency = findViewById(R.id.textViewCurrency);
        textViewCurrency.setText(currencyName);

        initToolbar();
        setupButton();

    }

    private void setupButton() {
        Button fab = findViewById(R.id.buttonChangeConversionRate);
        fab.setOnClickListener(this::onButtonClicked);
    }

    private void onButtonClicked(View view) {
        EditText editTextExchangeRate = findViewById(R.id.editTextConversionRate);
        String newExchangeRate = editTextExchangeRate.getText().toString();

        ExchangeRateDatabase exchangeRateDatabase = new ExchangeRateDatabase();
        exchangeRateDatabase.setExchangeRate(currencyName, Double.parseDouble(newExchangeRate));
        Toast.makeText(this, getString(R.string.exchange_rate_updated), Toast.LENGTH_SHORT).show();
        finish();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_edit_currency);
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