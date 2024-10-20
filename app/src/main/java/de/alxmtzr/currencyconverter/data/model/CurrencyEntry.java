package de.alxmtzr.currencyconverter.data.model;

public class CurrencyEntry {
    public String currencyName;
    public double exchangeRate;

    public CurrencyEntry(String currencyName, double exchangeRate) {
        this.currencyName = currencyName;
        this.exchangeRate = exchangeRate;
    }
}
