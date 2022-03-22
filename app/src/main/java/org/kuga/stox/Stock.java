package org.kuga.stox;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class Stock {
    private final String name;
    private final HashMap<Calendar, Double> openPriceHistory;
    private Trade acquisitionTrade;

    public Stock(String name) {
        this.name = name;
        this.openPriceHistory = new HashMap<>();
    }

    public Stock(String name, Trade purchase) {
        this(name);
        this.acquisitionTrade = purchase;
    }

    public String getName() {
        return name;
    }

    public Double getOpenPrice(Calendar time) {
        return openPriceHistory.getOrDefault(time, -1.0);
    }

    public Trade getAcquisitionTrade() {
        return this.acquisitionTrade;
    }

    public void setAcquisitionTrade(Trade acquisitionTrade) {
        this.acquisitionTrade = acquisitionTrade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return name.equals(stock.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
