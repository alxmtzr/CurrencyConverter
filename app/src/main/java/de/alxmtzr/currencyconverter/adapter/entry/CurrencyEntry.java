package de.alxmtzr.currencyconverter.adapter.entry;

public class CurrencyEntry {
    public int currencyFlag;
    public String currencyName;
    public double exchangeRate;

    public CurrencyEntry(int currencyFlag, String currencyName, double exchangeRate) {
        this.currencyFlag = currencyFlag;
        this.currencyName = currencyName;
        this.exchangeRate = exchangeRate;
    }
}
