package de.alxmtzr.currencyconverter.data.model;

public class CurrencyDTO {
    public final String code;
    public final String alphaCode;
    public final String numericCode;
    public final String name;
    public final double rate;
    public final String date;
    public final double inverseRate;

    public CurrencyDTO(String code, String alphaCode, String numericCode, String name, double rate, String date, double inverseRate) {
        this.code = code;
        this.alphaCode = alphaCode;
        this.numericCode = numericCode;
        this.name = name;
        this.rate = rate;
        this.date = date;
        this.inverseRate = inverseRate;
    }

    @Override
    public String toString() {
        return name + " (" + code + "), rate: " + rate;
    }
}
