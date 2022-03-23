package org.kuga.stox;

public class Trade {

    private final Stock stock;
    private final String action;
    private final double price;
    private final double quantity;

    public Trade(Stock stock, String action, double price, double quantity) {
        this.stock = stock;
        this.action = action;
        this.price = price;
        this.quantity = quantity;
        this.stock.setAcquisitionTrade(this);
    }

    public String getName() {
        return stock.getName();
    }

    public String getAction() {
        return action;
    }

    public double getPrice() {
        return price;
    }

    public double getQuantity() {
        return quantity;
    }
}
