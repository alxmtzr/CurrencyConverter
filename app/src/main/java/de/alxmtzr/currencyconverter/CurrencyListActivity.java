package de.alxmtzr.currencyconverter;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CurrencyListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list);

        String[] months = { "January", "February", "March", "April",
                "May", "June", "July", "August",
                "September", "October", "November", "December"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.list_view_item,
                R.id.list_text_view_item,
                months);

        ListView listView = (ListView) findViewById(R.id.currency_list_view);
        listView.setAdapter(adapter);

    }
}