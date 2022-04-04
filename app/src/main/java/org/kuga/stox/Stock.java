package org.kuga.stox;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import io.polygon.kotlin.sdk.websocket.PolygonWebSocketMessage;

public class Stock {
    private final String name;
    private final HashMap<Long, Double> openPriceHistory;
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

    public void setOpenPrice(long time, double price) {
        openPriceHistory.put(time, price);
    }

    public Double getOpenPrice(long time) {
        return openPriceHistory.getOrDefault(time, -1.0);
    }

    public Trade getAcquisitionTrade() {
        return this.acquisitionTrade;
    }

    public Double getHeldQuantity() {
        if (acquisitionTrade.getAction().equals("SELL")) {
            return 0.0;
        }
        else {
            return acquisitionTrade.getQuantity();
        }
    }

    public void setAcquisitionTrade(Trade acquisitionTrade) {
        this.acquisitionTrade = acquisitionTrade;
    }

    public void updatePrice(PolygonWebSocketMessage.StocksMessage.Aggregate message) {
        if (message.getOpenPrice() != null && message.getEndTimestampMillis() != null) {
            setOpenPrice(message.getEndTimestampMillis(), message.getOpenPrice());
        }
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
