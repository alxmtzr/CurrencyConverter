package de.alxmtzr.currencyconverter;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

public class EditCurrencyActivity extends AppCompatActivity {
    private String currencyName;
    private double exchangeRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_currency);

        currencyName = getIntent().getStringExtra("currencyName");
        exchangeRate = getIntent().getDoubleExtra("exchangeRate", -1);

        initToolbar();

        TextView editTextExchangeRate = findViewById(R.id.editTextExchangeRate);
        editTextExchangeRate.setText(String.valueOf(exchangeRate));

        // setup callback for checkmark-button
        editTextExchangeRate.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // done button on keyboard was pressed
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("newExchangeRate", Double.parseDouble(editTextExchangeRate.getText().toString()));
                    returnIntent.putExtra("listPosition", getIntent().getIntExtra("listPosition", -1));

                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
                return false;
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_edit_currency);
        toolbar.setTitle("Edit Currency Value " + currencyName);
        setSupportActionBar(toolbar);
    }
}