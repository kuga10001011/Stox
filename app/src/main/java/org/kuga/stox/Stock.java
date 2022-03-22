package org.kuga.stox;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class Stock {
    private final String name;
    private final HashMap<Calendar, Double> openPriceHistory;
    private double purchasePrice;

    public Stock(String name) {
        this.name = name;
        this.openPriceHistory = new HashMap<>();
    }

    public Stock(String name, double purchasePrice) {
        this(name);
        this.purchasePrice = purchasePrice;
    }

    public String getName() {
        return name;
    }

    public Double getOpenPrice(Calendar time) {
        return openPriceHistory.getOrDefault(time, -1.0);
    }

    public double getPurchasePrice() {
        return purchasePrice;
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
