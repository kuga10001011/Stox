package org.kuga.stox;

public class Trade {

    private String name;
    private String action;
    private double price;
    private double quantity;

    public Trade(String name, String action, double price, double quantity) {
        this.name = name;
        this.action = action;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
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
