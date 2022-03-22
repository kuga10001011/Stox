package org.kuga.stox;

public class Trade {

    private Stock stock;
    private String action;
    private double price;
    private double quantity;

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
