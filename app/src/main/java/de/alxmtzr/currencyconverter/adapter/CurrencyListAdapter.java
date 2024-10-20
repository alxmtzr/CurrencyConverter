package de.alxmtzr.currencyconverter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import de.alxmtzr.currencyconverter.R;
import de.alxmtzr.currencyconverter.adapter.entry.CurrencyEntry;

public class CurrencyListAdapter extends BaseAdapter {
    private List<CurrencyEntry> data;

    public CurrencyListAdapter(List<CurrencyEntry> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * This method is called by the ListView to get the view for each item in the list.
     * @param i The position of the item in the list
     * @param view The view to be reused
     * @param viewGroup The parent view group
     * @return The view for the item at position i
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        /*
            * Gets data element at position i
            * Creates view, reuses existing view when provided to the method
            * Fills view with data
            * Text in TextView currency
            * Text in TextView exchangeRate
         */
        Context context = viewGroup.getContext();
        CurrencyEntry entry = data.get(i);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_view_item, null, false);
        }

        // get text view showing currency name and set its text to current data currency name
        TextView currencyNameTextView = (TextView) view.findViewById(R.id.list_text_view_item_currency);
        currencyNameTextView.setText(entry.currencyName);

        // get text view showing exchange reate and set its text to current data exchange rate
        TextView exchangeRateTextView = (TextView) view.findViewById(R.id.list_text_view_item_exchange_rate);
        // convert double to string
        String exchangeRateStr = String.valueOf(entry.exchangeRate);
        exchangeRateTextView.setText(exchangeRateStr);

        return view;
    }
}
